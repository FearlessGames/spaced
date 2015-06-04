package se.ardorgui.view.views;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import se.ardorgui.components.base.Component;
import se.ardorgui.components.base.ComponentContainerViewInterface;
import se.ardorgui.view.ComponentColor;
import se.ardorgui.view.ComponentViewUtil;

public class ComponentContainerPanelView extends ComponentContainerView implements ComponentContainerViewInterface {
	private final Mesh mesh;
	private final ComponentColor componentColor;

	public ComponentContainerPanelView(final Node node, final Mesh panel) {
		super(node);
		this.mesh = panel;
		componentColor = new ComponentColor(new ColorRGBA(1,1,1,1), new ColorRGBA(panel.getDefaultColor()));
		getSpatial().attachChild(panel);
	}

	@Override
	public void onReleaseResources(final Component component) {
		ComponentViewUtil.release(mesh);
		super.onReleaseResources(component);
	}

	public Mesh getMesh() {
		return mesh;
	}

	@Override
	public void onDisable(final Component component) {
		mesh.setDefaultColor(componentColor.setDisabled());
	}

	@Override
	public void onEnable(final Component component) {
		mesh.setDefaultColor(componentColor.setEnabled());
	}

	@Override
	public void onChangeFade(final Component component) {
		mesh.setDefaultColor(componentColor.setFade(component.getFade()));
	}

	@Override
	public void onChangeColor(final Component component) {
		mesh.setDefaultColor(componentColor.setColor(component.getColor()));
	}
}