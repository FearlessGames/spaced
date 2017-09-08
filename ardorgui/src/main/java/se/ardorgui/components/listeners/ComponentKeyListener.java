package se.ardorgui.components.listeners;


import com.ardor3d.input.KeyEvent;

public interface ComponentKeyListener {
	void keyPressed(KeyEvent e);
	void keyReleased(KeyEvent e);
	void keyTyped(KeyEvent e);
}