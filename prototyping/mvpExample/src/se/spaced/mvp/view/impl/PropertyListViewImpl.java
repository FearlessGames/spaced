package se.spaced.mvp.view.impl;

import net.infonode.docking.View;
import se.spaced.mvp.view.PropertyListView;
import se.spaced.mvp.view.tdi.TdiChildWindow;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class PropertyListViewImpl extends JPanel implements PropertyListView, TdiChildWindow {
	private final View view;

	public PropertyListViewImpl() {
		add(new JLabel("Some property editor"), BorderLayout.CENTER);
		view = new View("Props", null, this);
		view.getWindowProperties().setCloseEnabled(false);
	}

	@Override
	public View getTdiView() {
		return view;
	}
}
