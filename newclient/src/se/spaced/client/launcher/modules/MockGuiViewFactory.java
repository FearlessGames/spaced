package se.spaced.client.launcher.modules;

import com.ardor3d.image.Texture;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.ui.text.BMFont;
import com.ardor3d.ui.text.BMText;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardorgui.view.GuiViewFactory;
import se.ardorgui.view.views.ButtonView;
import se.ardorgui.view.views.ComponentContainerView;
import se.ardorgui.view.views.CursorView;
import se.ardorgui.view.views.LabelView;
import se.ardorgui.view.views.PanelView;
import se.ardorgui.view.views.PictureView;
import se.ardorgui.view.views.ProgressCircleView;
import se.ardorgui.view.views.ProgressQuadView;
import se.ardorgui.view.views.RttView;
import se.ardortech.math.Rectangle;
import se.ardortech.meshgenerator.MeshFactory;
import se.ardortech.meshgenerator.shapes.PanelMeshDataGenerator;

import java.awt.Insets;

import static se.mockachino.Mockachino.*;

@Singleton
public final class MockGuiViewFactory implements GuiViewFactory {

	@Inject
	public MockGuiViewFactory() {
	}

	@Override
	public LabelView createLabelView(String textString, ColorRGBA color, BMFont font, float fontSize, BMText.Align textAlignment) {
		return mock(LabelView.class);
	}

	@Override
	public ProgressQuadView createProgressBarView(int width, int height, float fillPercent, Texture texture, Rectangle visibleRect) {
		throw new RuntimeException("NYI");
	}

	@Override
	public ProgressCircleView createProgressCircleView(int width, int height, float fillPercent, Texture texture) {
		throw new RuntimeException("NYI");
	}

	@Override
	public PanelView createPanelView(int width, int height, Insets insets, Texture texture) {
		MeshFactory mf = new MeshFactory();
		PanelMeshDataGenerator generator = new PanelMeshDataGenerator(null, null, 0, 0, 0, 0);
		Mesh mesh = new Mesh();
		mesh.setMeshData(mf.createMeshData(generator));
		return new PanelView(new Node(), mesh, generator);
	}

	@Override
	public CursorView createCursorView(int width, int height, ColorRGBA color) {
		throw new RuntimeException("NYI");
	}

	@Override
	public PictureView createPictureView(Texture texture, int width, int height) {
		throw new RuntimeException("NYI");
	}

	@Override
	public RttView createRttView(Texture texture, int width, int height) {
		throw new RuntimeException("NYI");
	}

	@Override
	public ButtonView createButtonView(int width, int height, Insets insets, Texture textureUp, Texture textureDown, Texture textureOver) {
		throw new RuntimeException("NYI");
	}

	@Override
	public ComponentContainerView createComponentContainerView() {
		throw new RuntimeException("NYI");
	}

	@Override
	public ComponentContainerView createComponentViewNode(Node node) {
		return new ComponentContainerView(node);
	}

	@Override
	public Texture getTexture(String fileName) {
		return new MockTexture();
	}
}
