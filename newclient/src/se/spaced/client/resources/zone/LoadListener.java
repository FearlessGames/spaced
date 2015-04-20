package se.spaced.client.resources.zone;

public interface LoadListener {
	void loadCompleted();

	void loadUpdate(int remainingTasks);
}
