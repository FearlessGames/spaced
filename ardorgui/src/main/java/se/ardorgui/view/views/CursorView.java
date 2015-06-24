package se.ardorgui.view.views;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.controller.ComplexSpatialController;
import com.ardor3d.scenegraph.controller.SpatialController;
import com.ardor3d.scenegraph.hint.CullHint;
import se.ardorgui.components.base.Component;
import se.ardortech.meshgenerator.shapes.QuadMeshDataGenerator;

public class CursorView extends PictureView {
	public CursorView(final Node node, final Mesh mesh, final ColorRGBA color, final QuadMeshDataGenerator generator) {
		super(node, mesh, generator);
	}

	@Override
	public void onHide(final Component component) {
		super.onHide(component);
		final Spatial cursor = getSpatial();
		cursor.getSceneHints().setCullHint(CullHint.Always);
		for (int i = 0; i < cursor.getControllers().size(); i++) {
			final SpatialController<?> controller = cursor.getController(i);
			cursor.removeController(controller);
			i--;
		}
	}

	@Override
	public void onShow(final Component component) {
		super.onShow(component);
		final Spatial cursor = getSpatial();
		cursor.getSceneHints().setCullHint(CullHint.Never);
		if (cursor.getControllers().isEmpty()) {
			cursor.addController(new ComplexSpatialController<Spatial>() {
				private static final long serialVersionUID = 1L;
				private float currentTime = 0;
				@Override
				public void update(double time, Spatial caller) {
					if (currentTime >= 0.15f) {
						if (cursor.getSceneHints().getCullHint() == CullHint.Always) {
							cursor.getSceneHints().setCullHint(CullHint.Never);
						} else {
							cursor.getSceneHints().setCullHint(CullHint.Always);
						}
						currentTime = 0;
					}
					currentTime += time;
				}
			});
		}
	}
}