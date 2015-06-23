package se.spaced.client.model.player;

public interface PlayerTargetingListener {
	void newTarget(TargetInfo targetInfo);

	void targetCleared();

	void newHover(TargetInfo targetInfo);

	void hoverCleared();
}
