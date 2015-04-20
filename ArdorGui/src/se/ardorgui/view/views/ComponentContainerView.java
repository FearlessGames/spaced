package se.ardorgui.view.views;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import se.ardorgui.components.base.Component;
import se.ardorgui.components.base.ComponentContainerViewInterface;
import se.ardorgui.view.ComponentViewUtil;

public class ComponentContainerView extends ComponentView<Node> implements ComponentContainerViewInterface {
	public ComponentContainerView(final Node node) {
		super(node);
		ComponentViewUtil.setupComponent(node);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onAdd(final Component component) {
		for (final Object view : component.getListeners().getListeners()) {
			if (view instanceof ComponentView) {
				getSpatial().attachChildAt(((ComponentView<Spatial>)view).getSpatial(), 0);
			}
		}
		getSpatial().updateGeometricState(0.0f, true);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onRemove(final Component component) {
		for (final Object view : component.getListeners().getListeners()) {
			if (view instanceof ComponentView) {
				getSpatial().detachChild(((ComponentView<Spatial>)view).getSpatial());
			}
		}
		getSpatial().updateGeometricState(0.0f, true);
	}

	@Override
	public void onBringToFront(Component component) {
		onRemove(component);
		onAdd(component);
	}
}