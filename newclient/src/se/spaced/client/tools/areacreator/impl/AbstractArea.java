package se.spaced.client.tools.areacreator.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import se.spaced.client.tools.areacreator.Area;
import se.spaced.shared.world.AreaPoint;

import java.util.List;

public abstract class AbstractArea implements Area {
	List<AreaPoint> areaPoints = Lists.newArrayList();

	@Override
	public ImmutableList<AreaPoint> getAreaPoints() {
		return ImmutableList.copyOf(areaPoints);
	}

	@Override
	public void insertAreaPointAfter(AreaPoint afterPoint, AreaPoint areaPoint) {
		int i = areaPoints.indexOf(afterPoint);
		areaPoints.add(i + 1, areaPoint);
	}

	@Override
	public void addAreaPoint(AreaPoint areaPoint) {
		areaPoints.add(areaPoint);
	}

	@Override
	public void remove(AreaPoint point) {
		areaPoints.remove(point);
	}

	@Override
	public void clear() {
		areaPoints.clear();
	}
}
