package se.ardortech.meshgenerator;

import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.util.geom.BufferUtils;

public class MeshFactory {
	public Mesh createMesh(MeshDataGenerator meshDataGenerator) {
		final Mesh mesh = new Mesh();
		mesh.setMeshData(createMeshData(meshDataGenerator));
		meshDataGenerator.getData(mesh.getMeshData());
		return mesh;
	}

	public MeshData createMeshData(MeshDataGenerator meshDataGenerator) {
		final MeshData meshData = new MeshData();
		meshData.setVertexBuffer(BufferUtils.createVector3Buffer(meshDataGenerator.getNumVertices()));
		meshData.setNormalBuffer(BufferUtils.createVector3Buffer(meshDataGenerator.getNumNormals()));
		meshData.setTextureBuffer(BufferUtils.createVector2Buffer(meshDataGenerator.getNumTextureCoords()), 0);
		meshData.setIndexBuffer(BufferUtils.createIntBuffer(meshDataGenerator.getNumIndices()));
		return meshData;
	}
}