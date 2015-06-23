package se.spaced.client.core.states;

import com.ardor3d.input.Key;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import se.spaced.client.ardor.InputManager;
import se.spaced.client.ardor.ui.SpacedGui;
import se.spaced.client.view.cursor.CursorView;


public class CreateCharacterState extends AbstractLoginState {
	private final GameStateContext context;
	private final GameState selectCharacterState;

	@Inject
	public CreateCharacterState(SpacedGui spacedGui, GameStateContext context,
			@Named("selectCharacterState") GameState selectCharacterState, CursorView cursorView, InputManager inputManager) {
		super(spacedGui, cursorView, inputManager);
		this.context = context;
		this.selectCharacterState = selectCharacterState;
	}

	@Override
	protected String getStartScript() {
		return "ui/createcharacter";
	}

	@Override
	public boolean onKey(char character, Key keyCode, boolean pressed) {
		if (pressed && keyCode.equals(Key.ESCAPE)) {
			context.changeState(selectCharacterState);
		}
		return super.onKey(character, keyCode, pressed);
	}
}
