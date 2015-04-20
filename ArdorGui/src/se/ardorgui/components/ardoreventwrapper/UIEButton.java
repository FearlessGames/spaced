package se.ardorgui.components.ardoreventwrapper;

import com.ardor3d.extension.ui.UIButton;
import com.ardor3d.input.InputState;
import com.ardor3d.input.MouseButton;
import se.spaced.shared.events.EventHandler;

public class UIEButton extends UIButton {

	private final EventForwarder forwarder;

	public UIEButton(String text, EventHandler eventHandler) {
		super(text);
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
	public boolean mouseClicked(MouseButton button, InputState state) {
		forwarder.mouseClicked(button, state);
		return super.mouseClicked(button, state);
	}

}
