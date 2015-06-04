package se.ardortech.meshgenerator.shapes;

import com.ardor3d.scenegraph.IndexBufferData;
import com.ardor3d.scenegraph.MeshData;
import se.ardortech.meshgenerator.MeshDataGenerator;

import java.awt.Insets;
import java.nio.FloatBuffer;

public class PanelMeshDataGenerator implements MeshDataGenerator {

	private Insets borderInsets;
	private Insets textureInsets;

	private final int textureWidth;
	private final int textureHeight;

	// TODO: Change width/height to a rectangle. With width/height its only possible to represent centered panels.
	private int width;
	private int height;

	private static final int[] indices = { 0, 1, 2,
		  1, 3, 2,
		  2, 3, 4,
		  3, 5, 4,
		  4, 5, 6,
		  5, 7, 6,
		  1, 8, 3,
		  3, 8, 9,
		  3, 9, 5,
		  5, 9, 10,
		  5, 10, 7,
		  7, 10, 11,
		  8, 12, 9,
		  9, 12, 13,
		  9, 13, 10,
		  10, 13, 14,
		  10, 14, 11,
		  11, 14, 15};

	public PanelMeshDataGenerator(Insets borderInsets, Insets textureInsets, int width, int height, int textureWidth, int textureHeight) {
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		setBorderInsets(borderInsets);
		setTextureInsets(textureInsets);
		setSize(width, height);
	}

	private void setBorderInsets(Insets borderInsets) {
		if (borderInsets == null) {
			this.borderInsets = new Insets(0, 0, 0, 0);
		} else {
			this.borderInsets = new Insets(borderInsets.top, borderInsets.left, borderInsets.bottom, borderInsets.right);
		}
	}

	private void setTextureInsets(Insets textureInsets) {
		if (textureInsets == null) {
			this.textureInsets = new Insets(0, 0, 0, 0);
		} else {
			this.textureInsets = new Insets(textureInsets.top, textureInsets.left, textureInsets.bottom, textureInsets.right);
		}
	}

	public void setSize(int width, int height) {
		this.width = Math.max(width, getMinWidth());
		this.height = Math.max(height, getMinHeight());
	}

	@Override
	public void getVertexData(FloatBuffer vertexBuffer) {
		int halfWidth = width / 2;
		int halfHeight = height / 2;
		vertexBuffer.put(-halfWidth).put(-halfHeight).put(0);
		vertexBuffer.put(borderInsets.left - halfWidth).put(-halfHeight).put(0);

		vertexBuffer.put(-halfWidth).put(borderInsets.bottom - halfHeight).put(0);
		vertexBuffer.put(borderInsets.left - halfWidth).put(borderInsets.bottom - halfHeight).put(0);

		vertexBuffer.put(-halfWidth).put(height - borderInsets.top - halfHeight).put(0);
		vertexBuffer.put(borderInsets.left - halfWidth).put(height - borderInsets.top - halfHeight).put(0);

		vertexBuffer.put(-halfWidth).put(height - halfHeight).put(0);
		vertexBuffer.put(borderInsets.left - halfWidth).put(height - halfHeight).put(0);

		vertexBuffer.put(width - borderInsets.right - halfWidth).put(-halfHeight).put(0);
		vertexBuffer.put(width - borderInsets.right - halfWidth).put(borderInsets.bottom - halfHeight).put(0);

		vertexBuffer.put(width - borderInsets.right - halfWidth).put(height - borderInsets.top - halfHeight).put(0);
		vertexBuffer.put(width - borderInsets.right - halfWidth).put(height - halfHeight).put(0);

		vertexBuffer.put(width - halfWidth).put(-halfHeight).put(0);
		vertexBuffer.put(width - halfWidth).put(borderInsets.bottom - halfHeight).put(0);

		vertexBuffer.put(width - halfWidth).put(height - borderInsets.top - halfHeight).put(0);
		vertexBuffer.put(width - halfWidth).put(height - halfHeight).put(0);
	}

	@Override
	public void getTextureData(FloatBuffer textureBuffer) {
		float fTextureHeight = textureHeight;
		float fTextureWidth = textureWidth;
		textureBuffer.put(textureInsets.left / fTextureWidth).put(textureInsets.bottom / fTextureHeight);
		textureBuffer.put((textureInsets.left + borderInsets.left) / fTextureWidth).put(textureInsets.bottom / fTextureHeight);

		textureBuffer.put(textureInsets.left / fTextureWidth).put((textureInsets.bottom + borderInsets.bottom) / fTextureHeight);
		textureBuffer.put((textureInsets.left + borderInsets.left) / fTextureWidth).put((textureInsets.bottom + borderInsets.bottom) / fTextureHeight);

		textureBuffer.put(textureInsets.left / fTextureWidth).put((fTextureHeight - (textureInsets.top + borderInsets.top)) / fTextureHeight);
		textureBuffer.put((textureInsets.left + borderInsets.left) / fTextureWidth).put((fTextureHeight - (textureInsets.top + borderInsets.top)) / fTextureHeight);

		textureBuffer.put(textureInsets.left / fTextureWidth).put((fTextureHeight - textureInsets.top ) / fTextureHeight);
		textureBuffer.put((textureInsets.left + borderInsets.left) / fTextureWidth).put((fTextureHeight - textureInsets.top ) / fTextureHeight);

		textureBuffer.put((fTextureWidth - (borderInsets.right + textureInsets.right)) / fTextureWidth).put(textureInsets.bottom / fTextureHeight);
		textureBuffer.put((fTextureWidth - (borderInsets.right + textureInsets.right)) / fTextureWidth).put((textureInsets.bottom + borderInsets.bottom) / fTextureHeight);

		textureBuffer.put((fTextureWidth - (borderInsets.right + textureInsets.right)) / fTextureWidth).put((fTextureHeight - (textureInsets.top + borderInsets.top)) / fTextureHeight);
		textureBuffer.put((fTextureWidth - (borderInsets.right + textureInsets.right)) / fTextureWidth).put((fTextureHeight - textureInsets.top ) / fTextureHeight);

		textureBuffer.put((fTextureWidth - textureInsets.right) / fTextureWidth).put(textureInsets.bottom / fTextureHeight);
		textureBuffer.put((fTextureWidth - textureInsets.right) / fTextureWidth).put((textureInsets.bottom + borderInsets.bottom) / fTextureHeight);

		textureBuffer.put((fTextureWidth - textureInsets.right) / fTextureWidth).put((fTextureHeight - (textureInsets.top + borderInsets.top)) / fTextureHeight);
		textureBuffer.put((fTextureWidth - textureInsets.right) / fTextureWidth).put((fTextureHeight - textureInsets.top ) / fTextureHeight);
	}

	public int getMinWidth() {
		return borderInsets.left + borderInsets.right;
	}

	public int getMinHeight() {
		return borderInsets.top + borderInsets.bottom;
	}

	@Override
	public int getNumIndices() {
		return 54;
	}

	@Override
	public int getNumNormals() {
		return 16;
	}

	@Override
	public int getNumTextureCoords() {
		return 16;
	}

	@Override
	public int getNumVertices() {
		return 16;
	}

	@Override
    public void getData(final MeshData meshData) {
        getIndexData(meshData.getIndices(), meshData.getVertexBuffer().position() / 3);
        getVertexData(meshData.getVertexBuffer());
        getNormalData(meshData.getNormalBuffer());
        getTextureData(meshData.getTextureBuffer(0));
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
	public void getIndexData(IndexBufferData indexBuffer, int offset) {
   	for (int i = 0; i < getNumIndices(); i++) {
			indexBuffer.put(indices[i] + offset);
		}
	}
}