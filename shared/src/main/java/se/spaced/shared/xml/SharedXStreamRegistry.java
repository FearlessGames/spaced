package se.spaced.shared.xml;

import com.thoughtworks.xstream.XStream;
import se.ardortech.math.AABox;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.Sphere;
import se.fearless.common.stats.ModStat;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.model.xmo.*;
import se.spaced.shared.world.area.*;
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
