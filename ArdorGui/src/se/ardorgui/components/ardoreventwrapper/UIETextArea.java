package se.ardorgui.components.ardoreventwrapper;

import com.ardor3d.extension.ui.UIState;
import com.ardor3d.extension.ui.UITextArea;
import com.ardor3d.extension.ui.backdrop.EmptyBackdrop;

public class UIETextArea extends UITextArea {
	public UIETextArea() {
		setBackdrop(new EmptyBackdrop());
		_defaultState = new UIState();
		switchState(_defaultState);
	}
}
