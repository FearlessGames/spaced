package se.spaced.spacedit.ui.view.filechooser;

import java.io.File;

public interface FileChooserView {
	public SelectedFile selectOpenFile(File directory, String... extensions);

	public SelectedFile selectSaveFile(File directory, String... extensions);

}
