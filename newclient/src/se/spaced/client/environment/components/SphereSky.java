package se.spaced.client.environment.components;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.FogState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.shape.Sphere;
import se.ardortech.TextureManager;

import javax.inject.Inject;

public class SphereSky implements Sky {
	private Node skyNode;

	@Inject
	public SphereSky(final TextureManager textureManager) {
		skyNode = new Node("SphereSky");
		final Sphere skySphere = new Sphere("The sky", 25, 35, 400);
		skySphere.setRandomColors();
		skySphere.setViewFromInside(true);
		disableFog(skySphere);
		disableCulling(skySphere);
		//disableZBuffer(skySphere); //TODO: why is this bugged?
		preBucketMesh(skySphere);
		setMaterial(skySphere);
		skyNode.attachChild(skySphere);
	}


	private void setMaterial(Mesh mesh) {
		MaterialState ms = new MaterialState();
		ms.setAmbient(new ColorRGBA(0.79f, 0.79f, 0.89f, 0.99f));
		ms.setDiffuse(new ColorRGBA(-0.75f, -0.99f, -1.483f, 1));
		ms.setEmissive(new ColorRGBA(0.025f, -0.15f, 0.18f, 1));
		mesh.setRenderState(ms);
	}

	@Override
	public void update(Camera cam) {
		skyNode.setTranslation(cam.getLocation());
	}

	private void preBucketMesh(Mesh mesh) {
		mesh.getSceneHints().setRenderBucketType(RenderBucketType.PreBucket);
	}

	private void disableZBuffer(Mesh mesh) {
		ZBufferState zbuff = new ZBufferState();
		zbuff.setEnabled(false);
		mesh.setRenderState(zbuff);
	}

	private void disableCulling(Mesh mesh) {
		CullState cs = new CullState();
		cs.setCullFace(CullState.Face.Front);
		mesh.getSceneHints().setCullHint(CullHint.Never);
	}

	private void disableFog(Mesh mesh) {
		final FogState fs = new FogState();
		fs.setEnabled(false);
		mesh.setRenderState(fs);
	}

	@Override
	public void init(Node node) {
		node.attachChild(skyNode);
	}

	@Override
	public Node getSkyNode() {
		return skyNode;
	}
}
