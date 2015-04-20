package se.ardorgui.components.ardoreventwrapper;

import com.ardor3d.extension.ui.UIComponent;
import com.ardor3d.image.Texture;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.renderer.ContextManager;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.util.geom.BufferUtils;
import se.ardorgui.view.ComponentViewUtil;
import se.ardortech.math.AARectangle;
import se.ardortech.meshgenerator.MeshDataGenerator;
import se.ardortech.meshgenerator.MeshUtil;
import se.ardortech.meshgenerator.shapes.ProgressCircleQuadMeshDataGenerator;


public class UIProgressCircle extends UIComponent {
	private final Mesh mesh;
	private final ProgressCircleQuadMeshDataGenerator generator;
	private float progress;
	private static final BlendState blendState = ComponentViewUtil.createBlendRGBMaxAlphaBlend();

	public UIProgressCircle() {
		this.mesh = new Mesh("ProgressCirle.mesh");
		generator = new ProgressCircleQuadMeshDataGenerator(AARectangle.fromCenterSize(new Vector2(0, 0),
				new Vector2(getContentWidth(), getContentHeight())));
		mesh.setMeshData(createMeshData(generator));
		generator.getData(mesh.getMeshData());
		ComponentViewUtil.setupComponent(mesh);
		update(getContentWidth(), getContentHeight(), 1.0f);
	}

	public void setTexture(Texture texture) {
		mesh.setRenderState(ComponentViewUtil.createTextureState(texture));
	}

	public void setPercentFilled(float progress) {
		this.progress = progress;
		update(getContentWidth(), getContentHeight(), progress);
	}

	public float getPercentFilled() {
		return progress;
	}

	private void update(int width, int height, float fillPercent) {
		MeshUtil.rewind(mesh.getMeshData());
		generator.getRectangle().set(AARectangle.fromCenterSize(new Vector2(0, 0), new Vector2(width, height)));
		generator.setFillPercent(fillPercent);
		generator.getVertexData(mesh.getMeshData().getVertexBuffer());
		generator.getTextureData(mesh.getMeshData().getTextureCoords(0).getBuffer());

	}

	@Override
	protected void predrawComponent(Renderer renderer) {
		super.predrawComponent(renderer);
		ContextManager.getCurrentContext().enforceState(blendState);
	}

	@Override
	protected void postdrawComponent(Renderer renderer) {
		super.postdrawComponent(renderer);
		ContextManager.getCurrentContext().clearEnforcedState(RenderState.StateType.Blend);
	}

	@Override
	protected void drawComponent(Renderer renderer) {

		ReadOnlyTransform worldTransform = getWorldTransform();
		mesh.setWorldTransform(worldTransform);
		mesh.setWorldTranslation(mesh.getWorldTranslation().add(getContentWidth() / 2, getContentHeight() / 2, 0, null));
		ReadOnlyColorRGBA color = getForegroundColor();
		mesh.setDefaultColor(color);
		mesh.render(renderer);
	}

	private MeshData createMeshData(MeshDataGenerator meshDataGenerator) {
		final MeshData meshData = new MeshData();
		meshData.setVertexBuffer(BufferUtils.createVector3Buffer(meshDataGenerator.getNumVertices()));
		meshData.setNormalBuffer(BufferUtils.createVector3Buffer(meshDataGenerator.getNumNormals()));
		meshData.setTextureBuffer(BufferUtils.createVector2Buffer(meshDataGenerator.getNumTextureCoords()), 0);
		meshData.setIndexBuffer(BufferUtils.createIntBuffer(meshDataGenerator.getNumIndices()));
		return meshData;
	}
}
