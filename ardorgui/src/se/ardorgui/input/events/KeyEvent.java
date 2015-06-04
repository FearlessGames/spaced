package se.ardorgui.input.events;

import com.ardor3d.input.Key;

public class KeyEvent {
	private Key key;
	private int keyCharacter;
	private boolean pressed;

	public KeyEvent() {
	}

	public KeyEvent(Key key, int keyCharacter, boolean pressed) {
		this.key = key;
		this.keyCharacter = keyCharacter;
		this.pressed = pressed;
	}

	public boolean isPressed() {
		return pressed;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public int getKeyCharacter() {
		return keyCharacter;
	}

	public void setKeyCharacter(int keyCharacter) {
		this.keyCharacter = keyCharacter;
	}
}
