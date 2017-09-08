package se.ardorgui.view.animation;

public interface AnimatorListener {
	void animationFinished(SpatialAnimator animator);
	void animationAborted(SpatialAnimator animator);
}