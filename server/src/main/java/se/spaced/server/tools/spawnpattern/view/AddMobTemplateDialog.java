package se.spaced.server.tools.spawnpattern.view;

import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.shared.tools.ui.FilteredJList;
import se.spaced.shared.tools.ui.TwoColumnBuilder;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddMobTemplateDialog extends JDialog {
	private FilteredJList<MobTemplateListItem> mobTemplateList;
	private MobTemplatePanel selectedMobTemplatePanel;
	private Presenter presenter;
	private final BorderBuilder borderBuilder;

	public AddMobTemplateDialog(Frame owner, Iterable<MobTemplate> mobTemplates, BorderBuilder borderBuilder) {
		super(owner, "Select existing Mob Template", true);
		this.borderBuilder = borderBuilder;

		add(createMobTemplateList());


		for (MobTemplate mobTemplate : mobTemplates) {
			mobTemplateList.addItem(new MobTemplateListItem(mobTemplate));
		}

		setLocationRelativeTo(owner);

		pack();


	}


	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	private Component createMobTemplateList() {
		JPanel listPanel = new JPanel(new BorderLayout());
		mobTemplateList = new FilteredJList<MobTemplateListItem>();
		mobTemplateList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				MobTemplateListItem selectedValue = mobTemplateList.getSelectedValue();
				if (selectedValue != null) {
					selectedMobTemplatePanel.setMobTemplateData(selectedValue.getMobTemplate());
				}
			}
		});

		final JButton button = new JButton("Create spawn for mob");
		button.setPreferredSize(new Dimension(140, 23));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MobTemplateListItem selectedValue = mobTemplateList.getSelectedValue();
				if (selectedValue != null) {
					presenter.createNewSpawnTemplateForMob(selectedValue.getMobTemplate());
				}
			}
		});

		listPanel.add(mobTemplateList.getFilterField(), BorderLayout.NORTH);
		listPanel.add(new JScrollPane(mobTemplateList), BorderLayout.CENTER);
		listPanel.add(button, BorderLayout.SOUTH);


		JPanel panel = new JPanel();
		TwoColumnBuilder twoColumnBuilder = new TwoColumnBuilder(panel);
		twoColumnBuilder.addRow(listPanel,
				selectedMobTemplatePanel = new MobTemplatePanel(borderBuilder, "Mob template data"));
		return panel;
	}

	private static class MobTemplateListItem {
		private final MobTemplate mobTemplate;

		private MobTemplateListItem(MobTemplate mobTemplate) {
			this.mobTemplate = mobTemplate;
		}

		private MobTemplate getMobTemplate() {
			return mobTemplate;
		}

		@Override
		public String toString() {
			return mobTemplate.getName();
		}

	}

	protected interface Presenter {
		void createNewSpawnTemplateForMob(MobTemplate mobTemplate);
	}
}
