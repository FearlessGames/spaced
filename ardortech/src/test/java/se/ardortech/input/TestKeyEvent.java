package se.ardortech.input;

import com.ardor3d.input.Key;
import com.ardor3d.input.KeyEvent;
import com.ardor3d.input.KeyState;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestKeyEvent {
	@Test
	public void test() {
		char character = 'z';
		KeyState keyState = KeyState.DOWN;
		Key key = Key.A;
		KeyEvent keyEvent = new KeyEvent(key, keyState, character);
		assertEquals(keyEvent.getKey(), key);
		assertEquals(keyEvent.getState(), keyState);
		assertEquals(keyEvent.getKeyChar(), character);
	}
}