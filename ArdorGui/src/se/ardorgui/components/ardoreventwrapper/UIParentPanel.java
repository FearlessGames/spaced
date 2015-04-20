package se.ardorgui.components.ardoreventwrapper;

import com.ardor3d.extension.ui.UIPanel;
import com.ardor3d.input.InputState;
import com.ardor3d.input.MouseButton;
import se.ardorgui.FixedAnchorLayout;
import se.spaced.shared.events.EventHandler;

public class UIParentPanel extends UIPanel {

	private final EventForwarder forwarder;

	public UIParentPanel(EventHandler eventHandler) {
		setLayout(new FixedAnchorLayout());
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
