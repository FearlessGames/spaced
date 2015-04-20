package se.spaced.client.core.states;

import com.ardor3d.input.Key;
import se.ardortech.input.KeyListener;
import se.spaced.client.ardor.InputManager;
import se.spaced.client.ardor.ui.SpacedGui;
import se.spaced.client.view.cursor.CursorView;

public abstract class AbstractLoginState implements GameState, KeyListener {
	protected final SpacedGui spacedGui;
	private final CursorView cursorView;
	private final InputManager inputManager;

	protected AbstractLoginState(SpacedGui spacedGui, CursorView cursorView, InputManager inputManager) {
		this.spacedGui = spacedGui;
		this.cursorView = cursorView;
		this.inputManager = inputManager;
	}

	@Override
	public void exit() {
		inputManager.removeKeyListener(this);
		spacedGui.teardown();
	}

	@Override
	public void start() {
		spacedGui.start(new String[] {getStartScript()});
		cursorView.newHover(null);
		inputManager.addKeyListener(this);
	}

	protected abstract String getStartScript();


	@Override
	public boolean onKey(char character, Key keyCode, boolean pressed) {
		if (pressed && keyCode == Key.F3) {
			spacedGui.reload();
			return true;
		}
		return false;
	}

	@Override
	public void update(GameStateContext context, double timePerFrame) {
		spacedGui.onUpdate(timePerFrame);
	}

	@Override
	public void updateFixed(GameStateContext context, long millisPerFrame) {
	}
}