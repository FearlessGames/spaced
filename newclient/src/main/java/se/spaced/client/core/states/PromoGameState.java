package se.spaced.client.core.states;

import com.ardor3d.input.Key;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import se.ardortech.input.KeyListener;
import se.spaced.client.ardor.InputManager;
import se.spaced.client.ardor.ui.SpacedGui;
import se.spaced.client.view.cursor.CursorView;


public class PromoGameState implements GameState, KeyListener {
	private final SpacedGui spacedGui;
	private final CursorView cursorView;
	private final InputManager inputManager;
	private final GameState nextState;
	private final GameStateContext context;

	@Inject
	public PromoGameState(
			SpacedGui spacedGui,
			CursorView cursorView,
			InputManager inputManager,
			@Named("selectServerState") GameState nextState,
			GameStateContext context) {
		this.spacedGui = spacedGui;
		this.cursorView = cursorView;
		this.inputManager = inputManager;
		this.nextState = nextState;
		this.context = context;
	}

	@Override
	public boolean onKey(char character, Key keyCode, boolean pressed) {
		if (pressed && keyCode.equals(Key.ESCAPE)) {
			context.changeState(nextState);
			return true;
		}
		if (pressed && keyCode == Key.F3) {
			spacedGui.reload();
			return true;
		}

		return false;
	}

	@Override
	public void exit() {
		inputManager.removeKeyListener(this);
	}

	@Override
	public void start() {
		spacedGui.start(new String[]{"ui/intro/promo"});
		cursorView.newHover(null);
		inputManager.addKeyListener(this);

	}

	@Override
	public void update(GameStateContext context, double timePerFrame) {
		spacedGui.onUpdate(timePerFrame);
	}

	@Override
	public void updateFixed(GameStateContext context, long millisPerFrame) {
	}
}
