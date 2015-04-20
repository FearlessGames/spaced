package se.spaced.spacedit.ui.view.propertylist;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class TextChangeListener implements DocumentListener {

	@Override
	public void insertUpdate(DocumentEvent e) {
		onTextChange();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		onTextChange();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		onTextChange();
	}

	public abstract void onTextChange();


}
