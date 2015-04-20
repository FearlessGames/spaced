package se.spaced.spacedit.ui.view.filechooser;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * This is not a singleton
 */
public class FileChooserViewSwingImpl extends JFileChooser implements FileChooserView {
	public FileChooserViewSwingImpl() {
	}

	/**
	 * show a open dialog
	 *
	 * @param directory  the root directory
	 * @param extensions what extensions to show
	 * @return the file handler or null if the user pressed cancel
	 */
	@Override
	public SelectedFile selectOpenFile(File directory, String... extensions) {
		FileFilter filter = new FileNameExtensionFilter("Filter on extensions", extensions);
		this.setFileFilter(filter);
		this.setCurrentDirectory(directory);
		int actionVal = this.showOpenDialog(null);
		return new SelectedFile(getSelectedFile(), getAction(actionVal));
	}

	private SelectedFile.UserAction getAction(int actionVal) {
		switch (actionVal) {
			case JFileChooser.APPROVE_OPTION:
				return SelectedFile.UserAction.APPROVE;
			case JFileChooser.CANCEL_OPTION:
				return SelectedFile.UserAction.CANCEL;
		}
		return SelectedFile.UserAction.ERROR;
	}

	/**
	 * show a save dialog
	 *
	 * @param directory  the root directory
	 * @param extensions what extensions to show
	 * @return the file handler or null if user pressed cancel
	 */
	@Override
	public SelectedFile selectSaveFile(File directory, String... extensions) {
		FileFilter filter = new FileNameExtensionFilter("Filter on extensions", extensions);
		this.setFileFilter(filter);
		this.setCurrentDirectory(directory);
		int actionVal = this.showSaveDialog(null);
		return new SelectedFile(getSelectedFile(), getAction(actionVal));
	}


}
