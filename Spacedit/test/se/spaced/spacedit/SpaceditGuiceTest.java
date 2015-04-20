package se.spaced.spacedit;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import org.junit.Before;
import org.junit.Ignore;
import se.spaced.spacedit.launcher.modules.MockModule;
import se.spaced.spacedit.launcher.modules.SpacEditModule;
import se.spaced.spacedit.ui.presenter.mainframe.MainFramePresenter;

import static org.junit.Assert.assertNotNull;

@Ignore
public class SpaceditGuiceTest {
	private MainFramePresenter mainFramePresenter;

	@Before
	public void setUp() {
		Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new MockModule(), new SpacEditModule());
		mainFramePresenter = injector.getInstance(MainFramePresenter.class);
	}

	@Ignore
	public void testSpacedServer() {
		assertNotNull(mainFramePresenter);
	}
}
