package se.spaced.shared.world.walkmesh;

import com.ardor3d.math.Matrix4;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import se.ardortech.math.Rotations;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.Vectors;
import se.fearless.common.io.FileLocator;
import se.fearless.common.io.IOLocator;
import se.spaced.shared.model.xmo.XmoLoader;
import se.spaced.shared.model.xmo.XmoRoot;
import se.spaced.shared.util.cache.CacheManager;
import se.spaced.shared.world.AreaPoint;
import se.spaced.shared.world.area.Polygon;
import se.spaced.shared.xml.SharedXStreamRegistry;
import se.spaced.shared.xml.XStreamIO;
import se.spaced.shared.xml.XmlIOException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

public class LocalSpaceConverter {

	public static final LocalSpaceConverter NULL_CONVERTER = new LocalSpaceConverter(SpacedVector3.ZERO,
			SpacedRotation.IDENTITY);

	public static void main(String[] args) throws XmlIOException, IOException {
		File rootDirectory = new File("shared/resources");
		JFileChooser fileChooser = new JFileChooser(rootDirectory);
		fileChooser.setFileFilter(new FileNameExtensionFilter("Xmo files", "xmo"));
		int choice = fileChooser.showOpenDialog(null);
		if (choice == JFileChooser.CANCEL_OPTION) {
			return;
		}
		XStream xStream = new XStream(new DomDriver());
		xStream.setMode(XStream.NO_REFERENCES);
		SharedXStreamRegistry xStreamRegistry = new SharedXStreamRegistry();
		xStreamRegistry.registerDefaultsOn(xStream);

		File selectedFile = fileChooser.getSelectedFile();
		IOLocator ioLocator = new FileLocator(rootDirectory);
		XmoLoader xmoLoader = new XmoLoader(new XStreamIO(xStream, ioLocator), new CacheManager());
		String path = selectedFile.getAbsolutePath();
		Splitter splitter = Splitter.on("resources").trimResults();
		Iterable<String> split = splitter.split(path);
		XmoRoot xmoRoot = xmoLoader.loadXmo(Iterables.get(split, 1));
		Walkmesh walkmesh = (Walkmesh) xStream.fromXML(ioLocator.getByteSource(xmoRoot.getWalkmeshFile()).openBufferedStream());

		// TODO: change from these hard coded values (used to convert the fearless ship walkmesh)
		LocalSpaceConverter localSpaceConverter = new LocalSpaceConverter(new SpacedVector3(0, -20, 0),
				new SpacedRotation(2.4920011842825406E-4, 0.30648853373904417, 8.023851233349423E-5, 0.9518743142598046));
		Walkmesh localizedWalkmesh = localSpaceConverter.convert(walkmesh);

		String xml = xStream.toXML(localizedWalkmesh);
		System.out.println(xml);
	}

	private final SpacedVector3 translation;
	private final SpacedRotation rotation;
	private final Transform transform;
	private final Transform inversTransform;

	public LocalSpaceConverter(SpacedVector3 translation, SpacedRotation rotation) {
		this.translation = translation;
		this.rotation = rotation;
		transform = buildTransform(translation, rotation);
		Matrix4 homogeneousMatrix = transform.getHomogeneousMatrix(null);
		homogeneousMatrix.invertLocal();

		inversTransform = new Transform();
		inversTransform.fromHomogeneousMatrix(homogeneousMatrix);
	}

	public SpacedVector3 getTranslation() {
		return translation;
	}

	public SpacedRotation getRotation() {
		return rotation;
	}

	public Walkmesh convert(Walkmesh walkmesh) {
		return transformWalkmesh(walkmesh, transform);
	}

	private Walkmesh transformWalkmesh(Walkmesh walkmesh, Transform usingTransform) {
		Walkmesh localSpaceWalkmesh = new Walkmesh();
		for (Polygon polygon : walkmesh.getPolygons()) {
			Polygon localPolygon = new Polygon(polygon.getId());
			for (SpacedVector3 point : polygon.getPoints()) {
				SpacedVector3 localSpacedPoint = convert(point, usingTransform);
				localPolygon.add(localSpacedPoint);
			}
			localSpaceWalkmesh.addPolygon(localPolygon);
		}

		for (WalkmeshConnection connection : walkmesh.getConnections()) {
			SpacedVector3 point1 = convert(connection.getPoint1(), usingTransform);
			SpacedVector3 point2 = convert(connection.getPoint2(), usingTransform);
			WalkmeshConnection localConnection = new WalkmeshConnection(connection.getFrom(), connection.getTo(),
					point1, point2, connection.getDirection());
			localSpaceWalkmesh.addConnection(localConnection);
		}
		return localSpaceWalkmesh;
	}

	private static Transform buildTransform(SpacedVector3 translation, SpacedRotation rotation) {
		Transform translationTransform = new Transform();
		translationTransform.setTranslation(translation);
		Transform rotTransform = new Transform();
		rotTransform.setTranslation(0, 0, 0);
		rotTransform.setRotation(Rotations.fromSpaced(rotation));
		Transform transform = new Transform();
		translationTransform.multiply(rotTransform, transform);
		return transform;
	}

	public SpacedVector3 convert(SpacedVector3 point, Transform usingTransform) {
		Vector3 store = new Vector3();
		usingTransform.applyInverse(point, store);
		return Vectors.fromArdor(store);
	}

	public SpacedRotation convert(SpacedRotation rot) {
		return rotation.applyInverseTo(rot);
	}

	public AreaPoint convert(AreaPoint areaPoint) {
		return convertAreaPoint(areaPoint, transform);
	}

	private AreaPoint convertAreaPoint(AreaPoint areaPoint, Transform usingTransform) {
		SpacedVector3 point = convert(areaPoint.getPoint(), usingTransform);
		SpacedRotation rot = convert(areaPoint.getRotation());
		return new AreaPoint(point, rot);
	}

	public Walkmesh inverse(Walkmesh walkmesh) {
		return transformWalkmesh(walkmesh, inversTransform);
	}

	public AreaPoint inverse(AreaPoint areaPoint) {
		return convertAreaPoint(areaPoint, inversTransform);
	}
}
