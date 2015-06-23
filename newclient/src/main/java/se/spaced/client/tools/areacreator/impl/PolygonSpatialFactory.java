package se.spaced.client.tools.areacreator.impl;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.google.common.collect.ImmutableList;
import se.spaced.client.tools.areacreator.AreaSpatialFactory;
import se.spaced.shared.world.AreaPoint;
import se.spaced.shared.world.area.PointSequence;

public class PolygonSpatialFactory implements AreaSpatialFactory {

	private final ShapeFactory shapeFactory;

	public PolygonSpatialFactory(ShapeFactory shapeFactory) {
		this.shapeFactory = shapeFactory;
	}

	@Override
	public Spatial create(PointSequence points) {
		ImmutableList<AreaPoint> areaPoints = points.getAreaPoints();
		if (areaPoints.isEmpty()) {
			return new Node();
		}

		Node node = new Node();
		int size = areaPoints.size();
		for (int i = 0; i < size; i++) {
			node.attachChild(shapeFactory.createCylinder(areaPoints.get(i)));
			if (size != i + 1) {
				node.attachChild(shapeFactory.createConnector(areaPoints.get(i), areaPoints.get(i + 1)));
			}
		}
		if (size >= 3) {
			node.attachChild(shapeFactory.createConnector(areaPoints.get(0), areaPoints.get(size - 1)));
			//node.attachChild(shapeFactory.createPolygonPlane(areaPoints));
		}


		return node;
	}
}
