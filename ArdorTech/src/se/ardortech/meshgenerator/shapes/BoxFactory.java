package se.ardortech.meshgenerator.shapes;

import com.ardor3d.scenegraph.Mesh;
import se.ardortech.math.Box;
import se.ardortech.meshgenerator.MeshFactory;

public class BoxFactory extends MeshFactory {
    public Mesh create(Box box) {
        return createMesh(new BoxMeshDataGenerator(box));
    }
}