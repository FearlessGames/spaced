package se.ardorgui.components.ardoreventwrapper;

import com.ardor3d.extension.ui.UIPasswordField;
import com.ardor3d.input.InputState;
import com.ardor3d.input.Key;
import com.ardor3d.input.MouseButton;
import se.spaced.shared.events.EventHandler;

public class UIEPasswordField extends UIPasswordField {

	private final EventForwarder forwarder;

	public UIEPasswordField(EventHandler eventHandler) {
		forwarder = new EventForwarder(this, eventHandler);
	}

	@Override
	public void mouseEntered(int mouseX, int mouseY, InputState state) {
		forwarder.mouseEntered(mouseX, mouseY);
		super.mouseEntered(mouseX, mouseY, state);
	}

	@Override
	public void mouseDeparted(int mouseX, int mouseY, InputState state) {
		forwarder.mouseDeparted(mouseX, mouseY);
		super.mouseDeparted(mouseX, mouseY, state);
	}

	@Override
	public boolean mousePressed(MouseButton button, InputState state) {
		forwarder.mousePressed(button, state);
		return super.mousePressed(button, state);
	}

	@Override
	public boolean mouseReleased(MouseButton button, InputState state) {
		forwarder.mouseReleased(button, state);
		return super.mouseReleased(button, state);
	}

	@Override
	public boolean keyPressed(Key key, InputState state) {
		forwarder.keyPressed(key, state);
		return super.keyPressed(key, state);
	}

	@Override
	public void gainedFocus() {
		super.gainedFocus();
		switchState(_writingState);
	}
}
