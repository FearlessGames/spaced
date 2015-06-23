package se.spaced.client.ardor;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Quad;
import com.google.inject.Singleton;

import java.nio.FloatBuffer;

@Singleton
public class SimpleWater extends Water {
	private final Mesh waterQuad = new Quad("waterQuad", 1, 1);
	private final Node node = new Node();

	@Override
	public void init() {
		waterQuad.setDefaultColor(ColorRGBA.BLUE);
		waterQuad.setSolidColor(ColorRGBA.BLUE);

		final FloatBuffer normBuf = waterQuad.getMeshData().getNormalBuffer();
		normBuf.clear();
		normBuf.put(0).put(1).put(0);
		normBuf.put(0).put(1).put(0);
		normBuf.put(0).put(1).put(0);
		normBuf.put(0).put(1).put(0);
		node.attachChild(waterQuad);
	}

	@Override
	public void update(Camera cam, double dt) {
		super.update(cam, dt);

		waterCamera.set(cam);
		final ReadOnlyVector3 transVec = new Vector3(cam.getLocation().getX(), 0, cam.getLocation().getZ());
		setTextureCoords(0, transVec.getX(), -transVec.getZ(), textureScale);
		setVertexCoords(transVec.getX(), transVec.getY(), transVec.getZ());

	}

	private void setVertexCoords(final double x, final double y, final double z) {
		final FloatBuffer vertBuf = waterQuad.getMeshData().getVertexBuffer();
		vertBuf.clear();

		vertBuf.put((float) (x - farPlane)).put((float) y).put((float) (z - farPlane));
		vertBuf.put((float) (x - farPlane)).put((float) y).put((float) (z + farPlane));
		vertBuf.put((float) (x + farPlane)).put((float) y).put((float) (z + farPlane));
		vertBuf.put((float) (x + farPlane)).put((float) y).put((float) (z - farPlane));
	}

	private void setTextureCoords(final int buffer, double x, double y, double textureScale) {
		x *= textureScale * 0.011f;
		y *= textureScale * 0.011f;
		textureScale = farPlane * textureScale;
		FloatBuffer texBuf = waterQuad.getMeshData().getTextureBuffer(buffer);
		texBuf.clear();
		texBuf.put((float) x).put((float) (textureScale + y));
		texBuf.put((float) x).put((float) y);
		texBuf.put((float) (textureScale + x)).put((float) y);
		texBuf.put((float) (textureScale + x)).put((float) (textureScale + y));
	}
}