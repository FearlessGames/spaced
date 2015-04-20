package se.spaced.client.core.states;

import com.ardor3d.input.Key;
import com.google.inject.Inject;
import se.ardortech.Main;
import se.spaced.client.ardor.InputManager;
import se.spaced.client.ardor.ui.SpacedGui;
import se.spaced.client.view.cursor.CursorView;

public class SelectServerState extends AbstractLoginState {
	private final Main main;

	@Inject
	public SelectServerState(SpacedGui spacedGui, Main main, CursorView cursorView, InputManager inputManager) {
		super(spacedGui, cursorView, inputManager);
		this.main = main;
	}

	@Override
	protected String getStartScript() {
		return "ui/selectserver";
		//return "ui/test/testbutton";
		//return "ui/intro/refugee";
	}

	@Override
	public boolean onKey(char character, Key keyCode, boolean pressed) {
		if (pressed && keyCode.equals(Key.ESCAPE)) {
			main.exit();
		}
		return super.onKey(character, keyCode, pressed);
	}
}
