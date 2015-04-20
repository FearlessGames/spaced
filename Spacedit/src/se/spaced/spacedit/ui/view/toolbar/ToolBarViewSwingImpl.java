package se.spaced.spacedit.ui.view.toolbar;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearlessgames.common.ui.Action;
import se.fearlessgames.common.ui.swing.ActionDispatcher;
import se.spaced.spacedit.state.RunningState;
import se.spaced.spacedit.state.StateChangeListener;
import se.spaced.spacedit.state.StateManager;
import se.spaced.spacedit.ui.tdi.TdiChildToolbar;

import javax.swing.JButton;
import javax.swing.JToolBar;
import java.awt.event.ActionListener;

@Singleton
public class ToolBarViewSwingImpl extends JToolBar implements ToolBarView, TdiChildToolbar, StateChangeListener {
	private final ActionDispatcher createNewXMOActionDispatcher = new ActionDispatcher();
	private final ActionDispatcher saveXMOActionDispatcher = new ActionDispatcher();
	private final ActionDispatcher openXMOActionDispatcher = new ActionDispatcher();


	private JButton newXMOButton;
	private JButton saveXMOButton;
	private JButton openXMOButton;

	@Inject
	public ToolBarViewSwingImpl(final StateManager stateManager) {
		stateManager.registerStateChangeListener(this);
		this.newXMOButton = addTextButton("New XMO", createNewXMOActionDispatcher);

		this.saveXMOButton = addTextButton("Save xmo", saveXMOActionDispatcher);
		this.openXMOButton = addTextButton("Open XMO", openXMOActionDispatcher);

		if (stateManager.getCurrentRunningState() == RunningState.DEFAULT) {
			this.fromXMOInContextToDefault();
		}
	}

	private JButton addTextButton(final String text, final ActionListener actionListener) {
		JButton button = new JButton(text);
		button.addActionListener(actionListener);
		add(button);
		return button;
	}

	@Override
	public JToolBar getToolBar() {
		return this;
	}


	@Override
	public void setOpenXMOAction(Action openXMOAction) {
		this.openXMOActionDispatcher.setAction(openXMOAction);
	}


	@Override
	public void setCreateNewXMOAction(Action createNewXMOAction) {
		this.createNewXMOActionDispatcher.setAction(createNewXMOAction);
	}

	@Override
	public void setSaveXMOAction(Action saveXMOAction) {
		this.saveXMOActionDispatcher.setAction(saveXMOAction);
	}

	@Override
	public void fromDefaultToXMOInContext() {
		saveXMOButton.setEnabled(true);
	}

	@Override
	public void fromXMOInContextToDefault() {
		saveXMOButton.setEnabled(false);
	}
}
