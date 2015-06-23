package se.spaced.client.tools.spd;

import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.visitor.Visitor;

public class ColorChangeVisitor implements Visitor {
	private final ReadOnlyColorRGBA color;

	public ColorChangeVisitor(ReadOnlyColorRGBA color) {
		this.color = color;
	}

	@Override
	public void visit(final Spatial spatial) {
		if (spatial instanceof Mesh) {
			final MaterialState material = (MaterialState) spatial.getLocalRenderState(RenderState.StateType.Material);
			spatial.setRenderState(material);
			material.setEmissive(color);
		}
	}
}
