package se.ardorgui.components.dialog;

import java.util.ArrayList;

public abstract class Dialog {
	private String header;
	private String message;
	private ArrayList<DialogButton> buttons;

	protected Dialog(String header, String message) {
		this.header = header;
		this.message = message;
		buttons = new ArrayList<DialogButton>();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String text) {
		this.message = text;
	}

	public void addButton(DialogButton button) {
		buttons.add(button);
	}

	public ArrayList<DialogButton> getButtons() {
		return buttons;
	}

	public abstract void onClose();

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}
}