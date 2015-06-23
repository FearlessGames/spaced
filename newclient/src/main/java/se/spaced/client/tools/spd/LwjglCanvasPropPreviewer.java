package se.spaced.client.tools.spd;

import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.Scene;
import com.ardor3d.framework.Updater;
import com.ardor3d.framework.lwjgl.LwjglAwtCanvas;
import com.ardor3d.framework.lwjgl.LwjglCanvasRenderer;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.math.Ray3;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.Timer;
import org.lwjgl.LWJGLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.shared.model.xmo.XmoEntity;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class LwjglCanvasPropPreviewer implements PropPreviwer {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final Node previewNode = new Node();
	private volatile boolean hasFocus;
	private volatile boolean shutdown;

	@Override
	public void shutdown() {
		shutdown = true;
	}

	@Override
	public void lostFocus() {
		hasFocus = false;
	}

	@Override
	public void gainedFocus() {
		hasFocus = true;
	}

	@Override
	public JComponent getPreviewComponent() {
		try {
			final Scene scene = new PreviewScene();
			final LwjglAwtCanvas canvas = new LwjglAwtCanvas(new DisplaySettings(1, 1, 0, 0, 0, 16, 0, 0, false, false),
					new LwjglCanvasRenderer(scene));
			Timer timer = new Timer();
			final FrameHandler frameHandler = new FrameHandler(timer);
			Updater updater = new PreviewUpdater();
			updater.init();
			frameHandler.addUpdater(updater);
			frameHandler.addCanvas(canvas);

			Runnable target = new PreviewRunner(frameHandler);
			Thread previewUpdateThread = new Thread(target);
			previewUpdateThread.start();
			JPanel panel = new JPanel();
			panel.add(canvas);
			return panel;
		} catch (LWJGLException e) {
			log.error("Failed to create preview canvas", e);
		}

		return new JPanel();
	}

	@Override
	public void preview(XmoEntity xmoEntity) {
		previewNode.detachAllChildren();
		previewNode.attachChild(xmoEntity.getModel());
	}

	private class PreviewScene implements Scene {
		@Override
		public boolean renderUnto(Renderer renderer) {
			renderer.draw(previewNode);
			return true;
		}

		@Override
		public PickResults doPick(Ray3 ray3) {
			return null;
		}
	}

	private class PreviewUpdater implements Updater {
		@Override
		public void init() {

		}

		@Override
		public void update(ReadOnlyTimer readOnlyTimer) {
			previewNode.updateGeometricState(readOnlyTimer.getTimePerFrame(), true);
		}
	}

	private class PreviewRunner implements Runnable {
		private final FrameHandler frameHandler;

		private PreviewRunner(FrameHandler frameHandler) {
			this.frameHandler = frameHandler;
		}

		@Override
		public void run() {
			while (!shutdown) {
				if (hasFocus) {
					frameHandler.updateFrame();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.warn("Interrupted when sleeping", e);
				}
			}
		}
	}
}
