package se.ardorgui.components.listeners;


import com.ardor3d.input.KeyEvent;

public interface ComponentKeyListener {
	public void keyPressed(KeyEvent e);
	public void keyReleased(KeyEvent e);
	public void keyTyped(KeyEvent e);
}