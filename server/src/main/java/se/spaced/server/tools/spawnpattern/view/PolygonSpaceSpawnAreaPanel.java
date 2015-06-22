package se.spaced.server.tools.spawnpattern.view;

import se.ardortech.math.SpacedVector3;
import se.spaced.server.model.spawn.area.PolygonSpaceSpawnArea;
import se.spaced.shared.tools.ui.TwoColumnBuilder;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Dimension;

public class PolygonSpaceSpawnAreaPanel extends JPanel {
	private final DefaultListModel dataModel;

	public PolygonSpaceSpawnAreaPanel(PolygonSpaceSpawnArea polygonSpaceSpawnArea) {
		dataModel = new DefaultListModel();
		for (SpacedVector3 point : polygonSpaceSpawnArea.getPolygon().getPoints()) {
			dataModel.addElement(point);
		}
		TwoColumnBuilder twoColumnBuilder = new TwoColumnBuilder(this);
		JScrollPane scrollPane = new JScrollPane(new JList(dataModel));
		scrollPane.setPreferredSize(new Dimension(200, 600));

		twoColumnBuilder.
				addRow("Rotation: ", new JLabel(polygonSpaceSpawnArea.getRotation().toString())).
				addRow("Points: ", scrollPane);
	}
}
