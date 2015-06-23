package se.spaced.client.tools.areacreator;

import se.spaced.shared.world.AreaPoint;
import se.spaced.shared.world.area.Geometry;
import se.spaced.shared.world.walkmesh.LocalSpaceConverter;

public interface AreaCreatorPresenter {

	void addPointToCurrentGeometry(AreaPoint point);

	void showGeometry(Geometry geometry);

	LocalSpaceConverter getLocalSpaceConverter();

	void showGui();


	void clearPoints();
}
