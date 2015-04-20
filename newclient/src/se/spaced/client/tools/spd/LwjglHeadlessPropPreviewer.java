package se.spaced.client.tools.spd;

import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.Scene;
import com.ardor3d.framework.lwjgl.LwjglHeadlessCanvas;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Ray3;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.shared.model.xmo.XmoEntity;
import se.spaced.shared.scheduler.Invoker;
import se.spaced.shared.scheduler.Job;
import se.spaced.shared.scheduler.JobManager;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;

public class LwjglHeadlessPropPreviewer implements PropPreviwer, Scene {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final Node previewNode = new Node();
	private final LwjglHeadlessCanvas canvas;
	private final Timer timer = new Timer();
	private final DisplaySettings settings;
	private final JLabel label;

	private final BufferedImage labelImage;
	private final int[] tmpData;


	private volatile boolean hasFocus;
	private volatile boolean shutdown;
	private final JobManager jobManager;
	private final Invoker invokeTarget;

	public LwjglHeadlessPropPreviewer(JobManager jobManager) {
		this.jobManager = jobManager;

		settings = new DisplaySettings(300, 300, 0, 0, false);
		canvas = new LwjglHeadlessCanvas(settings, this);
		canvas.getRenderer().setBackgroundColor(ColorRGBA.BLACK_NO_ALPHA);
		labelImage = new BufferedImage(settings.getWidth(), settings.getHeight(), BufferedImage.TYPE_INT_ARGB);
		tmpData = ((DataBufferInt) labelImage.getRaster().getDataBuffer()).getData();

		label = new JLabel("View of xmo:");
		label.setVerticalTextPosition(SwingConstants.TOP);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		label.setIcon(new ImageIcon(labelImage));

		//need to run it on the main thread, so use jobManager and invoker target for a job
		invokeTarget = new Invoker() {
			@Override
			public void invoke() {
				timer.update();

				previewNode.updateGeometricState(timer.getTimePerFrame(), true);
				canvas.draw();

				final IntBuffer data = canvas.getDataBuffer();
				final int width = settings.getWidth();
				int x = settings.getHeight();
				while (--x >= 0) {
					data.get(tmpData, x * width, width);
				}

				label.setIcon(new ImageIcon(labelImage));
			}
		};
		//then push the job to the jobManager each tick in the update thread
		new Thread(new PreviewRunner());
	}

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
		return label;
	}

	@Override
	public void preview(XmoEntity xmoEntity) {
		previewNode.detachAllChildren();
		previewNode.attachChild(xmoEntity.getModel());
	}

	@Override
	public boolean renderUnto(Renderer renderer) {
		renderer.draw(previewNode);
		return true;
	}

	@Override
	public PickResults doPick(Ray3 ray3) {
		return null;
	}

	private class PreviewRunner implements Runnable {
		@Override
		public void run() {
			while (!shutdown) {
				if (hasFocus) {
					jobManager.addJob(new Job(0, false, invokeTarget));
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					log.warn("Interrupted when sleeping", e);
				}
			}
		}
	}


}
