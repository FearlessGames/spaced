package se.spaced.spacedit.launcher;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.log.Slf4jJulBridge;
import se.fearlessgames.common.ui.Action;
import se.spaced.spacedit.ardor.FrameUpdater;
import se.spaced.spacedit.launcher.modules.ArdorAwtModule;
import se.spaced.spacedit.launcher.modules.SpacEditModule;
import se.spaced.spacedit.ui.presenter.mainframe.MainFramePresenter;

public class SpacEditLauncher {
	private final MainFramePresenter presenter;
	private final Injector injector;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public SpacEditLauncher() {
		injector = Guice.createInjector(Stage.DEVELOPMENT, new ArdorAwtModule(), new SpacEditModule());
		presenter = injector.getInstance(MainFramePresenter.class);
	}

	static {
		Slf4jJulBridge.init();
	}

	public void showGui() throws Exception {
		logger.info("Starting spacedit");
		presenter.start();
		presenter.setExitAction(new Action() {
			@Override
			public void act() {
				System.exit(0);
			}
		});
	}

	private void createUpdateThread() {
		FrameUpdater frameUpdater = injector.getInstance(FrameUpdater.class);
		Thread thread = new Thread(frameUpdater);
		thread.run();
	}

	public static void main(String[] args) throws Exception {
		SpacEditLauncher launcher = new SpacEditLauncher();
		launcher.showGui();
		launcher.createUpdateThread();
	}
}