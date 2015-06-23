package se.spaced.client.tools.areacreator.impl;

import se.spaced.client.tools.areacreator.Area;
import se.spaced.client.tools.areacreator.XmlAreaFormatter;
import se.spaced.shared.world.AreaPoint;
import se.spaced.shared.world.area.Geometry;
import se.spaced.shared.world.area.Polygon;
import se.spaced.shared.world.walkmesh.LocalSpaceConverter;

public class PolygonArea extends AbstractArea implements Area {
	private final XmlAreaFormatter xmlAreaFormatter;

	public PolygonArea(XmlAreaFormatter xmlAreaFormatter) {
		this.xmlAreaFormatter = xmlAreaFormatter;
	}

	@Override
	public String asGeometryString(LocalSpaceConverter localSpaceConverter) {
		Polygon polygon = new Polygon();
		for (AreaPoint areaPoint : areaPoints) {
			areaPoint = localSpaceConverter.convert(areaPoint);
			polygon.add(areaPoint.getPoint());
		}
		return xmlAreaFormatter.getXml(polygon);
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
		Polygon polygon = (Polygon) geometry;
		for (AreaPoint areaPoint : polygon.getAreaPoints()) {
			AreaPoint invertedAreaPoint = localSpaceConverter.inverse(areaPoint);
			addAreaPoint(invertedAreaPoint);
		}
	}

}
