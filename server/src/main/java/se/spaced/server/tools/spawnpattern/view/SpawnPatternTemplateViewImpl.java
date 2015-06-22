package se.spaced.server.tools.spawnpattern.view;

import com.google.inject.Inject;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.shared.tools.ui.FilteredJList;
import se.spaced.shared.tools.ui.TextField;
import se.spaced.shared.tools.ui.TwoColumnBuilder;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SpawnPatternTemplateViewImpl extends JPanel implements SpawnPatternTemplateView {
	private Presenter presenter;
	private final JLabel uuidLabel;
	private TextField nameTextField;
	private final SpawnAreaView spawnAreaView;

	private FilteredJList<MobSpawnTemplateListItem> spawnTemplateList;


	@Inject
	public SpawnPatternTemplateViewImpl(SpawnAreaView spawnAreaView, MobSpawnTemplateView mobSpawnTemplateView) {
		super();
		this.spawnAreaView = spawnAreaView;
		TwoColumnBuilder twoColumnBuilder = new TwoColumnBuilder(this);
		JPanel listPanel = createSpawnTemplateListPanel();

		twoColumnBuilder.
				addRow("UUID:", uuidLabel = new JLabel()).
				addRow("Name:", createNameTextField()).
				addRow("Spawn Area:", spawnAreaView.asPanel()).
				addRow("Mob spawn schedules:").
				addRow(listPanel, mobSpawnTemplateView.asPanel()).

				addBottomSpacer();


	}

	private Component createNameTextField() {
		nameTextField = new TextField(40);

		nameTextField.addTextChangeListener(new TextField.TextChanged() {
			@Override
			public void onChange(TextField sender, String text) {
				presenter.changeNameOnCurrentPattern(text);
			}
		});
		return nameTextField;
	}


	private JPanel createSpawnTemplateListPanel() {

		JButton removeButton = new JButton("Remove selected");
		removeButton.setPreferredSize(new Dimension(140, 23));
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MobSpawnTemplateListItem selectedValue = spawnTemplateList.getSelectedValue();
				if (selectedValue != null) {
					presenter.removeSpawnTemplate(selectedValue.getMobSpawnTemplate());
				}
			}
		});

		JButton addButton = new JButton("Add new");
		addButton.setPreferredSize(new Dimension(140, 23));
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.addMobSpawnTemplate();
			}
		});

		spawnTemplateList = new FilteredJList<MobSpawnTemplateListItem>();
		spawnTemplateList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				MobSpawnTemplateListItem selectedValue = spawnTemplateList.getSelectedValue();
				if (selectedValue != null) {
					presenter.selectedMobSpawnTemplate(selectedValue.getMobSpawnTemplate());
				}
			}
		});
		JPanel listPanel = new JPanel(new BorderLayout());
		listPanel.add(spawnTemplateList.getFilterField(), BorderLayout.NORTH);
		listPanel.add(new JScrollPane(spawnTemplateList), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
		buttonPanel.add(removeButton);
		buttonPanel.add(addButton);
		listPanel.add(buttonPanel, BorderLayout.SOUTH);
		return listPanel;
	}

	@Override
	public JPanel asPanel() {
		return this;
	}


	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setUUID(String uuid) {
		uuidLabel.setText(uuid);
	}

	@Override
	public void setPatternName(String name) {
		nameTextField.setText(name);
	}

	@Override
	public void setMobSpawns(Iterable<MobSpawnTemplate> mobspawns) {
		spawnTemplateList.removeAllItems();
		for (MobSpawnTemplate mobspawn : mobspawns) {
			spawnTemplateList.addItem(new MobSpawnTemplateListItem(mobspawn));
		}
	}


	@Override
	public void selectMobSpawnTemplate(MobSpawnTemplate mobSpawnTemplate) {
		if (mobSpawnTemplate == null) {
			spawnTemplateList.clearSelection();
			presenter.selectedMobSpawnTemplate(null);
		} else {
			spawnTemplateList.setSelectedItem(new MobSpawnTemplateListItem(mobSpawnTemplate));
		}
	}


	private static class MobSpawnTemplateListItem {
		private final MobSpawnTemplate mobSpawnTemplate;

		private MobSpawnTemplateListItem(MobSpawnTemplate mobSpawnTemplate) {
			this.mobSpawnTemplate = mobSpawnTemplate;
		}

		@Override
		public String toString() {
			return mobSpawnTemplate.getMobTemplate().getName();
		}

		public MobSpawnTemplate getMobSpawnTemplate() {
			return mobSpawnTemplate;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof MobSpawnTemplateListItem)) {
				return false;
			}

			MobSpawnTemplateListItem that = (MobSpawnTemplateListItem) o;

			if (mobSpawnTemplate != null ? !mobSpawnTemplate.equals(that.mobSpawnTemplate) : that.mobSpawnTemplate != null) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return mobSpawnTemplate != null ? mobSpawnTemplate.hashCode() : 0;
		}
	}


}
