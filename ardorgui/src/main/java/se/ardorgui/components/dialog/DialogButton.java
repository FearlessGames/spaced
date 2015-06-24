package se.ardorgui.components.dialog;

public abstract class DialogButton {
	private String text;
	protected DialogButton(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	public abstract void execute();
}
