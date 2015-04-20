package se.spaced.client.tools.areacreator.impl;

import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.client.tools.areacreator.Area;
import se.spaced.client.tools.areacreator.XmlAreaFormatter;
import se.spaced.shared.world.AreaPoint;
import se.spaced.shared.world.area.Geometry;
import se.spaced.shared.world.area.Path;
import se.spaced.shared.world.walkmesh.LocalSpaceConverter;

public class PathArea extends AbstractArea implements Area {
	private final XmlAreaFormatter xmlAreaFormatter;

	public PathArea(XmlAreaFormatter xmlAreaFormatter) {
		this.xmlAreaFormatter = xmlAreaFormatter;
	}

	@Override
	public String asGeometryString(LocalSpaceConverter localSpaceConverter) {
		Path path = new Path();
		for (AreaPoint areaPoint : areaPoints) {
			areaPoint = localSpaceConverter.convert(areaPoint);
			path.add(areaPoint.getPoint());
		}
		return xmlAreaFormatter.getXml(path);
	}

	@Override
	public String asListValueString(LocalSpaceConverter localSpaceConverter) {
		StringBuilder sb = new StringBuilder();
		for (AreaPoint areaPoint : areaPoints) {
			areaPoint = localSpaceConverter.convert(areaPoint);
			sb.append(xmlAreaFormatter.getLocationXmlTag(areaPoint)).append(NEW_LINE);
		}
		return sb.toString();
	}

	@Override
	public void addGeometry(Geometry geometry, LocalSpaceConverter localSpaceConverter) {
		Path path = (Path) geometry;
		for (SpacedVector3 spacedVector3 : path) {
			AreaPoint areaPoint = new AreaPoint(spacedVector3, SpacedRotation.IDENTITY);
			AreaPoint invertedAreaPoint = localSpaceConverter.inverse(areaPoint);
			addAreaPoint(invertedAreaPoint);
		}
	}

}
