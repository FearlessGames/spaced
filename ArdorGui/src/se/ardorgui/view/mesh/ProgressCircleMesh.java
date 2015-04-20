package se.ardorgui.view.mesh;

import com.ardor3d.math.FastMath;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Vector2;
import com.ardor3d.scenegraph.FloatBufferDataUtil;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.util.geom.BufferUtils;

import java.nio.FloatBuffer;

// Keeping this class as reference
@Deprecated
public class ProgressCircleMesh extends Mesh {
	private static final long serialVersionUID = 1L;

	public ProgressCircleMesh(final String name, final float width, final float height, final float fillPercent) {
		super(name);

		getMeshData().setVertexBuffer(BufferUtils.createVector3Buffer(6));
		getMeshData().setNormalBuffer(BufferUtils.createVector3Buffer(6));
		getMeshData().setIndexBuffer(BufferUtils.createIntBuffer(12));
		getMeshData().setColorBuffer(null);
		getMeshData().setTextureCoords(FloatBufferDataUtil.makeNew(new Vector2[6]), 0);

		for (int i = 0; i < 4; i++) {
			getMeshData().getIndices().put(0);
			getMeshData().getIndices().put(1 + (i + 1));
			getMeshData().getIndices().put(1 + i);
		}

		getMeshData().getNormalBuffer().rewind();
		for (int i=0;i<getMeshData().getVertexCount();i++) {
			getMeshData().getNormalBuffer().put(0).put(0).put(1);
        }

		update(width, height, fillPercent);
    }

	public void update(final float width, final float height, final float fillPercent) {
		final FloatBuffer vb = getMeshData().getVertexBuffer();
		final FloatBuffer tb = getMeshData().getTextureCoords(0).getBuffer();
		vb.rewind();
		tb.rewind();
		final float x = width / 2f;
		final float y = height / 2f;

		final int tri = (int)(fillPercent * 4.0f);
		final double vB = ((fillPercent * 4.0f) - tri) * MathUtils.HALF_PI;
		final double vC = MathUtils.PI - MathUtils.HALF_PI / 2 - vB;
		final double b = (FastMath.sin(vB) / FastMath.sin(vC));
		final double length = FastMath.sqrt(2);
		final float interpolation = (float)(b / length);

		vb.put(0).put(0).put(0);
		tb.put(0.5f).put(0.5f);

		final float[] X = new float[4];
		final float[] Y = new float[4];
		final float[] U = new float[4];
		final float[] V = new float[4];
		X[0] = 0; X[1] = x; X[2] =  0; X[3] = -x;
		Y[0] = y; Y[1] = 0; Y[2] = -y; Y[3] =  0;
		U[0] = 0; U[1] = 0; U[2] =  1; U[3] =  1;
		V[0] = 1; V[1] = 0; V[2] =  0; V[3] =  1;

		for(int t=0;t<5; t++) {
			final int vertInterpolate = tri + 1;
			if (t < vertInterpolate) {				// Add the vert
				final int ind = t%4;						// Index to the vertex
				vb.put(X[ind]).put(Y[ind]).put(0);
				tb.put(U[ind]).put(V[ind]);
			} else if (t == vertInterpolate) {		// Interpolate
				final int indPrev = (t-1)%4;	// Index to the previous vertex
				final int ind = t%4;			// Index to the vertex
				vb.put(X[indPrev] + (X[ind] - X[indPrev]) * interpolation);
				vb.put(Y[indPrev] + (Y[ind] - Y[indPrev]) * interpolation);
				vb.put(0);
				tb.put(U[indPrev] + (U[ind] - U[indPrev]) * interpolation);
				tb.put(V[indPrev] + (V[ind] - V[indPrev]) * interpolation);
			} else {								// Discard triangle
				vb.put(0).put(0).put(0);
				tb.put(0.5f).put(0.5f);
			}
		}
	}
}