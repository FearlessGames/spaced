package se.ardorgui.view;

import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.controller.ComplexSpatialController.RepeatType;
import se.ardorgui.components.listeners.ComponentMouseListener;
import se.ardorgui.input.events.ComponentMouseEvent;
import se.ardorgui.view.animation.SpatialAnimator;

public class ButtonAnimationListener implements ComponentMouseListener {
	private final Spatial spatial;

	public ButtonAnimationListener(Spatial spatial) {
		this.spatial = spatial;
	}

	@Override
	public void mouseClicked(ComponentMouseEvent e) {
		animateClicked();
	}

	@Override
	public void mousePressed(ComponentMouseEvent e) {
		animateStop();
	}

	@Override
	public void mouseReleased(ComponentMouseEvent e) {
	}

	@Override
	public void mouseEntered(ComponentMouseEvent e) {
		animateOver();
	}

	private void animateClicked() {
		SpatialAnimator st = new SpatialAnimator(spatial);
		st.setPosition(0.0f, new Vector3(0, 0, 0));
		st.setScale(0.0f, new Vector3(1, 1, 1));
		st.setScale(0.05f, new Vector3(1.05f, 1.05f, 1));
		st.setScale(0.1f, new Vector3(1, 1, 1));
		st.interpolateMissing();
	}

	private void animateStop() {
		SpatialAnimator st = new SpatialAnimator(spatial);
		st.setPosition(0.0f, new Vector3(0, 0, 0));
		st.interpolateMissing();
	}

	private void animateOver() {
		SpatialAnimator st = new SpatialAnimator(spatial);
		st.setRepeatType(RepeatType.CYCLE);
		st.setPosition(0.0f, new Vector3(0, 0, 0));
		st.setScale(0.0f, new Vector3(1, 1, 1));
		st.setScale(0.3f, new Vector3(0.98f, 0.98f, 1));
		st.interpolateMissing();
	}

	@Override
	public void mouseExited(ComponentMouseEvent e) {
		animateStop();
	}
}