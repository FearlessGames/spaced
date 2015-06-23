package se.spaced.client.tools.areacreator;

import se.spaced.shared.world.AreaPoint;

public interface XmlAreaFormatter {
	String getRotationXmlTag(AreaPoint areaPoint);

	String getLocationXmlTag(AreaPoint areaPoint);

	String getXml(Object object);
}
