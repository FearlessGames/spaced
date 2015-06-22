package se.spaced.server.tools.loot.edit;

import se.spaced.server.loot.LootTemplateProbability;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LootTemplateProbabilityTreeNode extends DefaultMutableTreeNode implements HasLootTemplateEditor {
	private final LootTemplateProbability probability;
	private final Callback<Void> onSave;

	public LootTemplateProbabilityTreeNode(LootTemplateProbability probability, Callback<Void> onSave) {
		super(probability, true);
		this.probability = probability;
		this.onSave = onSave;
	}


	public LootTemplateProbability getProbability() {
		return probability;
	}

	@Override
	public JPanel getEditPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 3));

		panel.add(new JLabel("Percentage"));
		final JTextField p = new JTextField(String.valueOf(probability.getProbability()));
		panel.add(p);

		panel.add(new JLabel(" "));
		JButton button = new JButton("Save");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double percentage = Double.parseDouble(p.getText());
					probability.setProbability(percentage);
					onSave.onAction(null);
				} catch (NumberFormatException nfe) {
					p.setText(String.valueOf(probability.getProbability()));
				}
			}
		});
		panel.add(button);

		return panel;

	}

	@Override
	public String toString() {
		return "P: " + probability.getProbability();
	}

}
