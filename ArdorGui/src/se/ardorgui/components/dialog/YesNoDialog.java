package se.ardorgui.components.dialog;

public abstract class YesNoDialog extends Dialog {
	protected YesNoDialog(String header, String message) {
		super(header, message);
		addButton(new DialogButton("Yes") {
			@Override
			public void execute() {
				onYes();
			}
		});
		addButton(new DialogButton("No") {
			@Override
			public void execute() {
				onNo();
			}
		});
	}

	public abstract void onYes();
	public abstract void onNo();
}