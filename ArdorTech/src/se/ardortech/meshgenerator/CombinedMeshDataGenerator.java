package se.ardortech.meshgenerator;

import com.ardor3d.scenegraph.IndexBufferData;
import com.ardor3d.scenegraph.MeshData;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class CombinedMeshDataGenerator implements MeshDataGenerator {
	private final Collection<MeshDataGenerator> meshDataGenerators = new ArrayList<MeshDataGenerator>();

	public CombinedMeshDataGenerator(final MeshDataGenerator... meshDataGenerators) {
		add(meshDataGenerators);
	}

	public void add(final MeshDataGenerator... meshDataGenerators) {
		this.meshDataGenerators.addAll(Arrays.asList(meshDataGenerators));
	}

	@Override
	public int getNumVertices() {
		int numVertices = 0;
		for (final MeshDataGenerator meshDataGenarator : meshDataGenerators) {
			numVertices += meshDataGenarator.getNumVertices();
		}
		return numVertices;
	}

	@Override
	public int getNumIndices() {
		int numIndices = 0;
		for (final MeshDataGenerator meshDataGenarator : meshDataGenerators) {
			numIndices += meshDataGenarator.getNumIndices();
		}
		return numIndices;
	}

	@Override
	public int getNumNormals() {
		int numNormals = 0;
		for (final MeshDataGenerator meshDataGenarator : meshDataGenerators) {
			numNormals += meshDataGenarator.getNumNormals();
		}
		return numNormals;
	}

	@Override
	public int getNumTextureCoords() {
		int numTextureCoords = 0;
		for (final MeshDataGenerator meshDataGenarator : meshDataGenerators) {
			numTextureCoords += meshDataGenarator.getNumTextureCoords();
		}
		return numTextureCoords;
	}

	@Override
	public void getData(final MeshData meshData) {
		for (final MeshDataGenerator meshDataGenarator : meshDataGenerators) {
			meshDataGenarator.getData(meshData);
		}
	}

	@Override
	public void getIndexData(IndexBufferData indexBuffer, int offset) {
		for (final MeshDataGenerator meshDataGenarator : meshDataGenerators) {
			meshDataGenarator.getIndexData(indexBuffer, offset);
		}
	}

	@Override
	public void getNormalData(FloatBuffer normalBuffer) {
		for (final MeshDataGenerator meshDataGenarator : meshDataGenerators) {
			meshDataGenarator.getNormalData(normalBuffer);
		}
	}

	@Override
	public void getTextureData(FloatBuffer textureBuffer) {
		for (final MeshDataGenerator meshDataGenarator : meshDataGenerators) {
			meshDataGenarator.getTextureData(textureBuffer);
		}
	}

	@Override
	public void getVertexData(FloatBuffer vertexBuffer) {
		for (final MeshDataGenerator meshDataGenarator : meshDataGenerators) {
			meshDataGenarator.getVertexData(vertexBuffer);
		}
	}
}