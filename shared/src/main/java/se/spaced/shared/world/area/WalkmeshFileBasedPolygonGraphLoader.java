package se.spaced.shared.world.area;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.shared.world.walkmesh.Walkmesh;
import se.spaced.shared.xml.XStreamIO;
import se.spaced.shared.xml.XmlIOException;

@Singleton
public class WalkmeshFileBasedPolygonGraphLoader implements PolygonGraphLoader {
	private final XStreamIO xStreamIO;

	@Inject
	public WalkmeshFileBasedPolygonGraphLoader(XStreamIO xStreamIO) {
		this.xStreamIO = xStreamIO;
	}

	@Override
	public PolygonGraph loadPolygonGraph(final String path) throws XmlIOException {
		PolygonGraph polygonGraph = new PolygonGraph();
		Walkmesh walkmesh = xStreamIO.load(Walkmesh.class, path);
		walkmesh.addToPolygonGraph(SpacedVector3.ZERO, SpacedRotation.IDENTITY, polygonGraph);
		return polygonGraph;
	}
}
