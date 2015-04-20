package se.spaced.spacedit.ui.presenter.toolbar;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.ui.Action;
import se.spaced.spacedit.ardor.DefaultScene;
import se.spaced.spacedit.ui.presenter.filechooser.ColladaFileChooserPresenter;
import se.spaced.spacedit.ui.presenter.filechooser.XMOFileManagerPresenter;
import se.spaced.spacedit.ui.view.toolbar.ToolBarView;
import se.spaced.spacedit.xmo.XmoManager;

@Singleton
public class ToolBarPresenterImpl implements ToolBarPresenter {
	private final ToolBarView toolBarView;
	private final ColladaFileChooserPresenter colladaFileChooser;
	private final DefaultScene scene;
	private final XMOFileManagerPresenter xmoFileCreationPresenter;
	private final XmoManager xmoManager;
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	public ToolBarPresenterImpl(final ToolBarView toolBarView, final ColladaFileChooserPresenter colladaFileChooser, final DefaultScene scene, final XMOFileManagerPresenter xmoFileCreationPresenter, final XmoManager xmoManager) {
		this.toolBarView = toolBarView;
		this.colladaFileChooser = colladaFileChooser;
		this.scene = scene;
		this.xmoFileCreationPresenter = xmoFileCreationPresenter;
		this.xmoManager = xmoManager;


		toolBarView.setCreateNewXMOAction(new Action() {
			@Override
			public void act() {
				xmoFileCreationPresenter.createNew();
			}
		});
		toolBarView.setSaveXMOAction(new Action() {
			@Override
			public void act() {
				xmoManager.saveXmoRoot();
			}
		});
		toolBarView.setOpenXMOAction(new Action() {
			@Override
			public void act() {
				xmoFileCreationPresenter.load();
			}
		});
	}
}
