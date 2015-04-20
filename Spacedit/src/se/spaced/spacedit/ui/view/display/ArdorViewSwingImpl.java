package se.spaced.spacedit.ui.view.display;

import com.ardor3d.framework.lwjgl.LwjglAwtCanvas;
import com.ardor3d.renderer.Camera;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.infonode.docking.View;
import se.spaced.spacedit.ui.tdi.TdiChildWindow;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

@Singleton
public class ArdorViewSwingImpl extends JPanel implements ArdorView, TdiChildWindow {
	private final LwjglAwtCanvas canvas;
	private final View view;
	private se.fearlessgames.common.ui.Action resizeAction;

	@Inject
	public ArdorViewSwingImpl(final LwjglAwtCanvas canvas) throws Exception {
		this.canvas = canvas;
		addComponentListener(new ResizeListener());
		setupCanvas();
		view = new View("3D", null, this);
		view.getWindowProperties().setCloseEnabled(false);
	}


	private void setupCanvas() throws Exception {
		add(canvas);
		canvas.setSize(new Dimension(640, 480));
		canvas.setVisible(true);
	}

	@Override
	public View getTdiView() {
		return view;
	}

	@Override
	public void setCanvasSize(Dimension size) {
		canvas.setSize(size);
		Camera camera = canvas.getCanvasRenderer().getCamera();
		if (camera != null) {
			camera.resize((int) size.getWidth(), (int) size.getWidth());
		}
	}

	@Override
	public void setResizeAction(se.fearlessgames.common.ui.Action action) {
		this.resizeAction = action;
	}

	private final class ResizeListener extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent e) {
			//the resizeing of the canvas is now handled by the presenter
			if (resizeAction != null) {
				resizeAction.act();
			}
		}
	}
}