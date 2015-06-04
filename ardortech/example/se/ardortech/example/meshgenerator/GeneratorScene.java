package se.ardortech.example.meshgenerator;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.CullState.Face;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.MaterialState.ColorMaterial;
import com.ardor3d.scenegraph.Mesh;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.example.BaseExampleScene;
import se.ardortech.math.AABox;
import se.ardortech.math.AARectangle;
import se.ardortech.math.SpacedVector3;
import se.ardortech.meshgenerator.CombinedMeshDataGenerator;
import se.ardortech.meshgenerator.MeshFactory;
import se.ardortech.meshgenerator.MeshUtil;
import se.ardortech.meshgenerator.shapes.BoxMeshDataGenerator;
import se.ardortech.meshgenerator.shapes.ProgressCircleQuadMeshDataGenerator;
import se.ardortech.meshgenerator.shapes.QuadMeshDataGenerator;
import se.ardortech.render.DebugRender;
import se.ardortech.render.ScreenshotRender;

@Singleton
public class GeneratorScene extends BaseExampleScene {
	private Mesh mesh;
	private final MeshFactory meshFactory = new MeshFactory();
	private ProgressCircleQuadMeshDataGenerator progressGenerator;
	private CombinedMeshDataGenerator generator;

	@Inject
	public GeneratorScene(DebugRender debugRender, ScreenshotRender screenshotRender) {
		super(debugRender, screenshotRender);
	}

	@Override
	protected void setUp() {
		generator = new CombinedMeshDataGenerator();
		float size = 1.0f;
		float spacing = 0.1f;
		float stride = size + spacing;
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				for (int z = 0; z < 4; z++) {
					SpacedVector3 min = new SpacedVector3(x * stride, y * stride, z * stride);
					SpacedVector3 max = min.add(new SpacedVector3(size, size, size));
					generator.add(new BoxMeshDataGenerator(new AABox(min, max)));
				}
			}
		}
		generator.add(new QuadMeshDataGenerator(new AARectangle(new Vector2(-(stride * 4 - spacing), 0), new Vector2(0, (stride * 4 - spacing)))));
		progressGenerator = new ProgressCircleQuadMeshDataGenerator(new AARectangle(new Vector2(0, 5), new Vector2(5, 10)));
		generator.add(progressGenerator);
		mesh = meshFactory.createMesh(generator);
		final CullState cs = new CullState();
		cs.setCullFace(Face.Back);
		mesh.setRenderState(cs);
		final MaterialState ms = new MaterialState();
        ms.setColorMaterial(ColorMaterial.Diffuse);
        mesh.setRenderState(ms);
        mesh.setTranslation(new Vector3(0, 0, -10));
        mesh.setModelBound(new BoundingBox());
        mesh.updateWorldBound(true);
        mesh.setRandomColors();
		root.attachChild(mesh);
	}

	public Mesh getMesh() {
		return mesh;
	}

	public void update(double timeInSeconds) {
		progressGenerator.setFillPercent((float)timeInSeconds - (int)timeInSeconds);
		MeshUtil.rewind(mesh.getMeshData());
		generator.getData(mesh.getMeshData());
	}
}