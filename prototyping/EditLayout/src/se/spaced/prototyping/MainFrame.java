package se.spaced.prototyping;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class MainFrame extends JFrame {
	public MainFrame(JPanel drawingPanel, JPanel toolBar){
		Container container = getContentPane();
		GridBagLayout layout = new GridBagLayout();
		container.setLayout(layout);
		setupSwing(container, drawingPanel, toolBar);
		this.setVisible(true);
		pack();
	}

	private void setupSwing(Container container, JPanel drawingPanel, JPanel toolBar) {
		GridBagConstraints constraintsForToolbarPanel = new GridBagConstraints();
		constraintsForToolbarPanel.fill = GridBagConstraints.HORIZONTAL;
		constraintsForToolbarPanel.weightx = 0.5;
		constraintsForToolbarPanel.gridx = 0;
		constraintsForToolbarPanel.gridy = 0;
		container.add(toolBar, constraintsForToolbarPanel);

		GridBagConstraints constraintsForDrawingPanel = new GridBagConstraints();
		constraintsForDrawingPanel.fill = GridBagConstraints.NONE;
		constraintsForDrawingPanel.gridx = 1;
		constraintsForDrawingPanel.gridy = 0;
		constraintsForDrawingPanel.weightx = 0.5;
		container.add(drawingPanel, constraintsForDrawingPanel);

	}
}
