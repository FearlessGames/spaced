package se.spaced.client.tools.areacreator;

import se.spaced.shared.world.AreaPoint;

public interface AreaDisplayHandler {
	void areaModified(Area area);

	void show(Area area);

	void hide();

	void showIndicator(AreaPoint currentPoint);

	void removeIndicator();

}
