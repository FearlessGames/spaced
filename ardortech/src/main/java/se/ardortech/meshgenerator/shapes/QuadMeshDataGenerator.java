package se.ardortech.meshgenerator.shapes;

import com.ardor3d.math.Vector2;
import com.ardor3d.scenegraph.IndexBufferData;
import com.ardor3d.scenegraph.MeshData;
import se.ardortech.math.AARectangle;
import se.ardortech.math.Rectangle;
import se.ardortech.meshgenerator.MeshDataGenerator;

import java.nio.FloatBuffer;

public class QuadMeshDataGenerator implements MeshDataGenerator {
    private Rectangle rectangle;
    private Rectangle uv;

	public QuadMeshDataGenerator(final Rectangle rectangle, final Rectangle uv) {
		this.rectangle = rectangle;
		this.uv = uv;
	}

	public QuadMeshDataGenerator(final Rectangle rectangle) {
		this(rectangle, new AARectangle(new Vector2(0,0), new Vector2(1, 1)));
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

	@Override
	public int getNumVertices() {
		return 4;
	}

	@Override
    public int getNumNormals() {
    	return 4;
    }

    @Override
    public int getNumTextureCoords() {
    	return 4;
    }

    @Override
    public int getNumIndices() {
    	return 6;
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
    	vertexBuffer.put(rectangle.getMin().getXf()).put(rectangle.getMax().getYf()).put(0f);
    	vertexBuffer.put(rectangle.getMin().getXf()).put(rectangle.getMin().getYf()).put(0f);
    	vertexBuffer.put(rectangle.getMax().getXf()).put(rectangle.getMin().getYf()).put(0f);
    	vertexBuffer.put(rectangle.getMax().getXf()).put(rectangle.getMax().getYf()).put(0f);
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
    	textureBuffer.put(uv.getMin().getXf()).put(uv.getMax().getYf());
    	textureBuffer.put(uv.getMin().getXf()).put(uv.getMin().getYf());
    	textureBuffer.put(uv.getMax().getXf()).put(uv.getMin().getYf());
    	textureBuffer.put(uv.getMax().getXf()).put(uv.getMax().getYf());
    }

    @Override
    public void getIndexData(IndexBufferData indexBuffer, int offset) {
    	indexBuffer.put(offset + 0).put(offset + 1).put(offset + 2).put(offset + 0).put(offset + 2).put(offset + 3);
    }
}