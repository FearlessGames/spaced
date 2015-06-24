package se.ardorgui.components.dialog;

public class InfoDialog extends Dialog {
	public InfoDialog(String header, String message) {
		super(header, message);
		addButton(new DialogButton("Ok") {
			@Override
			public void execute() {
				onOk();
			}
		});
	}

	public void onOk() {
		onClose();
	}

	@Override
	public void onClose() {
	}
}