package se.spaced.client.core.states;

import com.ardor3d.input.Key;
import com.google.inject.Inject;
import se.spaced.client.ardor.InputManager;
import se.spaced.client.ardor.ui.SpacedGui;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.client.view.cursor.CursorView;

public class LoginState extends AbstractLoginState {

	private final ServerConnection serverConnection;

	@Inject
	public LoginState(SpacedGui spacedGui, ServerConnection serverConnection, CursorView cursorView, InputManager inputManager) {
		super(spacedGui, cursorView, inputManager);
		this.serverConnection = serverConnection;
	}

	@Override
	protected String getStartScript() {
		return "ui/login";
	}

	@Override
	public boolean onKey(char character, Key keyCode, boolean pressed) {
		if (pressed && keyCode.equals(Key.ESCAPE)) {
			serverConnection.disconnect("");
		}
		return super.onKey(character, keyCode, pressed);
	}
}