package se.spaced.spacedit.state;

public interface StateChangeListener {
	void fromDefaultToXMOInContext();

	void fromXMOInContextToDefault();
}
