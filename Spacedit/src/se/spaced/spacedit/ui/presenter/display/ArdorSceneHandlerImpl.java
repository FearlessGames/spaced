package se.spaced.spacedit.ui.presenter.display;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Node;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.spacedit.xmo.XmoManager;
import se.spaced.spacedit.xmo.model.MovableXmo;
import se.spaced.spacedit.xmo.model.listeners.ExtendedMeshObjectPropertyListener;
import se.spaced.spacedit.xmo.model.listeners.XmoContainerNodePropertyListener;
import se.spaced.spacedit.xmo.model.listeners.XmoMetaNodePropertyListener;
import se.spaced.spacedit.xmo.model.listeners.XmoRootPropertyListener;

@Singleton
public class ArdorSceneHandlerImpl implements ArdorSceneHandler, ExtendedMeshObjectPropertyListener, XmoRootPropertyListener, XmoMetaNodePropertyListener, XmoContainerNodePropertyListener {
	private final XmoManager xmoManager;
	private static final double MOVE_SPEED = 5;
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	public ArdorSceneHandlerImpl(final XmoManager xmoManager) {
		this.xmoManager = xmoManager;
	}

	@Override
	public void moveUp() {
		addMovement(0, MOVE_SPEED, 0);
	}


	@Override
	public void moveDown() {
		addMovement(0, -MOVE_SPEED, 0);
	}

	@Override
	public void moveRight() {
		addMovement(MOVE_SPEED, 0, 0);
	}

	@Override
	public void moveLeft() {
		addMovement(-MOVE_SPEED, 0, 0);

	}


	@Override
	public void moveIn() {
		addMovement(0, 0, MOVE_SPEED);

	}

	@Override
	public void moveOut() {
		addMovement(0, 0, -MOVE_SPEED);

	}

	private void addMovement(double x, double y, double z) {
		if (xmoManager.getCurrentlySelectedObject() instanceof MovableXmo) {
			Node currentNode = xmoManager.getCurrentNode();
			Vector3 location = currentNode.getTranslation().add(x, y, z, null);
			((MovableXmo) xmoManager.getCurrentlySelectedObject()).updateLocation(location.getX(), location.getY(), location.getZ());
		}
	}


	@Override
	public void onLocationChange(double x, double y, double z) {
		xmoManager.getCurrentNode().setTranslation(x, y, z);
	}

	@Override
	public void onScaleChange(double x, double y, double z) {
		scaleTo(x, y, z);
	}

	@Override
	public void scaleTo(double x, double y, double z) {
		Node currentNode = xmoManager.getCurrentNode();
		currentNode.setScale(x, y, z);
	}

	@Override
	public void onNameChange(String name) {
		xmoManager.getXmoRootNode().setName(name);
	}

	@Override
	public void onRotationChange(double x, double y, double z, double w) {
		xmoManager.getCurrentNode().setRotation(new Quaternion(x, y, z, w));
	}

	@Override
	public void onSizeChange(double x, double y, double z) {

	}


}
