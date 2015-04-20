package se.spaced.spacedit.ui.presenter.filechooser;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.spacedit.ui.view.filechooser.FileChooserView;
import se.spaced.spacedit.ui.view.filechooser.SelectedFile;
import se.spaced.spacedit.xmo.XmoCreator;
import se.spaced.spacedit.xmo.XmoManager;

import java.io.File;

@Singleton
public class XMOFileManagerPresenterImpl implements XMOFileManagerPresenter {
	private final FileChooserView fileChooserView;
	private final String xmoExtension;
	private final XmoManager xmoManager;
	private final XmoCreator creator;
	private final String xmoDir;
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	public XMOFileManagerPresenterImpl(final FileChooserView fileChooserView, @Named("xmoExtension") final String xmoExtension, final XmoManager xmoManager, final XmoCreator creator, @Named("xmoDir") final String xmoDir) {
		this.fileChooserView = fileChooserView;
		this.xmoExtension = xmoExtension;
		this.xmoManager = xmoManager;
		this.creator = creator;
		this.xmoDir = xmoDir;
	}

	@Override
	public void createNew() {
		SelectedFile selectedFile = fileChooserView.selectSaveFile(new File(xmoDir), xmoExtension);
		if (selectedFile.getUserAction() != SelectedFile.UserAction.APPROVE) {
			return;
		}
		File newFile = selectedFile.getFile();
		creator.create(newFile);

	}

	@Override
	public void load() {
		SelectedFile selectedFile = fileChooserView.selectOpenFile(new File(xmoDir), xmoExtension);
		if (selectedFile.getUserAction() != SelectedFile.UserAction.APPROVE) {
			return;
		}
		File openFile = selectedFile.getFile();
		if (openFile.exists()) {
			xmoManager.loadXmoRoot(openFile.getPath());
		}
	}

}
