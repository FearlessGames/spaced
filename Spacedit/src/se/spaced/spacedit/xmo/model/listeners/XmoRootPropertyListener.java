package se.spaced.spacedit.xmo.model.listeners;

public interface XmoRootPropertyListener {
	void onNameChange(String name);

	void onSizeChange(double x, double y, double z);
}