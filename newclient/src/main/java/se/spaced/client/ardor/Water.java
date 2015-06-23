package se.spaced.client.ardor;

import com.ardor3d.renderer.Camera;
import com.ardor3d.scenegraph.Node;
import se.ardortech.water.WaterNode;

public abstract class Water {
	protected WaterNode waterNode;
	protected final double textureScale = 0.06;
	protected double farPlane = 42000.0;
	protected final Camera waterCamera = new Camera(1, 1);
	private Node root;
	private Node reflectedNode;

	public void update(Camera cam, double dt) {
		farPlane = cam.getFrustumFar();
	}

	public abstract void init();

	public void install(Node root, Node reflectedNode) {
		this.root = root;
		this.reflectedNode = reflectedNode;
		init();
	}

	protected void onReady() {
		root.attachChild(waterNode);
		waterNode.addReflectedScene(reflectedNode);
	}
}
