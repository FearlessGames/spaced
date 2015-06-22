package se.spaced.server.tools.spawnpattern.view;

import se.spaced.shared.tools.ui.TwoColumnBuilder;
import se.spaced.shared.world.area.Geometry;
import se.spaced.shared.world.area.Path;
import se.spaced.shared.world.area.Polygon;
import se.spaced.shared.world.area.SinglePoint;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

public class GeometryPanelFactory {
	private final Map<Class<? extends Geometry>, Factory> factoryMap;

	public GeometryPanelFactory() {
		factoryMap = new HashMap<Class<? extends Geometry>, Factory>();
		factoryMap.put(Path.class, new ListPanelFactory());
		factoryMap.put(Polygon.class, new ListPanelFactory());
		factoryMap.put(SinglePoint.class, new SinglePointFactory());
	}

	JPanel create(Geometry geometry) {
		Factory factory = factoryMap.get(geometry.getClass());
		if (factory != null) {
			return factory.create(geometry);
		}
		return null;
	}

	private interface Factory {
		JPanel create(Geometry geometry);
	}

	private static class ListPanelFactory implements Factory {

		@Override
		public JPanel create(Geometry geometry) {
			JPanel rootPanel = new JPanel(new BorderLayout());
			rootPanel.add(new JLabel(geometry.getClass().getSimpleName()), BorderLayout.NORTH);
			JScrollPane scrollPane = new JScrollPane(creatList(geometry));
			scrollPane.setPreferredSize(new Dimension(400, 100));
			rootPanel.add(scrollPane, BorderLayout.CENTER);
			return rootPanel;
		}

		private Component creatList(Geometry geometry) {
			if (geometry instanceof Polygon) {
				return new JList(((Polygon) geometry).getPoints().toArray());
			} else if (geometry instanceof Path) {
				return new JList(((Path) geometry).getPathPoints().toArray());
			}
			throw new RuntimeException("Not a list geometry shape!");
		}
	}

	private static class SinglePointFactory implements Factory {
		@Override
		public JPanel create(Geometry geometry) {
			if (geometry instanceof SinglePoint) {
				JPanel rootPanel = new JPanel();
				TwoColumnBuilder twoColumnBuilder = new TwoColumnBuilder(rootPanel);
				twoColumnBuilder.
						addRow(new JLabel("SinglePoint")).
						addRow(new JLabel("Position: "), new JLabel(((SinglePoint) geometry).getPoint().toString())).
						addRow(new JLabel("Rotation: "), new JLabel(((SinglePoint) geometry).getRotation().toString()));

				return rootPanel;
			}
			throw new RuntimeException("Not a Single Point!");
		}
	}
}
