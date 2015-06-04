package se.spaced.shared.tools.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class TwoColumnBuilder {
	private final GridBagConstraints constraints;
	private final JPanel panel;
	private int rows;

	public TwoColumnBuilder(JPanel panel) {
		this.panel = panel;
		panel.setLayout(new GridBagLayout());
		constraints = new GridBagConstraints();
		constraints.insets = new Insets(1, 1, 1, 1);
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.fill = GridBagConstraints.NONE;
	}


	public void addBottomSpacer() {
		constraints.gridy = rows;
		constraints.weighty = 1;
		constraints.weightx = 0;
		constraints.gridx = 0;
		panel.add(new JPanel(), constraints);
	}

	public TwoColumnBuilder addRow(Component c1, Component c2) {
		constraints.gridy = rows;
		constraints.gridx = 0;
		constraints.weightx = 0;
		panel.add(c1, constraints);

		constraints.gridy = rows;
		constraints.gridx = 1;
		constraints.weightx = 1;
		panel.add(c2, constraints);

		rows++;
		return this;
	}

	public TwoColumnBuilder addRow(Component c1) {
		constraints.gridy = rows;
		constraints.gridx = 0;
		constraints.weightx = 0;
		constraints.gridwidth = 2;
		panel.add(c1, constraints);
		constraints.gridwidth = 1;
		rows++;
		return this;
	}

	public TwoColumnBuilder addRow(String text) {
		return addRow(new JLabel(text));
	}

	public TwoColumnBuilder addRow(String text, Component c2) {
		return addRow(new JLabel(text), c2);
	}
}
