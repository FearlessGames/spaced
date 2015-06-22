package se.spaced.server.tools.spawnpattern.view;

import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.shared.tools.ui.TwoColumnBuilder;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SpawnAreaViewImpl extends JPanel implements SpawnAreaView {
	private final JLabel uuidLabel;
	private final JPanel areaPanel;
	private Presenter presenter;

	public SpawnAreaViewImpl() {
		super();
		TwoColumnBuilder twoColumnBuilder = new TwoColumnBuilder(this);
		twoColumnBuilder.
				addRow(new JLabel("UUID: "), uuidLabel = new JLabel()).
				addRow(areaPanel = new JPanel(new GridLayout(1, 1))).
				addRow(createButtons()).
				addBottomSpacer();

	}

	private Component createButtons() {
		JPanel panel = new JPanel(new GridLayout(1, 1));
		JButton changeTypeButton = new JButton("Change area type and paste data");
		changeTypeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.changeAreaType();
			}
		});
		panel.add(changeTypeButton);

		return panel;
	}

	@Override
	public Class<?> askForAreaType(Class<?>[] possibilities, Class<?> preselected) {
		return (Class<?>) JOptionPane.showInputDialog(
				this,
				"Select the spawn area class to use\nMake sure the apropriate area data has been copied from SCAD",
				"Spawn area type",
				JOptionPane.PLAIN_MESSAGE,
				null,
				possibilities,
				preselected);
	}


	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setUUID(UUID pk) {
		uuidLabel.setText(pk.toString());
	}

	@Override
	public void setSpawnAreaPanel(JPanel panel) {
		areaPanel.removeAll();
		areaPanel.add(panel);
	}

	@Override
	public JPanel asPanel() {
		return this;
	}
}
