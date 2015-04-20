package se.spaced.spacedit.xmo.model;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyQuaternion;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Node;
import se.spaced.shared.model.xmo.ExtendedMeshObject;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.spacedit.xmo.XmoManagerListener;
import se.spaced.spacedit.xmo.model.listeners.ExtendedMeshObjectPropertyListener;

public class WrappedExtendedMeshObject implements NodeHolder, MovableXmo {
	private final ListenerDispatcher<ExtendedMeshObjectPropertyListener> propertyChangedDispatcher;
	private final ExtendedMeshObjectOwner owner;
	private final ExtendedMeshObject extendedMeshObject;
	private Node node;

	public WrappedExtendedMeshObject(ExtendedMeshObject xmo, ExtendedMeshObjectOwner owner) {
		this.extendedMeshObject = xmo;
		this.owner = owner;

		propertyChangedDispatcher = ListenerDispatcher.create(ExtendedMeshObjectPropertyListener.class);
	}

	public ExtendedMeshObject getExtendedMeshObject() {
		return extendedMeshObject;
	}

	public void addPropertyChangeListener(ExtendedMeshObjectPropertyListener propertyListener) {
		this.propertyChangedDispatcher.addListener(propertyListener);
	}

	public void updateScale(double x, double y, double z) {
		extendedMeshObject.getScale().setX(x);
		extendedMeshObject.getScale().setY(y);
		extendedMeshObject.getScale().setZ(z);
		propertyChangedDispatcher.trigger().onScaleChange(x, y, z);
	}

	public ReadOnlyVector3 getScale() {
		if (extendedMeshObject.getScale() == null) {
			extendedMeshObject.setScale(new Vector3());
		}
		return extendedMeshObject.getScale();
	}

	public ReadOnlyQuaternion getRotation() {
		if (extendedMeshObject.getRotation() == null) {
			extendedMeshObject.setRotation(new Quaternion());
		}
		return extendedMeshObject.getRotation();
	}

	public void updateRotation(double x, double y, double z, double w) {
		extendedMeshObject.getRotation().set(x, y, z, w);
		propertyChangedDispatcher.trigger().onRotationChange(x, y, z, w);
	}

	@Override
	public void updateLocation(double x, double y, double z) {
		extendedMeshObject.getPosition().set(x, y, z);
		propertyChangedDispatcher.trigger().onLocationChange(x, y, z);
	}

	public String getColladaFile() {
		return extendedMeshObject.getColladaFile();
	}

	public void setColladaFile(String colladaFile) {
		extendedMeshObject.setColladaFile(colladaFile);
	}

	public void setXmoMaterialFile(String xmoMaterialFile) {
		extendedMeshObject.setXmoMaterialFile(xmoMaterialFile);
	}

	public String getXmoMaterialFile() {
		return extendedMeshObject.getXmoMaterialFile();
	}

	public Vector3 getPosition() {
		return extendedMeshObject.getPosition();
	}

	public void setPosition(Vector3 position) {
		extendedMeshObject.setPosition(position);
	}

	public Node getNode() {
		return node;
	}

	@Override
	public XmoType getXmoType() {
		return XmoType.ExtendedMeshObject;
	}

	@Override
	public void triggerSelectedDispatch(XmoManagerListener xmoManagerListener) {
		xmoManagerListener.selectedExtendedMeshObject(this);
	}

	public void setNode(Node node) {
		this.node = node;
	}

	@Override
	public String toString() {
		return extendedMeshObject.toString();
	}

	@Override
	public WrappedExtendedMeshObject createExtendedMeshObject() {
		return null;
	}

	@Override
	public WrappedXmoContainerNode createXmoContainerNode() {
		return null;
	}

	@Override
	public NodeHolder delete() {
		return owner.delete(this);
	}
}
