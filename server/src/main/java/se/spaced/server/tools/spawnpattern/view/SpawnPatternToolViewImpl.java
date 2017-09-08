package se.spaced.server.tools.spawnpattern.view;

import com.google.inject.Inject;
import se.spaced.server.model.spawn.SpawnPatternTemplate;
import se.spaced.shared.tools.ui.FilteredJList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SpawnPatternToolViewImpl extends JFrame implements SpawnPatternToolView, ErrorView, IsFrame {
	private FilteredJList<SpawnPatternTemplateListItem> spawnTemplates;
	private Presenter presenter;

	@Inject
	public SpawnPatternToolViewImpl(SpawnPatternTemplateView spawnPatternTemplateView) {
		super("Spt");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel rootPanel = new JPanel(new BorderLayout());

		JSplitPane splitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT,
				createSidePanel(),
				new JScrollPane(spawnPatternTemplateView.asPanel()));

		splitPane.setDividerLocation(200);

		rootPanel.add(createTopPanel(), BorderLayout.NORTH);
		rootPanel.add(splitPane, BorderLayout.CENTER);

		rootPanel.setPreferredSize(new Dimension(800, 600));

		getContentPane().add(rootPanel);


		pack();

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		setMaximizedBounds(env.getMaximumWindowBounds());
		setExtendedState(this.getExtendedState() | MAXIMIZED_BOTH);

		setVisible(true);

	}


	private JPanel createTopPanel() {
		JPanel panel = new JPanel();
		JButton saveButton = new JButton("Save templates");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.saveTemplates();
			}
		});
		panel.add(saveButton);

		JButton deleteButton = new JButton("Delete template");
		deleteButton.setForeground(Color.RED);
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.removeSelectedSpawnPattern(spawnTemplates.getSelectedValue().getSpawnPatternTemplate());
				spawnTemplates.removeItem(spawnTemplates.getSelectedValue());
			}
		});
		panel.add(deleteButton);

		JButton toXmlButton = new JButton("Export to xml");
		toXmlButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.toXml();
			}
		});
		panel.add(toXmlButton);

		JButton newButton = new JButton("New SpawnTemplate");
		newButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(SpawnPatternToolViewImpl.this,
						"Enter the name for new template",
						"New Template",
						JOptionPane.PLAIN_MESSAGE);
				presenter.createNewTemplate(name);
			}
		});
		panel.add(newButton);

		return panel;
	}

	private JPanel createSidePanel() {
		JPanel panel = new JPanel(new BorderLayout());

		spawnTemplates = new FilteredJList<SpawnPatternTemplateListItem>();

		spawnTemplates.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				SpawnPatternTemplate selectedValue = spawnTemplates.getSelectedValue().getSpawnPatternTemplate();
				if (selectedValue != null) {
					presenter.selectedSpawnPattern(selectedValue);
				}

			}
		});

		panel.add(spawnTemplates.getFilterField(), BorderLayout.NORTH);
		JScrollPane scrollPane = new JScrollPane(spawnTemplates);
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	@Override
	public void showErrorMessage(String header, String message) {
		JOptionPane.showMessageDialog(this,
				message,
				header,
				JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void addSpawnPatternTemplate(SpawnPatternTemplate spawnPatternTemplate) {
		spawnTemplates.addItem(new SpawnPatternTemplateListItem(spawnPatternTemplate));

	}

	@Override
	public void selectSpawnPatternTemplate(SpawnPatternTemplate spawnPatternTemplate) {
		spawnTemplates.setSelectedItem(new SpawnPatternTemplateListItem(spawnPatternTemplate));
	}

	@Override
	public void showExportedXml(String title, String xml) {
		JFrame xmlFrame = new JFrame(title);
		JTextArea area = new JTextArea(xml);
		xmlFrame.getContentPane().add(new JScrollPane(area));
		xmlFrame.pack();
		xmlFrame.setVisible(true);
	}

	@Override
	public JFrame asFrame() {
		return this;
	}

	public static class SpawnPatternTemplateListItem {

		private final SpawnPatternTemplate spawnPatternTemplate;

		public SpawnPatternTemplateListItem(SpawnPatternTemplate spawnPatternTemplate) {
			this.spawnPatternTemplate = spawnPatternTemplate;
		}

		@Override
		public String toString() {
			return spawnPatternTemplate.getName() != null ? spawnPatternTemplate.getName() : spawnPatternTemplate.getPk().toString();
		}

		public SpawnPatternTemplate getSpawnPatternTemplate() {
			return this.spawnPatternTemplate;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			SpawnPatternTemplateListItem that = (SpawnPatternTemplateListItem) o;

			return spawnPatternTemplate != null ? spawnPatternTemplate.equals(that.spawnPatternTemplate) : that.spawnPatternTemplate == null;
		}

		@Override
		public int hashCode() {
			return spawnPatternTemplate != null ? spawnPatternTemplate.hashCode() : 0;
		}
	}


}
