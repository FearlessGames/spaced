package se.spaced.client.model.animation;

import com.ardor3d.extension.animation.skeletal.layer.AnimationLayer;
import com.ardor3d.extension.animation.skeletal.state.AbstractFiniteState;
import com.ardor3d.extension.animation.skeletal.state.FadeTransitionState;

import static com.ardor3d.extension.animation.skeletal.state.AbstractTwoStateLerpTransition.BlendType.SCurve3;

public class SpacedBlendState extends FadeTransitionState {

	public SpacedBlendState(String targetState, double fadeTime) {
		super(targetState, fadeTime, SCurve3);
	}

	public void blendBetween(AbstractFiniteState from, AbstractFiniteState to, AnimationLayer layer) {
		if (from == null) {
			layer.setCurrentState(to, true);
		} else {
			setStateA(from);
			setStateB(to);
			setStart(layer.getManager().getCurrentGlobalTime());
			layer.setCurrentState(this, true);
		}
	}
}
