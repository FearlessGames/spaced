package se.spaced.server.tools.spawnpattern.view;

import se.spaced.server.model.spawn.area.RandomSpaceSpawnArea;
import se.spaced.shared.tools.ui.TwoColumnBuilder;
import se.spaced.shared.world.area.Cube;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class RandomSpaceSpawnAreaPanel extends JPanel {

	public RandomSpaceSpawnAreaPanel(RandomSpaceSpawnArea area) {
		Cube cube = area.getCube();
		TwoColumnBuilder columnBuilder = new TwoColumnBuilder(this);
		columnBuilder.addRow("Corner:", new JLabel(cube.getCorner().toString()));
		columnBuilder.addRow("Width:", new JLabel(String.valueOf(cube.getWidth())));
		columnBuilder.addRow("Height:", new JLabel(String.valueOf(cube.getHeight())));
		columnBuilder.addRow("Depth:", new JLabel(String.valueOf(cube.getDepth())));
		columnBuilder.addRow("Rotation:", new JLabel(cube.getRotation().toString()));
	}
}
