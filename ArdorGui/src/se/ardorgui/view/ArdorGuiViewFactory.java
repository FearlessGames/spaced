package se.ardorgui.view;

import com.ardor3d.image.Texture;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector2;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.ui.text.BMFont;
import com.ardor3d.ui.text.BMText;
import com.ardor3d.util.TextureManager;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardorgui.view.mesh.TextQuad;
import se.ardorgui.view.views.ButtonView;
import se.ardorgui.view.views.ComponentContainerView;
import se.ardorgui.view.views.CursorView;
import se.ardorgui.view.views.LabelView;
import se.ardorgui.view.views.PanelView;
import se.ardorgui.view.views.PictureView;
import se.ardorgui.view.views.ProgressCircleView;
import se.ardorgui.view.views.ProgressQuadView;
import se.ardorgui.view.views.RttView;
import se.ardortech.math.AARectangle;
import se.ardortech.math.Rectangle;
import se.ardortech.meshgenerator.MeshFactory;
import se.ardortech.meshgenerator.shapes.PanelMeshDataGenerator;
import se.ardortech.meshgenerator.shapes.ProgressCircleQuadMeshDataGenerator;
import se.ardortech.meshgenerator.shapes.QuadMeshDataGenerator;

import java.awt.Insets;

public class ArdorGuiViewFactory implements GuiViewFactory {
	private static final Logger logger = LoggerFactory.getLogger(ArdorGuiViewFactory.class);

	private final MeshFactory meshFactory;

	@Inject
	public ArdorGuiViewFactory(MeshFactory meshFactory) {
		this.meshFactory = meshFactory;
	}

	@Override
	public LabelView createLabelView(final String textString, final ColorRGBA color, final BMFont font, final float fontSize, final BMText.Align textAlignment) {
		return new LabelView(new TextQuad("TextQuad", textString, font, fontSize, textAlignment, true, color));
	}

	@Override
	public ProgressQuadView createProgressBarView(final int width, final int height, final float fillPercent, final Texture texture, final Rectangle textureUV) {
		QuadMeshDataGenerator generator = new QuadMeshDataGenerator(AARectangle.fromCenterSize(new Vector2(0, 0), new Vector2(width, height)));
		return new ProgressQuadView(setTexture(meshFactory.createMesh(generator), texture), width, height, fillPercent, textureUV, generator);
	}

	@Override
	public ProgressCircleView createProgressCircleView(final int width, final int height, final float fillPercent, final Texture texture) {
		ProgressCircleQuadMeshDataGenerator generator = new ProgressCircleQuadMeshDataGenerator(AARectangle.fromCenterSize(new Vector2(0, 0), new Vector2(width, height)));
		return new ProgressCircleView(setTexture(meshFactory.createMesh(generator), texture), width, height, fillPercent, generator);
	}

	@Override
	public PanelView createPanelView(final int width, final int height, final Insets insets, final Texture texture) {
		PanelMeshDataGenerator generator = new PanelMeshDataGenerator(insets, null, width, height, texture.getImage().getWidth(), texture.getImage().getHeight());
		return new PanelView(new Node("Node"), setTexture(meshFactory.createMesh(generator), texture), generator);
	}

	@Override
	public CursorView createCursorView(final int width, final int height, final ColorRGBA color) {
		QuadMeshDataGenerator generator = new QuadMeshDataGenerator(AARectangle.fromCenterSize(new Vector2(0, 0), new Vector2(width, height)));
		return new CursorView(new Node("Node"), meshFactory.createMesh(generator), color, generator);
	}

	@Override
	public PictureView createPictureView(final Texture texture, final int width, final int height) {
		QuadMeshDataGenerator generator = new QuadMeshDataGenerator(AARectangle.fromCenterSize(new Vector2(0, 0), new Vector2(width, height)));
		return new PictureView(new Node("Node"), setTexture(meshFactory.createMesh(generator), texture), generator);
	}

	@Override
	public RttView createRttView(final Texture texture, final int width, final int height) {
		QuadMeshDataGenerator generator = new QuadMeshDataGenerator(AARectangle.fromCenterSize(new Vector2(0, 0), new Vector2(width, height)));
		return new RttView(new Node("Node"), setTexture(meshFactory.createMesh(generator), texture), generator);
	}

	@Override
	public ButtonView createButtonView(final int width, final int height, final Insets insets, final Texture textureUp, final Texture textureDown, final Texture textureOver) {
		PanelMeshDataGenerator generator = new PanelMeshDataGenerator(insets, null, width, height, textureUp.getImage().getWidth(), textureUp.getImage().getHeight());
		return new ButtonView(new Node("Node"), setTexture(meshFactory.createMesh(generator), textureUp), generator, textureUp, textureDown, textureOver);
	}

	@Override
	public ComponentContainerView createComponentContainerView() {
		return new ComponentContainerView(new Node("Node"));
	}

	@Override
	public ComponentContainerView createComponentViewNode(final Node node) {
		return new ComponentContainerView(node);
	}

	public <T extends Mesh> T setTexture(final T mesh, final Texture texture) {
		mesh.setRenderState(ComponentViewUtil.createTextureState(texture));
		return mesh;
	}

	@Override
	public Texture getTexture(final String fileName) {
		Texture texture = TextureManager.load(fileName,
				Texture.MinificationFilter.Trilinear, TextureStoreFormat.GuessCompressedFormat, true);
		if (texture == null) {
			logger.info("Texture not found: " + fileName);
		} else {
			logger.info("Texture loaded: " + fileName);
		}
		return texture;
	}
}