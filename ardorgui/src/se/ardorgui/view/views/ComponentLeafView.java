package se.ardorgui.view.views;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.scenegraph.Mesh;
import se.ardorgui.components.base.Component;
import se.ardorgui.view.ComponentColor;

public abstract class ComponentLeafView<T extends Mesh> extends ComponentView<T> {
	private final ComponentColor componentColor;

	protected ComponentLeafView(final T jmeObject) {
		super(jmeObject);
		componentColor = new ComponentColor(new ColorRGBA(1,1,1,1), new ColorRGBA(jmeObject.getDefaultColor()));
	}

	@Override
	public void onDisable(final Component component) {
		getSpatial().setDefaultColor(componentColor.setDisabled());
	}

	@Override
	public void onEnable(final Component component) {
		getSpatial().setDefaultColor(componentColor.setEnabled());
	}

	@Override
	public void onChangeFade(final Component component) {
		getSpatial().setDefaultColor(componentColor.setFade(component.getFade()));
	}

	@Override
	public void onChangeColor(final Component component) {
		getSpatial().setDefaultColor(componentColor.setColor(component.getColor()));
	}
}