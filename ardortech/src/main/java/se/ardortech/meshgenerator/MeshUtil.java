package se.ardortech.meshgenerator;

import com.ardor3d.scenegraph.MeshData;

public class MeshUtil {
	private MeshUtil() {
	}

	public static void rewind(MeshData meshData) {
		meshData.getIndexBuffer().rewind();
        meshData.getVertexBuffer().rewind();
        meshData.getNormalBuffer().rewind();
        meshData.getTextureBuffer(0).rewind();
	}
}
