package se.spaced.mvp.view.impl;

import se.spaced.mvp.Action;
import se.spaced.mvp.view.MainMenuView;
import se.spaced.mvp.view.tdi.TdiChildMenu;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuViewImpl extends JMenuBar implements MainMenuView, TdiChildMenu {
	private Action openFileAction;
	private Action saveFileAction;
	private Action quitAction;

	public MainMenuViewImpl() {
		add(createFileMenu());
	}

	private JMenu createFileMenu() {
		JMenu menu = new JMenu("File");
		menu.add("Open File").addActionListener(newAction(openFileAction));
		menu.add("Save File").addActionListener(newAction(saveFileAction));
		menu.addSeparator();
		menu.add("Quit").addActionListener(newAction(quitAction));
		return menu;
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
	public JMenuBar getMenuBar() {
		return this;
	}

	@Override
	public void setOpenFileAction(se.spaced.mvp.Action action) {
		this.openFileAction = action;
	}

	@Override
	public void setSaveFileAction(Action action) {
		this.saveFileAction = action;
	}

	@Override
	public void setQuitAction(Action action) {
		this.quitAction = action;
	}
}
