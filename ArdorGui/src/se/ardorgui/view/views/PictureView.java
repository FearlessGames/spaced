package se.ardorgui.view.views;

import com.ardor3d.math.Vector2;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import se.ardorgui.components.base.Component;
import se.ardorgui.components.base.ComponentContainerViewInterface;
import se.ardortech.meshgenerator.MeshUtil;
import se.ardortech.meshgenerator.shapes.QuadMeshDataGenerator;

public class PictureView extends ComponentContainerPanelView implements ComponentContainerViewInterface {
	protected final QuadMeshDataGenerator generator;

	public PictureView(final Node node, final Mesh picture, final QuadMeshDataGenerator generator) {
		super(node, picture);
		this.generator = generator;
	}

	@Override
	public void onResize(final Component component) {
		generator.getRectangle().setCenterSize(new Vector2(0, 0), new Vector2(component.getArea().getWidth(), component.getArea().getHeight()));
		MeshUtil.rewind(getMesh().getMeshData());
		generator.getData(getMesh().getMeshData());
	}
}