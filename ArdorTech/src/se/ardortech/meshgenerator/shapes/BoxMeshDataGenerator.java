package se.ardortech.meshgenerator.shapes;

import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.IndexBufferData;
import com.ardor3d.scenegraph.MeshData;
import se.ardortech.math.Box;
import se.ardortech.meshgenerator.MeshDataGenerator;

import java.nio.FloatBuffer;

public class BoxMeshDataGenerator implements MeshDataGenerator {
    private Box box;
    final int[] indices = {2, 1, 0, 3, 2, 0, 6, 5, 4, 7, 6, 4, 10, 9, 8, 11, 10, 8, 14, 13, 12, 15, 14, 12, 18, 17, 16, 19, 18, 16, 22, 21, 20, 23, 22, 20};

	public BoxMeshDataGenerator(final Box box) {
		this.box = box;
	}

    public Box getBox() {
		return box;
	}

	public void setBox(Box box) {
		this.box = box;
	}

	@Override
	public int getNumVertices() {
		return 24;
	}

	@Override
    public int getNumNormals() {
    	return 24;
    }

    @Override
    public int getNumTextureCoords() {
    	return 24;
    }

    @Override
    public int getNumIndices() {
    	return 36;
    }

    @Override
    public void getData(final MeshData meshData) {
        getIndexData(meshData.getIndices(), meshData.getVertexBuffer().position() / 3);
        getVertexData(meshData.getVertexBuffer());
        getNormalData(meshData.getNormalBuffer());
        getTextureData(meshData.getTextureBuffer(0));
    }

    public Vector3[] computeVertices() {
        final Vector3[] rVal = new Vector3[8];
        rVal[0] = new Vector3(box.getMin().getX(), box.getMin().getY(), box.getMin().getZ());
        rVal[1] = new Vector3(box.getMax().getX(), box.getMin().getY(), box.getMin().getZ());
        rVal[2] = new Vector3(box.getMax().getX(), box.getMax().getY(), box.getMin().getZ());
        rVal[3] = new Vector3(box.getMin().getX(), box.getMax().getY(), box.getMin().getZ());
        rVal[4] = new Vector3(box.getMax().getX(), box.getMin().getY(), box.getMax().getZ());
        rVal[5] = new Vector3(box.getMin().getX(), box.getMin().getY(), box.getMax().getZ());
        rVal[6] = new Vector3(box.getMax().getX(), box.getMax().getY(), box.getMax().getZ());
        rVal[7] = new Vector3(box.getMin().getX(), box.getMax().getY(), box.getMax().getZ());
        return rVal;
    }

    @Override
    public void getVertexData(FloatBuffer vertexBuffer) {
        final Vector3[] vert = computeVertices(); // returns 8

        // Back
        put(vertexBuffer, vert[0]);
        put(vertexBuffer, vert[1]);
        put(vertexBuffer, vert[2]);
        put(vertexBuffer, vert[3]);

        // Right
        put(vertexBuffer, vert[1]);
        put(vertexBuffer, vert[4]);
        put(vertexBuffer, vert[6]);
        put(vertexBuffer, vert[2]);

        // Front
        put(vertexBuffer, vert[4]);
        put(vertexBuffer, vert[5]);
        put(vertexBuffer, vert[7]);
        put(vertexBuffer, vert[6]);

        // Left
        put(vertexBuffer, vert[5]);
        put(vertexBuffer, vert[0]);
        put(vertexBuffer, vert[3]);
        put(vertexBuffer, vert[7]);

        // Top
        put(vertexBuffer, vert[2]);
        put(vertexBuffer, vert[6]);
        put(vertexBuffer, vert[7]);
        put(vertexBuffer, vert[3]);

        // Bottom
        put(vertexBuffer, vert[0]);
        put(vertexBuffer, vert[5]);
        put(vertexBuffer, vert[4]);
        put(vertexBuffer, vert[1]);
    }

	private void put(FloatBuffer vertexBuffer, final Vector3 vert) {
		vertexBuffer.put(vert.getXf()).put(vert.getYf()).put(vert.getZf());
	}

	@Override
	public void getNormalData(FloatBuffer normalBuffer) {
        // back
        for (int i = 0; i < 4; i++) {
            normalBuffer.put(0).put(0).put(-1);
        }

        // right
        for (int i = 0; i < 4; i++) {
            normalBuffer.put(1).put(0).put(0);
        }

        // front
        for (int i = 0; i < 4; i++) {
            normalBuffer.put(0).put(0).put(1);
        }

        // left
        for (int i = 0; i < 4; i++) {
            normalBuffer.put(-1).put(0).put(0);
        }

        // top
        for (int i = 0; i < 4; i++) {
            normalBuffer.put(0).put(1).put(0);
        }

        // bottom
        for (int i = 0; i < 4; i++) {
            normalBuffer.put(0).put(-1).put(0);
        }
    }

	@Override
	public void getTextureData(FloatBuffer textureBuffer) {
        for (int i = 0; i < 6; i++) {
        	textureBuffer.put(1).put(0);
        	textureBuffer.put(0).put(0);
        	textureBuffer.put(0).put(1);
        	textureBuffer.put(1).put(1);
        }
    }

	@Override
    public void getIndexData(IndexBufferData indexBuffer, int offset) {
        for (int i = 0; i < getNumIndices(); i++) {
        	indexBuffer.put(indices[i] + offset);
        }
    }
}