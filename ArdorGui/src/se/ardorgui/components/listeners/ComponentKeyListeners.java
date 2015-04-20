package se.ardorgui.components.listeners;


import com.ardor3d.input.KeyEvent;

public class ComponentKeyListeners extends ListenerContainer<ComponentKeyListener> {
	public void sendPressedEvent(KeyEvent e) {
		for (ComponentKeyListener listener : listeners) {
			listener.keyPressed(e);
		}
	}

	public void sendReleasedEvent(KeyEvent e) {
		for (ComponentKeyListener listener : listeners) {
			listener.keyReleased(e);
		}
	}
}