package se.spaced.mvp.view.impl;

import se.spaced.mvp.Action;
import se.spaced.mvp.view.MainToolBarView;
import se.spaced.mvp.view.tdi.TdiChildToolbar;

import javax.swing.JButton;
import javax.swing.JToolBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainToolBarViewImpl extends JToolBar implements MainToolBarView, TdiChildToolbar {
	private Action openFileAction;
	private Action saveFileAction;

	public MainToolBarViewImpl() {
		addButtons();
	}

	private void addButtons() {
		JButton button = new JButton("Open File");
		button.addActionListener(newAction(openFileAction));
		add(button);

		button = new JButton("Save File");
		button.addActionListener(newAction(saveFileAction));
		add(button);
	}

	private ActionListener newAction(final Action action) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (action != null) {
					action.act();
				}
			}
		};
	}


	@Override
	public void setOpenFileAction(Action action) {
		this.openFileAction = action;
	}

	@Override
	public void setSaveFileAction(Action action) {
		this.saveFileAction = action;
	}

	@Override
	public JToolBar getToolBar() {
		return this;
	}
}
