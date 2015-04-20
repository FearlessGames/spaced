package se.spaced.server.tools.spawnpattern.view;

import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.shared.tools.ui.TwoColumnBuilder;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MobTemplatePanel extends JPanel {
	private JLabel mobName;
	private JLabel mobPK;
	private JLabel mobStamina;
	private JLabel mobMaxShield;
	private JLabel mobCoolRate;
	private JLabel mobShieldRecovery;
	private BrainPanel brainPanel;

	public MobTemplatePanel(BorderBuilder borderBuilder, String title) {
		this.setBorder(borderBuilder.getTitleBorder(title));
		TwoColumnBuilder twoColumnBuilder = new TwoColumnBuilder(this);

		twoColumnBuilder.addRow("UUID", mobPK = new JLabel());
		twoColumnBuilder.addRow("Name", mobName = new JLabel());
		twoColumnBuilder.addRow("Stamina", mobStamina = new JLabel());
		twoColumnBuilder.addRow("Max Shield", mobMaxShield = new JLabel());
		twoColumnBuilder.addRow("Cool Rate", mobCoolRate = new JLabel());
		twoColumnBuilder.addRow("Shield Recovery", mobShieldRecovery = new JLabel());
		twoColumnBuilder.addRow(brainPanel = new BrainPanel(borderBuilder.getTitleBorder("Brain stack")));
	}

	public void setMobTemplateData(MobTemplate mobTemplate) {
		if (mobTemplate == null) {
			mobPK.setText("");
			mobName.setText("");
			mobStamina.setText("");
			mobMaxShield.setText("");
			mobCoolRate.setText("");
			mobShieldRecovery.setText("");
			brainPanel.clearBrain();
		} else {
			mobPK.setText(mobTemplate.getPk().toString());
			mobName.setText(mobTemplate.getName());
			mobStamina.setText(String.valueOf(mobTemplate.getStamina()));
			mobMaxShield.setText(String.valueOf(mobTemplate.getMaxShield()));
			mobCoolRate.setText(String.valueOf(mobTemplate.getCoolRate()));
			mobShieldRecovery.setText(String.valueOf(mobTemplate.getShieldRecovery()));
			brainPanel.setBrain(mobTemplate.getBrainTemplate());
		}
	}
}
