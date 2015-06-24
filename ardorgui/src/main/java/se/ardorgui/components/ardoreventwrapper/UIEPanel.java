package se.ardorgui.components.ardoreventwrapper;

import com.ardor3d.extension.ui.UIComponent;
import com.ardor3d.extension.ui.UIPanel;
import com.ardor3d.input.InputState;
import com.ardor3d.input.MouseButton;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.PickingHint;
import se.ardorgui.FixedAnchorLayout;
import se.spaced.shared.events.EventHandler;

public class UIEPanel extends UIPanel {

	private final EventForwarder forwarder;

	public UIEPanel(EventHandler eventHandler) {
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

	@Override
	public UIComponent getUIComponent(final int hudX, final int hudY) {
		if (!getSceneHints().isPickingHintEnabled(PickingHint.Pickable) || !isVisible() || !insideMargin(hudX, hudY)) {
			return null;
		}

		UIComponent ret = null;
		UIComponent found = this;

		for (int i = 0; i < getNumberOfChildren(); i++) {
			final Spatial s = getChild(i);
			if (s instanceof UIComponent) {
				final UIComponent comp = (UIComponent) s;
				ret = comp.getUIComponent(hudX, hudY);

				if (ret != null) {
					found = ret;
				}
			}
		}

		return found;
	}

	@Override
	public void updateMinimumSizeFromContents() {
	}
}
