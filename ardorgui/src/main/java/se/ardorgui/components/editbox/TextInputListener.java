package se.ardorgui.components.editbox;

import com.ardor3d.input.Key;
import com.ardor3d.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardorgui.components.listeners.ComponentKeyListenerAdapter;

import java.util.HashSet;
import java.util.Set;

// TODO: refactor this class
public class TextInputListener extends ComponentKeyListenerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(TextInputListener.class);
	private final Set<Key> nonVisualCharacters;
	private final EditBox editBox;

	public TextInputListener(final EditBox editBox) {
		this.editBox = editBox;

		nonVisualCharacters = new HashSet<Key>();
		nonVisualCharacters.add(Key.APPS);
		nonVisualCharacters.add(Key.BACK);
		nonVisualCharacters.add(Key.CAPITAL);
		nonVisualCharacters.add(Key.DELETE);
		nonVisualCharacters.add(Key.DOWN);
		nonVisualCharacters.add(Key.END);
		nonVisualCharacters.add(Key.ESCAPE);
		nonVisualCharacters.add(Key.F1);
		nonVisualCharacters.add(Key.F2);
		nonVisualCharacters.add(Key.F3);
		nonVisualCharacters.add(Key.F4);
		nonVisualCharacters.add(Key.F5);
		nonVisualCharacters.add(Key.F6);
		nonVisualCharacters.add(Key.F7);
		nonVisualCharacters.add(Key.F8);
		nonVisualCharacters.add(Key.F9);
		nonVisualCharacters.add(Key.F10);
		nonVisualCharacters.add(Key.F11);
		nonVisualCharacters.add(Key.F12);
		nonVisualCharacters.add(Key.F13);
		nonVisualCharacters.add(Key.F14);
		nonVisualCharacters.add(Key.F15);
		nonVisualCharacters.add(Key.HOME);
		nonVisualCharacters.add(Key.INSERT);
		nonVisualCharacters.add(Key.LCONTROL);
		nonVisualCharacters.add(Key.LEFT);
		nonVisualCharacters.add(Key.LSHIFT);
		nonVisualCharacters.add(Key.LMETA);
		nonVisualCharacters.add(Key.PAGEDOWN_NEXT);
		nonVisualCharacters.add(Key.PAGEUP_PRIOR);
		nonVisualCharacters.add(Key.PAUSE);
		nonVisualCharacters.add(Key.RCONTROL);
		nonVisualCharacters.add(Key.RSHIFT);
		nonVisualCharacters.add(Key.RIGHT);
		nonVisualCharacters.add(Key.RMENU);
		nonVisualCharacters.add(Key.RMETA);
		nonVisualCharacters.add(Key.SCROLL);
		nonVisualCharacters.add(Key.SLEEP);
		nonVisualCharacters.add(Key.STOP);
		nonVisualCharacters.add(Key.SYSRQ);
		nonVisualCharacters.add(Key.TAB);
		//nonVisualCharacters.add(Key.NONE);
	}

	@Override
	public void keyPressed(final KeyEvent e) {
		final StringBuilder builder = new StringBuilder(editBox.getText());
		if (e.getKey() == Key.BACK) {
			if (builder.length() > 0) {
				builder.deleteCharAt(builder.length() - 1);
			}
		} else if (e.getKey() == Key.RETURN) {
			logger.debug("In focus, return");
			editBox.getEditBoxListeners().returnPressed(editBox);
			return;
		} else if (Character.isLetterOrDigit(e.getKeyChar()) || Character.isSpaceChar(e.getKeyChar())) {
			builder.append(String.valueOf(e.getKeyChar()));
		} else {
			if (!nonVisualCharacters.contains(e.getKey())) {
				if (logger.isDebugEnabled()) {
					logger.debug("Other char " + e.getKey());
				}
				builder.append(String.valueOf(e.getKeyChar()));
			}
		}
		editBox.setText(builder.toString());
	}
}
