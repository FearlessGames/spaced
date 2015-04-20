package se.ardorgui.components.editbox;

import se.ardorgui.components.listeners.ListenerContainer;

public class EditBoxListeners extends ListenerContainer<EditBoxListener> {
	public void returnPressed(EditBox editBox) {
		for (EditBoxListener listener : listeners) {
			listener.returnPressed(editBox);
		}
	}

	public void onTextChanged(EditBox editBox) {
		for (EditBoxListener listener : listeners) {
			listener.textChanged(editBox);
		}
	}
}