package se.spaced.mvp.view.impl;

import net.infonode.docking.View;
import se.spaced.mvp.view.EditView;
import se.spaced.mvp.view.tdi.TdiChildWindow;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class EditViewImpl extends JPanel implements EditView, TdiChildWindow {
	private final View view;

	public EditViewImpl() {
		add(new JLabel("Ardor editor panel!"), BorderLayout.CENTER);
		view = new View("3D", null, this);
		view.getWindowProperties().setCloseEnabled(false);
	}

	@Override
	public View getTdiView() {
		return view;
	}
}
