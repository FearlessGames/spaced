package se.spaced.shared.xml;

import com.thoughtworks.xstream.XStream;
import se.ardortech.math.AABox;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.Sphere;
import se.spaced.shared.model.aura.ModStat;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.model.xmo.AttachmentPointIdentifier;
import se.spaced.shared.model.xmo.Blending;
import se.spaced.shared.model.xmo.Material;
import se.spaced.shared.model.xmo.XmoAttachmentPoint;
import se.spaced.shared.model.xmo.XmoMetaNode;
import se.spaced.shared.world.area.Cube;
import se.spaced.shared.world.area.MultiplePoints;
import se.spaced.shared.world.area.Path;
import se.spaced.shared.world.area.Polygon;
import se.spaced.shared.world.area.SinglePoint;
import se.spaced.shared.world.walkmesh.Walkmesh;
import se.spaced.shared.world.walkmesh.WalkmeshConnection;

public class SharedXStreamRegistry {
	public void registerDefaultsOn(XStream xStream) {

		xStream.registerConverter(new Vector3Converter());
		xStream.registerConverter(new QuaternionConverter());
		xStream.registerConverter(new UUIDConverter());


		xStream.processAnnotations(Sphere.class);
		xStream.processAnnotations(AABox.class);
		xStream.processAnnotations(XmoMetaNode.class);
		xStream.processAnnotations(XmoAttachmentPoint.class);
		xStream.processAnnotations(AttachmentPointIdentifier.class);
		xStream.processAnnotations(Material.class);
		xStream.processAnnotations(Blending.class);


		xStream.alias("itemType", ItemType.class);
		xStream.alias("vector3", SpacedVector3.class);
		xStream.alias("rotation", SpacedRotation.class);
		xStream.alias("modStat", ModStat.class);

		xStream.alias("cube", Cube.class);
		xStream.alias("points", MultiplePoints.class);
		xStream.alias("point", SinglePoint.class);
		xStream.alias("path", Path.class);

		xStream.alias("walkmesh", Walkmesh.class);
		xStream.alias("polygon", Polygon.class);
		xStream.alias("connection", WalkmeshConnection.class);
	}
}
