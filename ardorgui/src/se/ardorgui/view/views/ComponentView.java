package se.ardorgui.view.views;

import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.CullHint;
import se.ardorgui.components.base.Component;
import se.ardorgui.components.base.ComponentListener;
import se.ardorgui.view.ComponentViewUtil;

import java.awt.Point;

public abstract class ComponentView<T extends Spatial> implements ComponentListener {
	private final T spatial;

	protected ComponentView(final T spatial) {
		this.spatial = spatial;
	}

	public T getSpatial() {
		return spatial;
	}

	@Override
	public void onHide(final Component component) {
		spatial.getSceneHints().setCullHint(CullHint.Always);
	}

	@Override
	public void onShow(final Component component) {
		spatial.getSceneHints().setCullHint(CullHint.Never);
	}

	@Override
	public void onMove(final Component component) {
		final Point componentPosition = component.getPosition();
		spatial.setTranslation(new Vector3(componentPosition.x, componentPosition.y, 0));
	}

	@Override
	public void onResize(final Component component) {}
	@Override
	public void onDisable(final Component component) {}
	@Override
	public void onEnable(final Component component) {}
	@Override
	public void onChangeFade(final Component component) {}
	@Override
	public void onChangeColor(final Component component) {}
	@Override
	public void onReleaseResources(final Component component) {
		ComponentViewUtil.release(spatial);
	}
}