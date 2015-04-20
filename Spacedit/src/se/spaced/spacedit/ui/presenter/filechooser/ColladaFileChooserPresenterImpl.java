package se.spaced.spacedit.ui.presenter.filechooser;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.spacedit.ui.view.filechooser.FileChooserView;
import se.spaced.spacedit.ui.view.filechooser.SelectedFile;
import se.spaced.spacedit.xmo.XmoManager;

import java.io.File;

@Singleton
public class ColladaFileChooserPresenterImpl implements ColladaFileChooserPresenter {
	private final FileChooserView fileChooserView;
	private final String colladaExtension;
	private final String colladaDir;
	private final XmoManager xmoManager;
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	public ColladaFileChooserPresenterImpl(final FileChooserView fileChooserView, @Named("colladaExtension") final String colladaExtension, final XmoManager xmoManager, @Named("colladaDir") final String colladaDir) {
		this.fileChooserView = fileChooserView;
		this.colladaExtension = colladaExtension;
		this.xmoManager = xmoManager;
		this.colladaDir = colladaDir;
	}

	@Override
	public void loadColladaFile() {
		SelectedFile selectedFile = fileChooserView.selectOpenFile(new File(colladaDir), colladaExtension);
		if (selectedFile.getUserAction() != SelectedFile.UserAction.APPROVE) {
			return;
		}

		File file = selectedFile.getFile();
		if (file.exists()) {


			xmoManager.addColladaFileAsXmo(file.getPath());
		}

	}

}
