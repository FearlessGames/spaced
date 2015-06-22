package se.spaced.server.tools.spawnpattern.view;

import se.spaced.server.mob.brains.templates.BrainTemplate;
import se.spaced.server.mob.brains.templates.CompositeBrainTemplate;
import se.spaced.shared.tools.ui.TwoColumnBuilder;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

public class BrainPanel extends JPanel {

	private final JLabel nameLabel;
	private final JTextArea stackArea;

	public BrainPanel(Border border) {
		this.setBorder(border);
		TwoColumnBuilder columnBuilder = new TwoColumnBuilder(this);
		nameLabel = new JLabel();
		columnBuilder.addRow("Name", nameLabel);
		stackArea = new JTextArea(5, 40);
		columnBuilder.addRow("Brain stack", stackArea);
	}

	public void setBrain(BrainTemplate brain) {
		nameLabel.setText(brain.getName());
		stackArea.setText("");
		if (brain instanceof CompositeBrainTemplate) {
			CompositeBrainTemplate composite = (CompositeBrainTemplate) brain;
			for (BrainTemplate brainTemplate : composite.brains()) {
				stackArea.append(brainTemplate.getClass().getSimpleName());
				stackArea.append("\n");
			}
		}
	}

	public void clearBrain() {
		nameLabel.setText("");
		stackArea.setText("");
	}
}
