package se.ardorgui.view.views;

import com.ardor3d.math.Vector2;
import com.ardor3d.scenegraph.Mesh;
import se.ardorgui.components.progress.Progress;
import se.ardorgui.components.progress.ProgressViewInterface;
import se.ardorgui.view.ComponentViewUtil;
import se.ardortech.math.AARectangle;
import se.ardortech.meshgenerator.MeshUtil;
import se.ardortech.meshgenerator.shapes.ProgressCircleQuadMeshDataGenerator;

public class ProgressCircleView extends ComponentLeafView<Mesh> implements ProgressViewInterface {
	private final ProgressCircleQuadMeshDataGenerator generator;

	public ProgressCircleView(final Mesh mesh, final float width, final float height, final float fillPercent, ProgressCircleQuadMeshDataGenerator generator) {
		super(mesh);
		this.generator = generator;
		update(width, height, fillPercent);
		ComponentViewUtil.setupComponent(mesh);
	}

	@Override
	public void onFillChanged(final Progress progress) {
		update(progress.getArea().getWidth(), progress.getArea().getHeight(), progress.getFillPercent());
	}

	private void update(final float width, final float height, final float fillPercent) {
		MeshUtil.rewind(getSpatial().getMeshData());
		generator.getRectangle().set(AARectangle.fromCenterSize(new Vector2(0, 0), new Vector2(width, height)));
		generator.setFillPercent(fillPercent);
		generator.getVertexData(getSpatial().getMeshData().getVertexBuffer());
		generator.getTextureData(getSpatial().getMeshData().getTextureCoords(0).getBuffer());
	}
}