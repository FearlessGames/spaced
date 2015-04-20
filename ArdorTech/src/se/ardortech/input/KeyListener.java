package se.ardortech.input;

import com.ardor3d.input.Key;

public interface KeyListener {
	boolean onKey(final char character, final Key keyCode, final boolean pressed);
}