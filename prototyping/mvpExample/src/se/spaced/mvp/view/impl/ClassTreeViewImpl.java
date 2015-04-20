package se.spaced.mvp.view.impl;

import net.infonode.docking.View;
import se.spaced.mvp.view.ClassTreeView;
import se.spaced.mvp.view.tdi.TdiChildWindow;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.GridLayout;

public class ClassTreeViewImpl extends JPanel implements ClassTreeView, TdiChildWindow {
	private JTree tree;
	private final View view;

	public ClassTreeViewImpl() {
		buildTree();
		view = new View("Class Tree", null, this);
		view.getWindowProperties().setCloseEnabled(false);

		this.setBackground(java.awt.Color.BLUE);
	}

	private void buildTree() {
		String[] strs = {"swing", "platf", "basic", "metal", "JTree"};

		DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[strs.length];
		for (int i = 0; i < strs.length; i++) {
			nodes[i] = new DefaultMutableTreeNode(strs[i]);
		}
		nodes[0].add(nodes[1]);
		nodes[1].add(nodes[2]);
		nodes[1].add(nodes[3]);
		nodes[0].add(nodes[4]);
		tree = new JTree(nodes[0]);
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
		renderer.setOpenIcon(null);
		renderer.setClosedIcon(null);
		renderer.setLeafIcon(null);
		BasicTreeUI ui = (BasicTreeUI) tree.getUI();
		ui.setExpandedIcon(null);
		ui.setCollapsedIcon(null);

		setLayout(new GridLayout(1, 1));

		add(tree);
	}

	@Override
	public View getTdiView() {
		return view;
	}
}
