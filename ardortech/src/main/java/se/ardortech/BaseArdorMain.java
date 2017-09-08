package se.ardortech;

import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.framework.Updater;
import com.ardor3d.util.TextureManager;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseArdorMain implements Main {
	private static Logger logger = LoggerFactory.getLogger(BaseArdorMain.class);
	private final FrameHandler frameHandler;
	protected final NativeCanvas canvas;
	protected final Updater updater;
	private volatile boolean exit = false;

	@Inject
	public BaseArdorMain(final FrameHandler frameHandler, final NativeCanvas canvas, final Updater updater) {
		this.frameHandler = frameHandler;
		this.canvas = canvas;
		this.updater = updater;
	}

	@Override
	public void run() {
		logger.info("Start");
		frameHandler.addUpdater(updater);
		frameHandler.addCanvas(canvas);
		frameHandler.init();
		SystemInfoLogger systemInfoLogger = new SystemInfoLogger(LoggerFactory.getLogger("SystemInfo"));
		systemInfoLogger.logHardwareData();
		systemInfoLogger.logOsData();
		systemInfoLogger.logJvmData();
		systemInfoLogger.logGlCapabilities();
		mainLoop();
	}

	public void mainLoop() {
		logger.info("Starting mainloop");
		while (!exit && !canvas.isClosing()) {
			update();
		}

		cleanUp();
	}

	protected void update() {
		frameHandler.updateFrame();
	}

	@Override
	public void exit() {
		logger.info("Exit flag set");
		exit = true;
	}

	public void cleanUp() {
		logger.info("Running clean up");
		canvas.getCanvasRenderer().makeCurrentContext();
		TextureManager.cleanAllTextures(canvas.getCanvasRenderer().getRenderer());
		canvas.close();
	}
}