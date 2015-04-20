package se.ardorgui.components.label;

import com.ardor3d.ui.text.BMText;
import se.ardorgui.components.base.ComponentListener;

public interface LabelViewInterface extends ComponentListener, LabelListenerInterface {
	void setAlign(BMText.Align align);

	int getRenderedWidth();
	int getRenderedHeight();
}