package se.spaced.spacedit.ui.presenter.mainframe;

import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.ui.Action;
import se.mockachino.annotations.*;
import se.spaced.spacedit.Slf4jTest;
import se.spaced.spacedit.ui.view.frame.MainViewSwingImpl;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class MainFramePresenterTest extends Slf4jTest {

	@Mock
	MainViewSwingImpl mainView;

	@Before
	public void setUp() throws Exception {
		setupMocks(this);
	}

	@Test
	public void testThatViewsAreStartedAtLaunch() throws Exception {
		MainFramePresenterImpl presenter = new MainFramePresenterImpl(mainView, null, null, null, null);
		presenter.start();
		verifyOnce().on(mainView).start();
	}

	@Test
	public void testThatActionsOnMainViewAreSet() throws Exception {
		MainFramePresenterImpl presenter = new MainFramePresenterImpl(mainView, null, null, null, null);
		verifyOnce().on(mainView).setQuitButtonAction(any(Action.class));
	}
}
