package se.spaced.spacedit.ui.view.filechooser;

import java.io.File;

public class SelectedFile {
	private File file;
	public UserAction userAction;

	public SelectedFile(File file, UserAction userAction) {
		this.file = file;
		this.userAction = userAction;
	}

	public File getFile() {
		return file;
	}

	public UserAction getUserAction() {
		return userAction;
	}

	public static enum UserAction {
		CANCEL,
		APPROVE,
		ERROR
	}
}
