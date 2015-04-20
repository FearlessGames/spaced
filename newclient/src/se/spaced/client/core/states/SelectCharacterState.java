package se.spaced.client.core.states;

import com.ardor3d.input.Key;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import se.spaced.client.ardor.InputManager;
import se.spaced.client.ardor.ui.SpacedGui;
import se.spaced.client.view.cursor.CursorView;

public class SelectCharacterState extends AbstractLoginState {
	private final GameStateContext context;
	private final GameState loginState;

	@Inject
	public SelectCharacterState(
			SpacedGui spacedGui, GameStateContext context, @Named("loginState") GameState loginState, CursorView cursorView, InputManager inputManager) {
		super(spacedGui, cursorView, inputManager);
		this.context = context;
		this.loginState = loginState;
	}

	@Override
	protected String getStartScript() {
		return "ui/selectcharacter";
	}

	@Override
	public boolean onKey(char character, Key keyCode, boolean pressed) {
		if (pressed && keyCode.equals(Key.ESCAPE)) {
			context.changeState(loginState);
		}
		return super.onKey(character, keyCode, pressed);
	}
}