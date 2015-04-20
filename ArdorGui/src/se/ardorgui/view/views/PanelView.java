package se.ardorgui.view.views;

import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import se.ardorgui.components.base.Component;
import se.ardorgui.components.base.ComponentContainerViewInterface;
import se.ardortech.meshgenerator.MeshUtil;
import se.ardortech.meshgenerator.shapes.PanelMeshDataGenerator;

public class PanelView extends ComponentContainerPanelView implements ComponentContainerViewInterface {
	protected final PanelMeshDataGenerator generator;

	public PanelView(final Node node, final Mesh panel, PanelMeshDataGenerator generator) {
		super(node, panel);
		this.generator = generator;
	}

	@Override
	public void onResize(final Component component) {
		generator.setSize(component.getArea().getWidth(), component.getArea().getHeight());
		MeshUtil.rewind(getMesh().getMeshData());
		generator.getData(getMesh().getMeshData());
	}
}