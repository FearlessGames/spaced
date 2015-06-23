package se.spaced.client.tools.areacreator.impl;

import se.spaced.client.tools.areacreator.Area;
import se.spaced.client.tools.areacreator.XmlAreaFormatter;
import se.spaced.shared.world.AreaPoint;
import se.spaced.shared.world.area.Geometry;
import se.spaced.shared.world.area.MultiplePoints;
import se.spaced.shared.world.area.SinglePoint;
import se.spaced.shared.world.walkmesh.LocalSpaceConverter;

public class MultiPointArea extends AbstractArea implements Area {
	private final XmlAreaFormatter xmlAreaFormatter;

	public MultiPointArea(XmlAreaFormatter xmlAreaFormatter) {
		this.xmlAreaFormatter = xmlAreaFormatter;
	}

	@Override
	public String asGeometryString(LocalSpaceConverter localSpaceConverter) {
		MultiplePoints multiplePoints = new MultiplePoints();
		for (AreaPoint areaPoint : areaPoints) {
			areaPoint = localSpaceConverter.convert(areaPoint);
			multiplePoints.add(new SinglePoint(areaPoint.getPoint(), areaPoint.getRotation()));
		}

		return xmlAreaFormatter.getXml(multiplePoints);
	}

	@Override
	public String asListValueString(LocalSpaceConverter localSpaceConverter) {
		StringBuilder sb = new StringBuilder();
		for (AreaPoint areaPoint : areaPoints) {
			areaPoint = localSpaceConverter.convert(areaPoint);
			sb.append(xmlAreaFormatter.getLocationXmlTag(areaPoint)).append(NEW_LINE);
			sb.append(xmlAreaFormatter.getRotationXmlTag(areaPoint)).append(NEW_LINE);
		}
		return sb.toString();
	}

	@Override
	public void addGeometry(Geometry geometry, LocalSpaceConverter localSpaceConverter) {
		MultiplePoints multiplePoints = (MultiplePoints) geometry;
		for (SinglePoint singlePoint : multiplePoints.getPoints()) {
			AreaPoint areaPoint = new AreaPoint(singlePoint.getPoint(), singlePoint.getRotation());
			AreaPoint invertedAreaPoint = localSpaceConverter.inverse(areaPoint);
			addAreaPoint(invertedAreaPoint);
		}
	}


}
