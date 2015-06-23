package se.spaced.client.ardor;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.extension.ui.UIHud;
import com.ardor3d.framework.Scene;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.math.Ray3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.event.DirtyType;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.ardortech.render.DebugRender;
import se.ardortech.render.ScreenshotRender;
import se.spaced.client.environment.EnvironmentSystem;

@Singleton
public class SpacedScene implements Scene {
	private final Node root;
	private final Node entityNode;
	private final DebugRender debugRender;
	private final ScreenshotRender screenshotRender;
	private final Node propsNode;
	private final EnvironmentSystem environmentSystem;
	private final UIHud hud;

	@Inject
	public SpacedScene(
			DebugRender debugRender,
			ScreenshotRender screenshotRender,
			@Named("rootNode") Node root,
			@Named("entityNode") Node entityNode,
			@Named("propsNode") Node propsNode,
			EnvironmentSystem environmentSystem,
			UIHud hud) {
		this.root = root;
		this.debugRender = debugRender;
		this.screenshotRender = screenshotRender;
		this.entityNode = entityNode;
		this.propsNode = propsNode;
		this.environmentSystem = environmentSystem;
		this.hud = hud;
	}

	public void init() {
		root.attachChild(entityNode);
		root.attachChild(propsNode);
		environmentSystem.init(root);
	}

	@Override
	@MainThread
	public boolean renderUnto(final Renderer renderer) {
		// GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).execute();
		renderer.draw(root);
		debugRender.render(root, renderer);
		screenshotRender.render(renderer);
		renderer.renderBuckets();
		renderer.draw(hud);
		return true;
	}

	@Override
	public PickResults doPick(final Ray3 pickRay) {
		throw new UnsupportedOperationException("This method is not implemented, use Picker.class instead!");
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

	public void update(Camera camera, double dt) {
		environmentSystem.update(camera, dt);
	}
}