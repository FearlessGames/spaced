package se.ardorgui.view.mesh;

import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Vector2;
import com.ardor3d.scenegraph.FloatBufferDataUtil;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.util.geom.BufferUtils;

import java.nio.FloatBuffer;

// Keeping this class as reference
@Deprecated
public class ProgressCircleQuadMesh extends Mesh {
	private static final long serialVersionUID = 1L;

	private static final int numTris = 8;
	private static final int numVerts = numTris + 2;
	private static final int numIndices = numTris * 3;

	public ProgressCircleQuadMesh(final String name, final float width, final float height, final float fillPercent) {
		super(name);

		getMeshData().setVertexBuffer(BufferUtils.createVector3Buffer(numVerts));
		getMeshData().setNormalBuffer(BufferUtils.createVector3Buffer(numVerts));
		getMeshData().setIndexBuffer(BufferUtils.createIntBuffer(numIndices));
		getMeshData().setColorBuffer(null);
		getMeshData().setTextureCoords(FloatBufferDataUtil.makeNew(new Vector2[numVerts]), 0);

		for (int i = 0; i < numTris; i++) {
			getMeshData().getIndices().put(0);
			getMeshData().getIndices().put(1 + (i + 1));
			getMeshData().getIndices().put(1 + i);
		}

		getMeshData().getNormalBuffer().rewind();
		for (int i=0; i < getMeshData().getVertexCount(); i++) {
			getMeshData().getNormalBuffer().put(0).put(0).put(1);
        }

		update(width, height, fillPercent);
    }

	public void update(final float width, final float height, float fillPercent) {
		final FloatBuffer vb = getMeshData().getVertexBuffer();
		final FloatBuffer tb = getMeshData().getTextureCoords(0).getBuffer();
		vb.rewind();
		tb.rewind();
		final float halfWidth = width / 2f;
		final float halfHeight = height / 2f;

		final int tri = (int)(fillPercent * 8.0f);
		final float[] vX = new float[9];
		final float[] vY = new float[9];
		vX[0] = 0; vX[1] = 1; vX[2] = 1; vX[3] = 1; vX[4] = 0; vX[5] = -1; vX[6] = -1; vX[7] = -1; vX[8] = 0;
		vY[0] = 1; vY[1] = 1; vY[2] = 0; vY[3] = -1; vY[4] = -1; vY[5] = -1; vY[6] = 0; vY[7] = 1; vY[8] = 1;

		// Center vertex
		vb.put(0.0f).put(0.0f).put(0.0f);
		tb.put(0.5f).put(0.5f);

		// Calculate circle position
		double circlePos = fillPercent * MathUtils.TWO_PI;
		float circleX = (float)Math.sin(circlePos);
		float circleY = (float)Math.cos(circlePos);

		if (tri == 0 || tri == 3 || tri == 4 || tri == 7) {
			circleX = circleX * 1 / circleY;
			circleY = vY[tri];
			if (tri == 3 || tri == 4) {
				circleX = -circleX;
			}
		} else {
			circleY = circleY * 1 / circleX;
			circleX = vX[tri];
			if (tri == 5 || tri == 6) {
				circleY = -circleY;
			}
		}
		for (int i = 0; i < numTris + 1; i++) {
			if (i > tri) {
				circleX = vX[i];
				circleY = vY[i];
			}
			vb.put(circleX * halfWidth).put(circleY * halfHeight).put(0.0f);
			tb.put(0.5f + circleX / 2).put(0.5f + circleY / 2);
		}
	}
}