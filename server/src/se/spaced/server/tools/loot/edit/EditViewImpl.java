package se.spaced.server.tools.loot.edit;

import se.spaced.server.loot.PersistableLootTemplate;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditViewImpl extends JPanel implements EditView, TreeSelectionListener {
	private final JComboBox lootTemplateBox;
	private final JPanel contentPanel;
	private final JTree tree;
	private Presenter presenter;


	public EditViewImpl() {
		setLayout(new BorderLayout());
		contentPanel = new JPanel(new FlowLayout());
		lootTemplateBox = new JComboBox();

		tree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode("-")));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);
		tree.addMouseListener(new TreeMouseAdapter());

		final JPanel topPanel = new JPanel();
		topPanel.add(lootTemplateBox);
		topPanel.add(createButton("View", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.onViewTemplate((PersistableLootTemplate) lootTemplateBox.getSelectedItem());
			}
		}));

		topPanel.add(createButton("Reload", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.onReload();
			}
		}));

		topPanel.add(createButton("Persist", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.onPersist();
			}
		}));

		createNewButton(topPanel);

		add(topPanel, BorderLayout.NORTH);


		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				new JScrollPane(tree),
				new JScrollPane(contentPanel));
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);

		add(splitPane, BorderLayout.CENTER);
	}

	private void createNewButton(JPanel topPanel) {
		final JPopupMenu newMenu = new JPopupMenu();
		newMenu.add(new JMenuItem("KofN")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(
						EditViewImpl.this,
						"Please enter the name for the new KofN lootTemplate",
						"New LootTemplate",
						JOptionPane.PLAIN_MESSAGE);

				presenter.createNewKofN(name);

			}
		});

		newMenu.add(new JMenuItem("Multi")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(
						EditViewImpl.this,
						"Please enter the name for the new Multi lootTemplate",
						"New LootTemplate",
						JOptionPane.PLAIN_MESSAGE);

				presenter.createNewMulti(name);

			}
		});

		newMenu.add(new JMenuItem("Single")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(
						EditViewImpl.this,
						"Please enter the name for the new Single Item LootTemplate",
						"New LootTemplate",
						JOptionPane.PLAIN_MESSAGE);

				presenter.createNewSingle(name);

			}
		});

		final JButton button = new JButton("New");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newMenu.show(button, 0, button.getHeight());
			}
		});

		topPanel.add(button);
	}

	private JButton createButton(String name, ActionListener actioListener) {
		JButton button = new JButton(name);
		button.addActionListener(actioListener);
		return button;
	}


	@Override
	public Component getPanel() {
		return this;
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
	public void setTreeRootNode(TreeNode rootNode) {
		tree.setModel(new DefaultTreeModel(rootNode));

	}

	@Override
	public void setEditorPanel(JPanel editorPanel) {
		contentPanel.removeAll();
		contentPanel.add(editorPanel);
		updateUI();
	}

	@Override
	public void refreshTree() {
		tree.updateUI();
		updateUI();
	}


	@Override
	public void valueChanged(TreeSelectionEvent e) {
		presenter.onNodeSelected(tree.getLastSelectedPathComponent());
	}

	private class TreeMouseAdapter extends MouseAdapter {
		private void myPopupEvent(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			JTree tree = (JTree) e.getSource();
			TreePath path = tree.getPathForLocation(x, y);
			if (path == null) {
				return;
			}

			tree.setSelectionPath(path);
			Object object = path.getLastPathComponent();
			if (object != null && object instanceof HasPopupMenu) {
				HasPopupMenu hasPopupMenu = (HasPopupMenu) object;
				JPopupMenu popup = hasPopupMenu.getPopupMenu();
				popup.show(tree, x, y);
			}
		}

		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				myPopupEvent(e);
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				myPopupEvent(e);
			}
		}
	}
}
