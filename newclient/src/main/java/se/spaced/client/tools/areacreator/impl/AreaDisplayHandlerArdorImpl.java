package se.spaced.client.tools.areacreator.impl;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.spaced.client.tools.areacreator.Area;
import se.spaced.client.tools.areacreator.AreaDisplayHandler;
import se.spaced.client.tools.areacreator.AreaSpatialFactory;
import se.spaced.shared.world.AreaPoint;
import se.spaced.shared.world.area.PointSequence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class AreaDisplayHandlerArdorImpl implements AreaDisplayHandler {
	private final ShapeFactory shapeFactory;
	private final Node areaDisplayRootNode;
	private final Node indicatorNode;
	private final Map<Class<? extends Area>, AreaSpatialFactory> factoryMap;

	@Inject
	public AreaDisplayHandlerArdorImpl(@Named("rootNode") Node rootNode) {
		areaDisplayRootNode = new Node("AreaDisplayRootNode");
		indicatorNode = new Node("IndicatorNode");
		rootNode.attachChild(areaDisplayRootNode);
		rootNode.attachChild(indicatorNode);
		shapeFactory = new ShapeFactory(ColorRGBA.RED, ColorRGBA.YELLOW, 2d, 0.2);

		factoryMap = new HashMap<Class<? extends Area>, AreaSpatialFactory>();

		AreaSpatialFactory individualArowFactory = new IndividualSpatialFactory();
		factoryMap.put(SinglePointArea.class, individualArowFactory);
		factoryMap.put(MultiPointArea.class, individualArowFactory);
		factoryMap.put(PathArea.class, new PathSpatialFactory(shapeFactory));
		factoryMap.put(PolygonArea.class, new PolygonSpatialFactory(shapeFactory));

	}

	@Override
	public void areaModified(Area area) {
		areaDisplayRootNode.detachAllChildren();
		areaDisplayRootNode.attachChild(factoryMap.get(area.getClass()).create(area));
	}

	@Override
	public void show(Area area) {
		areaModified(area);
	}

	@Override
	public void hide() {
		areaDisplayRootNode.detachAllChildren();
	}

	@Override
	public void showIndicator(AreaPoint currentPoint) {
		indicatorNode.detachAllChildren();
		Spatial indicator = shapeFactory.createIndicator(currentPoint);
		indicatorNode.attachChild(indicator);
	}

	@Override
	public void removeIndicator() {
		indicatorNode.detachAllChildren();
	}

	private static class PathSpatialFactory implements AreaSpatialFactory {
		private final ShapeFactory shapeFactory;

		PathSpatialFactory(
				ShapeFactory shapeFactory) {
			this.shapeFactory = shapeFactory;
		}

		@Override
		public Spatial create(PointSequence points) {
			List<AreaPoint> areaPoints = points.getAreaPoints();
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
			return node;
		}
	}


	private class IndividualSpatialFactory implements AreaSpatialFactory {

		@Override
		public Spatial create(PointSequence points) {
			Node node = new Node();
			for (AreaPoint areaPoint : points.getAreaPoints()) {
				node.attachChild(shapeFactory.createArrow(areaPoint));
			}
			return node;
		}
	}
}
