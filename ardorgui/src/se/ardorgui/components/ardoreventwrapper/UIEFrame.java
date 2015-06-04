package se.ardorgui.components.ardoreventwrapper;

import com.ardor3d.extension.ui.UIComponent;
import com.ardor3d.extension.ui.UIFrame;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.PickingHint;

import java.util.EnumSet;

public class UIEFrame extends UIFrame {
	public UIEFrame(String title, EnumSet<FrameButtons> buttons) {
		super(title, buttons);
		getStatusBar().setMinimumContentHeight(6);
		getStatusBar().fireComponentDirty();
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

}
