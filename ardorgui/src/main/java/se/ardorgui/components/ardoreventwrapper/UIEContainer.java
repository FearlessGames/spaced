package se.ardorgui.components.ardoreventwrapper;

import com.ardor3d.extension.ui.UIComponent;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.PickingHint;
import se.spaced.shared.events.EventHandler;

public class UIEContainer extends UIEPanel {

	public UIEContainer(EventHandler eventHandler) {
		// Useful for debugging
		//setBackdrop(new SolidBackdrop(new ColorRGBA(0.0f, 0.7f, 0.2f, 0.7f)));
		super(eventHandler);
	}

	@Override
    public UIComponent getUIComponent(final int hudX, final int hudY) {
        if (!getSceneHints().isPickingHintEnabled(PickingHint.Pickable) || !isVisible()) {
            return null;
        }

        UIComponent ret = null;
        UIComponent found = null;

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
