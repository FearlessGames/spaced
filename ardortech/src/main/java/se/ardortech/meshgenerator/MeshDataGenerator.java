package se.ardortech.meshgenerator;

import com.ardor3d.scenegraph.IndexBufferData;
import com.ardor3d.scenegraph.MeshData;

import java.nio.FloatBuffer;

public interface MeshDataGenerator {
    int getNumVertices();
    int getNumIndices();
    int getNumNormals();
    int getNumTextureCoords();
    void getVertexData(FloatBuffer vertexBuffer);
    void getNormalData(FloatBuffer normalBuffer);
    void getTextureData(FloatBuffer textureBuffer);
    void getIndexData(IndexBufferData indexBuffer, int offset);
    void getData(MeshData meshData);
}