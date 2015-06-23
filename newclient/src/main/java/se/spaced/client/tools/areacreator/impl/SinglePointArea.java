package se.spaced.client.tools.areacreator.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import se.spaced.client.tools.areacreator.Area;
import se.spaced.client.tools.areacreator.XmlAreaFormatter;
import se.spaced.shared.world.AreaPoint;
import se.spaced.shared.world.area.Geometry;
import se.spaced.shared.world.area.SinglePoint;
import se.spaced.shared.world.walkmesh.LocalSpaceConverter;

import java.util.List;

public class SinglePointArea implements Area {
	private final List<AreaPoint> areaPoints = Lists.newArrayList();
	private final XmlAreaFormatter xmlAreaFormatter;

	public SinglePointArea(XmlAreaFormatter xmlAreaFormatter) {
		this.xmlAreaFormatter = xmlAreaFormatter;
	}

	@Override
	public ImmutableList<AreaPoint> getAreaPoints() {
		return ImmutableList.copyOf(areaPoints);
	}

	@Override
	public void addAreaPoint(AreaPoint areaPoint) {
		areaPoints.clear();
		areaPoints.add(areaPoint);
	}

	@Override
	public void insertAreaPointAfter(AreaPoint afterPoint, AreaPoint areaPoint) {
		addAreaPoint(areaPoint);
	}

	@Override
	public String asGeometryString(LocalSpaceConverter localSpaceConverter) {
		if (areaPoints.isEmpty()) {
			return "";
		}

		AreaPoint areaPoint = areaPoints.get(0);
		areaPoint = localSpaceConverter.convert(areaPoint);
		SinglePoint singlePoint = new SinglePoint(areaPoint.getPoint(), areaPoint.getRotation());
		return xmlAreaFormatter.getXml(singlePoint);
	}

	@Override
	public String asListValueString(LocalSpaceConverter localSpaceConverter) {
		if (areaPoints.isEmpty()) {
			return "";
		}
		AreaPoint areaPoint = areaPoints.get(0);
		areaPoint = localSpaceConverter.convert(areaPoint);
		String location = xmlAreaFormatter.getLocationXmlTag(areaPoint);
		String rotation = xmlAreaFormatter.getRotationXmlTag(areaPoint);
		return location + NEW_LINE + rotation;
	}

	@Override
	public void remove(AreaPoint point) {
		areaPoints.remove(point);
	}

	@Override
	public void clear() {
		areaPoints.clear();
	}

	@Override
	public void addGeometry(Geometry geometry, LocalSpaceConverter localSpaceConverter) {
		SinglePoint singlePoint = (SinglePoint) geometry;
		AreaPoint areaPoint = new AreaPoint(singlePoint.getPoint(), singlePoint.getRotation());
		AreaPoint invertedAreaPoint = localSpaceConverter.inverse(areaPoint);
		addAreaPoint(invertedAreaPoint);
	}


}
