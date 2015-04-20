package se.spaced.spacedit.xmo.model.listeners;

public interface ExtendedMeshObjectPropertyListener {
	void onRotationChange(double x, double y, double z, double w);

	void onScaleChange(double x, double y, double z);

	void onLocationChange(double x, double y, double z);
}
