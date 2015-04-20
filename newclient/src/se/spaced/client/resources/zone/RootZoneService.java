package se.spaced.client.resources.zone;

import se.ardortech.math.SpacedVector3;

public interface RootZoneService {
	void setFileName(String fileName);

	void reload(SpacedVector3 cameraPosition);

	void addListener(RootZoneServiceListener listener);
}
