package se.spaced.spacedit.ui.presenter.toolbar;

import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.ui.Action;
import se.mockachino.annotations.*;
import se.spaced.spacedit.Slf4jTest;
import se.spaced.spacedit.ardor.DefaultScene;
import se.spaced.spacedit.ui.presenter.filechooser.ColladaFileChooserPresenter;
import se.spaced.spacedit.ui.presenter.filechooser.XMOFileManagerPresenter;
import se.spaced.spacedit.ui.view.toolbar.ToolBarView;
import se.spaced.spacedit.xmo.XmoManager;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class ToolBarPresenterTest extends Slf4jTest {

	@Mock
	ToolBarView toolBarView;
	@Mock
	DefaultScene defaultScene;
	@Mock
	ColladaFileChooserPresenter colladaFileChooser;
	XMOFileManagerPresenter xmoPresenter;
	@Mock
	XmoManager xmoManager;

	@Before
	public void setUp() throws Exception {
		setupMocks(this);
	}

	@Test
	public void testActionAssignments() {
		ToolBarPresenterImpl presenter = new ToolBarPresenterImpl(toolBarView, colladaFileChooser, defaultScene, xmoPresenter, xmoManager);
		verifyOnce().on(toolBarView).setOpenXMOAction(any(Action.class));
		verifyOnce().on(toolBarView).setCreateNewXMOAction(any(Action.class));
		verifyOnce().on(toolBarView).setSaveXMOAction(any(Action.class));

	}

}