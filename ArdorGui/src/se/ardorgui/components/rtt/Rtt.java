package se.ardorgui.components.rtt;

import com.ardor3d.framework.CanvasRenderer;
import com.ardor3d.image.Texture2D;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.TextureRenderer;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.event.DirtyType;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.shape.Quad;
import se.ardorgui.components.ardoreventwrapper.UIEPanel;
import se.ardorgui.components.area.ComponentArea;
import se.spaced.shared.events.EventHandler;

public class Rtt extends UIEPanel {

	private final Camera camera;
	private final TextureRenderer textureRenderer;
	private final Texture2D texture = new Texture2D();
	private Node rootNode;
	private final CanvasRenderer canvasRenderer;
	private final Quad quad;


	public Rtt(
			final ComponentArea componentArea, ComponentArea rttArea, final Node rootNode,
			final Camera camera,
			final TextureRenderer textureRenderer,
			CanvasRenderer canvasRenderer,
			EventHandler eventHandler) {
		super(eventHandler);
		this.setDoClip(true);
		quad = new Quad("RttQuad", rttArea.getWidth(), rttArea.getHeight());
		setContentSize(componentArea.getWidth(), componentArea.getHeight());
		quad.setTranslation(componentArea.getWidth() >> 1, componentArea.getHeight() >> 1, 0);


		quad.getSceneHints().setLightCombineMode(LightCombineMode.Off);
		attachChild(quad);
		this.camera = camera;
		this.textureRenderer = textureRenderer;
		this.canvasRenderer = canvasRenderer;

		setNodeToRender(rootNode);

		textureRenderer.setBackgroundColor(new ColorRGBA(0, 0, 0, 0));
		textureRenderer.getCamera().set(camera);

		canvasRenderer.makeCurrentContext();
		textureRenderer.setupTexture(texture);
	}


	public void updateTexture() {
		canvasRenderer.makeCurrentContext();
		rootNode.updateGeometricState(0, false);
		rootNode.markDirty(DirtyType.RenderState);

		textureRenderer.render(rootNode, texture, Renderer.BUFFER_COLOR_AND_DEPTH);
		TextureState screen = new TextureState();
		screen.setTexture(texture);
		screen.setEnabled(true);
		quad.setRenderState(screen);
	}

	public Camera getCamera() {
		return camera;
	}

	public Node getNode() {
		return rootNode;
	}

	public final void setNodeToRender(Node node) {
		rootNode = node;
		
		final RenderState zbs = new ZBufferState();
		zbs.setEnabled(true);
      rootNode.setRenderState(zbs);
	}

	@Override
	protected void drawComponent(Renderer renderer) {
		super.drawComponent(renderer);
	}

	public final void setRttOffset(int x, int y) {
		quad.setTranslation(x + (getContentWidth() >> 1), y + (getContentHeight() >> 1), 0);
	}
}