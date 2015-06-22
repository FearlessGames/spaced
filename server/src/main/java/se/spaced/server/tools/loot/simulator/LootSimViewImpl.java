package se.spaced.server.tools.loot.simulator;

import se.spaced.server.loot.PersistableLootTemplate;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LootSimViewImpl extends JPanel implements LootSimView {
	private final JComboBox lootTemplateBox;
	private final JTextArea textArea;
	private Presenter presenter;

	public LootSimViewImpl() {
		setLayout(new BorderLayout());
		JPanel upperPanel = new JPanel(new FlowLayout());

		lootTemplateBox = new JComboBox();
		textArea = new JTextArea();

		JButton simulateButton = new JButton("Simulate");
		simulateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (presenter != null) {
					presenter.onSearch((PersistableLootTemplate) lootTemplateBox.getSelectedItem());
				}
			}
		});

		JButton reloadButton = new JButton("Reload");
		reloadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.onReload();
			}
		});


		upperPanel.add(lootTemplateBox);
		upperPanel.add(simulateButton);
		upperPanel.add(reloadButton);

		add(upperPanel, BorderLayout.NORTH);
		add(new JScrollPane(textArea), BorderLayout.CENTER);

	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setTemplates(PersistableLootTemplate[] templates) {
		lootTemplateBox.removeAllItems();
		lootTemplateBox.setModel(new DefaultComboBoxModel(templates));
	}

	@Override
	public JTextArea getTextArea() {
		return textArea;
	}

	@Override
	public Component getPanel() {
		return this;
	}


}
