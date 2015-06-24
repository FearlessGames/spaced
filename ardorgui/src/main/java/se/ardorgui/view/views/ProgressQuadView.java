package se.ardorgui.view.views;

import com.ardor3d.math.Vector2;
import com.ardor3d.math.type.ReadOnlyVector2;
import com.ardor3d.scenegraph.Mesh;
import se.ardorgui.components.progress.Progress;
import se.ardorgui.components.progress.ProgressViewInterface;
import se.ardorgui.view.ComponentViewUtil;
import se.ardortech.math.AARectangle;
import se.ardortech.math.Rectangle;
import se.ardortech.meshgenerator.MeshUtil;
import se.ardortech.meshgenerator.shapes.QuadMeshDataGenerator;

public class ProgressQuadView extends ComponentLeafView<Mesh> implements ProgressViewInterface {
	private final Rectangle textureUV;
	private final QuadMeshDataGenerator generator;

	public ProgressQuadView(final Mesh mesh, final float width, final float height, final float fillPercent, final Rectangle textureUV, QuadMeshDataGenerator generator) {
		super(mesh);
		this.textureUV = textureUV;
		this.generator = generator;

		update(width, height, fillPercent);
		ComponentViewUtil.setupComponent(mesh);
	}

	@Override
	public void onFillChanged(final Progress progress) {
		update(progress.getArea().getWidth(), progress.getArea().getHeight(), progress.getFillPercent());
	}

	private Rectangle shrink(Rectangle rectangle, Rectangle factors) {
		ReadOnlyVector2 size = rectangle.getSize();
		return new AARectangle(new Vector2(rectangle.getMin().getX() + factors.getMin().getX() * size.getX(),
											   rectangle.getMin().getY() + factors.getMin().getY() * size.getY()),
								   new Vector2(rectangle.getMin().getX() + factors.getMax().getX() * size.getX(),
										   	   rectangle.getMin().getY() + factors.getMax().getY() * size.getY()));
	}

	private void update(final float width, final float height, final float fillPercent) {
		MeshUtil.rewind(getSpatial().getMeshData());
		Rectangle fillRect = new AARectangle(new Vector2(0, 0), new Vector2(fillPercent, 1.0f));
		generator.getRectangle().set(shrink(AARectangle.fromCenterSize(new Vector2(0, 0), new Vector2(width, height)), fillRect));
		generator.getUv().set(shrink(textureUV, fillRect));
		generator.getVertexData(getSpatial().getMeshData().getVertexBuffer());
		generator.getTextureData(getSpatial().getMeshData().getTextureCoords(0).getBuffer());
	}
}