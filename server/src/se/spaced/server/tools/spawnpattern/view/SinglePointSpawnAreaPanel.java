package se.spaced.server.tools.spawnpattern.view;

import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.shared.tools.ui.TwoColumnBuilder;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SinglePointSpawnAreaPanel extends JPanel {

	public SinglePointSpawnAreaPanel(SinglePointSpawnArea area) {
		TwoColumnBuilder twoColumnBuilder = new TwoColumnBuilder(this);
		twoColumnBuilder.
				addRow(new JLabel("Position: "), new JLabel(area.getSpawnPoint().getPoint().toString())).
				addRow(new JLabel("Rotation: "), new JLabel(area.getSpawnPoint().getRotation().toString()));
	}
}
