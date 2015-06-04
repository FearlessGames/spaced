package se.ardortech.meshgenerator.shapes;

import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.type.ReadOnlyVector2;
import com.ardor3d.scenegraph.IndexBufferData;
import com.ardor3d.scenegraph.MeshData;
import se.ardortech.math.AARectangle;
import se.ardortech.math.Rectangle;
import se.ardortech.meshgenerator.MeshDataGenerator;

import java.nio.FloatBuffer;

public class ProgressCircleQuadMeshDataGenerator implements MeshDataGenerator {
	private static final int numTris = 8;
	private static final int numVerts = numTris + 2;
	private static final int numIndices = numTris * 3;

    private Rectangle rectangle;
    private Rectangle uv;				// TODO: use uv coords
    private float fillPercent;

	public ProgressCircleQuadMeshDataGenerator(final Rectangle rectangle, final Rectangle uv, float fillPercent) {
		this.rectangle = rectangle;
		this.uv = uv;
		this.fillPercent = fillPercent;
	}

	public ProgressCircleQuadMeshDataGenerator(final Rectangle rectangle) {
		this(rectangle, new AARectangle(new Vector2(0,0), new Vector2(1, 1)), 1.0f);
	}

    public Rectangle getRectangle() {
		return rectangle;
	}

	public void setRectangle(Rectangle rectangle) {
		this.rectangle = rectangle;
	}

	public Rectangle getUv() {
		return uv;
	}

	public void setUv(Rectangle uv) {
		this.uv = uv;
	}

	public float getFillPercent() {
		return fillPercent;
	}

	public void setFillPercent(float fillPercent) {
		this.fillPercent = fillPercent;
	}

	@Override
	public int getNumVertices() {
		return numVerts;
	}

	@Override
    public int getNumNormals() {
    	return numVerts;
    }

    @Override
    public int getNumTextureCoords() {
    	return numVerts;
    }

    @Override
    public int getNumIndices() {
    	return numIndices;
    }

    private int getIndexOffset(final MeshData meshData) {
    	return meshData.getVertexBuffer().position() / 3;
    }

    @Override
    public void getData(final MeshData meshData) {
        getIndexData(meshData.getIndices(), getIndexOffset(meshData));
        getVertexData(meshData.getVertexBuffer());
        getNormalData(meshData.getNormalBuffer());
        getTextureData(meshData.getTextureBuffer(0));
    }

    @Override
    public void getVertexData(FloatBuffer vertexBuffer) {
    	update(vertexBuffer, null);
    }

    // TODO: don't run this method twice (for vertices / texturecoords)!
	private void update(FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
		ReadOnlyVector2 size = rectangle.getSize();
		ReadOnlyVector2 center = rectangle.getCenter();
		final float halfWidth = size.getXf() / 2f;
		final float halfHeight = size.getYf() / 2f;

		final int tri = (int)(fillPercent * 8.0f);
		final float[] vX = new float[9];
		final float[] vY = new float[9];
		vX[0] = 0; vX[1] = 1; vX[2] = 1; vX[3] = 1; vX[4] = 0; vX[5] = -1; vX[6] = -1; vX[7] = -1; vX[8] = 0;
		vY[0] = 1; vY[1] = 1; vY[2] = 0; vY[3] = -1; vY[4] = -1; vY[5] = -1; vY[6] = 0; vY[7] = 1; vY[8] = 1;

		// Center vertex
		if (vertexBuffer != null) {
			vertexBuffer.put(center.getXf()).put(center.getYf()).put(0.0f);
		}
		if (textureBuffer != null) {
			textureBuffer.put(0.5f).put(0.5f);
		}

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
			if (vertexBuffer != null) {
				vertexBuffer.put(center.getXf() + circleX * halfWidth).put(center.getYf() + circleY * halfHeight).put(0.0f);
			}
			if (textureBuffer != null) {
				textureBuffer.put(0.5f + circleX / 2).put(0.5f + circleY / 2);
			}
		}
	}

    @Override
    public void getNormalData(FloatBuffer normalBuffer) {
    	if (normalBuffer != null) {
	        for (int i = 0; i < getNumNormals(); i++) {
	            normalBuffer.put(0).put(0).put(1);
	        }
    	}
    }

    @Override
    public void getTextureData(FloatBuffer textureBuffer) {
    	update(null, textureBuffer);
    }

    @Override
    public void getIndexData(IndexBufferData indexBuffer, int offset) {
    	for (int i = 0; i < numTris; i++) {
    		indexBuffer.put(offset + 0);
    		indexBuffer.put(offset + 1 + (i + 1));
    		indexBuffer.put(offset + 1 + i);
		}
    }
}