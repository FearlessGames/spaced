package se.spaced.client.model.animation;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import se.spaced.shared.model.AnimationState;

import java.util.HashMap;
import java.util.Map;

@XStreamAlias("AnimationMapping")
public class AnimationMapping {
	private Map<AnimationState, String> stateToFilenameMap = new HashMap<AnimationState, String>();

	public void setStateToFilenameMap(Map<AnimationState, String> stateToFilenameMap) {
		this.stateToFilenameMap = stateToFilenameMap;
	}

	public Map<AnimationState, String> getStateToFilenameMap() {
		return stateToFilenameMap;
	}
}
