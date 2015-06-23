package se.spaced.client.view.entity;

import se.ardortech.input.ClientMouseButton;
import se.spaced.client.core.states.Updatable;

public interface EntityInteractionView extends Updatable {
	void onMouseDown(int x, int y, ClientMouseButton clientMouseButton);

	void onMouseUp(int x, int y, ClientMouseButton clientMouseButton);

	void onMouseMove(int x, int y);
}
