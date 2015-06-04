package se.ardortech.example;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.Scene;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.intersection.PickingUtil;
import com.ardor3d.intersection.PrimitivePickResults;
import com.ardor3d.light.PointLight;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.event.DirtyType;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.GameTaskQueueManager;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.render.DebugRender;
import se.ardortech.render.ScreenshotRender;

public abstract class BaseExampleScene implements Scene {
	private static Logger logger = LoggerFactory.getLogger(BaseExampleScene.class);
	protected final Node root = new Node();
	private final DebugRender debugRender;
	private final ScreenshotRender screenshotRender;

	@Inject
	public BaseExampleScene(DebugRender debugRender, ScreenshotRender screenshotRender) {
		this.debugRender = debugRender;
		this.screenshotRender = screenshotRender;
	}

	public void init() {
		logger.debug("Init scene");
		// ZBuffer
		final ZBufferState buf = new ZBufferState();
		buf.setEnabled(true);
		buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
		root.setRenderState(buf);

		// Render bucket type
		root.getSceneHints().setRenderBucketType(RenderBucketType.Opaque);

		// Light
		final PointLight light = new PointLight();
		light.setLocation(new Vector3(-5, 5, -5));
		light.setEnabled(true);
		LightState lightState = new LightState();
		lightState.setEnabled(true);
		lightState.attach(light);
		root.setRenderState(lightState);

		// Wireframe
		WireframeState wireframeState = new WireframeState();
		wireframeState.setEnabled(false);
		root.setRenderState(wireframeState);

		setUp();
	}

	protected abstract void setUp();

	@Override
	@MainThread
	public boolean renderUnto(final Renderer renderer) {
		root.updateGeometricState(0.0f, true);
		GameTaskQueueManager.getManager(0).getQueue(GameTaskQueue.RENDER).execute();

		renderer.draw(root);
		debugRender.render(root, renderer);
		screenshotRender.render(renderer);
		return true;
	}

	@Override
	public PickResults doPick(final Ray3 pickRay) {
		final PrimitivePickResults pickResults = new PrimitivePickResults();
		pickResults.setCheckDistance(true);
		PickingUtil.findPick(root, pickRay, pickResults);
		return pickResults;
	}

	public void toggleLight() {
		RenderState lightState = root.getLocalRenderStates().get(RenderState.StateType.Light);
		lightState.setEnabled(!lightState.isEnabled());
		// Either an update or a markDirty is needed here since we did not touch the affected spatial directly.
		root.markDirty(DirtyType.RenderState);
	}

	public void toggleWireframe() {
		RenderState wireframeState = root.getLocalRenderStates().get(RenderState.StateType.Wireframe);
		wireframeState.setEnabled(!wireframeState.isEnabled());
		// Either an update or a markDirty is needed here since we did not touch the affected spatial directly.
		root.markDirty(DirtyType.RenderState);
	}
}