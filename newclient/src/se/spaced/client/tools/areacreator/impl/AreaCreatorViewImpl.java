package se.spaced.client.tools.areacreator.impl;

import com.google.inject.Singleton;
import se.spaced.client.tools.areacreator.AreaCreatorView;
import se.spaced.shared.world.AreaPoint;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

@Singleton
public class AreaCreatorViewImpl extends JFrame implements AreaCreatorView {

	private Presenter presenter;
	private DefaultListModel listModel;
	private JList pointList;
	private final JPanel areaTypePanel;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final JCheckBox useLocal;
	private final JTextField activeProp;

	@Inject
	public AreaCreatorViewImpl() {
		super("Create Area");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		JPanel rootPanel = new JPanel(new BorderLayout());

		areaTypePanel = new JPanel(new GridLayout(1, 0));
		areaTypePanel.setBorder(BorderFactory.createTitledBorder("Areas"));
		rootPanel.add(areaTypePanel, BorderLayout.NORTH);

		activeProp = new JTextField(30);
		activeProp.setEditable(false);
		useLocal = new JCheckBox("Use Local Space", true);


		JPanel listPanel = createListPanel();
		rootPanel.add(listPanel, BorderLayout.CENTER);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
		JPanel pointsButtonPanel = createPointsButtonPanel();
		JPanel xmlButtonsPanel = createXmlButtonsPanel();


		southPanel.add(pointsButtonPanel);
		southPanel.add(new JSeparator());
		southPanel.add(xmlButtonsPanel);
		southPanel.add(createCurrentPropPanel());
		rootPanel.add(southPanel, BorderLayout.SOUTH);

		getContentPane().add(rootPanel);
		pack();
	}

	private JPanel createCurrentPropPanel() {
		JPanel panel = new JPanel();
		panel.add(useLocal);
		panel.add(new JLabel("Active prop:"));
		panel.add(activeProp);
		return panel;
	}

	@Override
	public void setActiveXmo(String xmoFile) {
		activeProp.setText(xmoFile);
		activeProp.setCaretPosition(activeProp.getText().length() - 1);
	}

	private JPanel createPointsButtonPanel() {
		JPanel midPanel = new JPanel();
		midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.X_AXIS));

		JButton addPointButton = new JButton("Add Point here");
		addPointButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (presenter != null) {
					presenter.addAreaPointHere();
					pointList.setSelectedIndex(pointList.getModel().getSize() - 1);
				}
			}
		});

		JButton insertPointButton = new JButton("Insert Point here");
		insertPointButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (presenter != null) {
					presenter.insertAreaPointHere();
				}
			}
		});

		JButton removePoint = new JButton("Remove selected point");
		removePoint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (presenter != null) {
					presenter.deletePoint();
				}
			}
		});

		JButton clearButton = new JButton("Clear all points");
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (presenter != null) {
					presenter.clearPoints();
				}
			}
		});

		midPanel.add(addPointButton);
		midPanel.add(insertPointButton);
		midPanel.add(removePoint);
		midPanel.add(clearButton);
		return midPanel;
	}

	private JPanel createListPanel() {
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		listModel = new DefaultListModel();
		pointList = new JList(listModel);
		pointList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pointList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (presenter != null) {
					presenter.selectedPoint();
				}
			}
		});

		TransferHandler transferHandler = new TransferHandler(new TransferHandler.Interactions() {
			@Override
			public void copy() {
				presenter.copyAreaPointToClipBoard();
			}

			@Override
			public void paste() {
				presenter.pasteAreaPointFromClipBoard();
			}

			@Override
			public void cut() {
				presenter.cutAreaPointToClipBoard();
			}
		});

		pointList.setTransferHandler(transferHandler);

		JScrollPane scrollPane = new JScrollPane(pointList);
		scrollPane.setPreferredSize(new Dimension(550, 200));
		topPanel.add(scrollPane);
		return topPanel;
	}

	private JPanel createXmlButtonsPanel() {
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		JButton pasteAreaButton = createPasteAreaButton();
		JButton saveAreaButton = createCopyAreaButton();
		JButton saveAsPatternButton = createCopyAsPatternButton();
		JToggleButton toggleShowAreaInWorldButton = createToggleShowAreaInWorldButton();

		bottomPanel.add(pasteAreaButton);
		bottomPanel.add(saveAreaButton);
		bottomPanel.add(saveAsPatternButton);
		bottomPanel.add(toggleShowAreaInWorldButton);
		return bottomPanel;
	}

	private JToggleButton createToggleShowAreaInWorldButton() {
		JToggleButton button = new JToggleButton("Toggle Show Points");
		button.setSelected(true);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (presenter != null) {
					presenter.toggleShowInWorld();
				}
			}
		});
		return button;
	}

	private JButton createCopyAsPatternButton() {
		JButton button = new JButton("Copy geometry");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (presenter != null) {
					presenter.copyGeometryToClipBoard();
				}
			}
		});
		return button;
	}

	private JButton createPasteAreaButton() {
		JButton button = new JButton("Paste geometry");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (presenter != null) {
					presenter.pasteGeometry();
				}
			}
		});
		return button;
	}

	private JButton createCopyAreaButton() {
		JButton button = new JButton("Copy values");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (presenter != null) {
					presenter.copyValuesToClipBoard();
				}
			}
		});
		return button;
	}

	@Override
	public void display() {
		setVisible(true);
	}

	@Override
	public AreaTypeSelector addAreaType(String name, final SelectionCallback selectionCallback) {
		final JRadioButton radioButton = new JRadioButton(name);
		radioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectionCallback.onSelection();
			}
		});
		buttonGroup.add(radioButton);
		areaTypePanel.add(radioButton);
		return new AreaTypeSelector() {
			@Override
			public void select() {
				radioButton.setSelected(true);
			}
		};
	}

	@Override
	public void setListItems(Collection<AreaPoint> items) {
		int selectedIndex = pointList.getSelectedIndex();
		clearListItems();
		for (AreaPoint item : items) {
			listModel.addElement(item);
		}

		pointList.setSelectedIndex(selectedIndex);
	}

	@Override
	public void clearListItems() {
		listModel.removeAllElements();
	}

	@Override
	public AreaPoint getCurrentSelectedAreaPoint() {
		return (AreaPoint) pointList.getSelectedValue();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void close() {
		dispose();
	}

	@Override
	public boolean useLocalSpace() {
		return useLocal.isSelected();
	}
}
