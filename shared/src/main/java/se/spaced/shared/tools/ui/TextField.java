package se.spaced.shared.tools.ui;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextField extends JTextField implements DocumentListener {
	private TextChanged textChanged;

	public TextField() {
		getDocument().addDocumentListener(this);
	}

	public TextField(String text) {
		super(text);
		getDocument().addDocumentListener(this);
	}

	public TextField(int columns) {
		super(columns);
		getDocument().addDocumentListener(this);
	}

	public TextField(String text, int columns) {
		super(text, columns);
		getDocument().addDocumentListener(this);
	}

	public void addTextChangeListener(TextChanged textChanged) {
		this.textChanged = textChanged;
	}

	private void trigger() {
		if (textChanged != null) {
			textChanged.onChange(this, this.getText());
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		trigger();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		trigger();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		trigger();
	}

	public interface TextChanged {
		void onChange(TextField sender, String text);
	}
}
