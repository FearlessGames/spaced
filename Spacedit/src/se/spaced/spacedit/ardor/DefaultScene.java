package se.spaced.spacedit.ardor;

import com.ardor3d.framework.Scene;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.intersection.PickingUtil;
import com.ardor3d.intersection.PrimitivePickResults;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.util.ReadOnlyTimer;

public class DefaultScene implements Scene {
	private final Node rootNode;
	private final Sun sun;
	private final PickResults pickResults;

	public DefaultScene(Node rootNode, Sun sun) {
		this.rootNode = rootNode;
		this.sun = sun;
		pickResults = new PrimitivePickResults();
		pickResults.setCheckDistance(true);
	}

	public Node getRootNode() {
		return rootNode;
	}

	private void createSun() {
		sun.setAxis(new Vector3(1.0, 0.3, 0.0));
		sun.setPosition(new Vector3(0.1, 1, 0));
		sun.setDaySpeed(0.2);
		rootNode.attachChild(sun.getNode());
	}

	@Override
	public boolean renderUnto(final Renderer renderer) {
		renderer.draw(rootNode);
		return true;
	}

	@Override
	public PickResults doPick(final Ray3 pickRay) {
		PickingUtil.findPick(rootNode, pickRay, pickResults);
		return pickResults;
	}

	public void setSpatial(Spatial spatial) {
		rootNode.attachChild(spatial);
		getRootNode().updateGeometricState(0, true);
	}

	public void init() {
		initZBuffer();
		createSun();
		LightState lightState = new LightState();
		lightState.setEnabled(true);
		lightState.attach(sun.getLight());
		rootNode.setRenderState(lightState);
	}

	private void initZBuffer() {
		final ZBufferState buf = new ZBufferState();
		buf.setEnabled(true);
		buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
		getRootNode().setRenderState(buf);
	}

	public void update(ReadOnlyTimer timer, Camera camera) {
		getRootNode().updateGeometricState(timer.getTimePerFrame(), true);
		sun.update(timer.getTimePerFrame(), camera);
	}
}