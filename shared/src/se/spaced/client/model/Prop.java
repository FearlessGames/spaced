package se.spaced.client.model;

import se.ardortech.math.Rotations;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.shared.model.xmo.XmoEntity;
import se.spaced.shared.resources.zone.Zone;

/**
 * A prop describes where to place a static mesh in the currentZone.
 */
public class Prop {
	private SpacedVector3 location = SpacedVector3.ZERO;
	private SpacedRotation rotation = SpacedRotation.IDENTITY;
	private SpacedVector3 scale = new SpacedVector3(1, 1, 1);
	private String xmoFile;
	private transient Zone zone;
	private transient XmoEntity xmoEntity;
	private transient CreatedCallback createdCallback;

	public Prop(String xmoFile, SpacedVector3 location, SpacedVector3 scale, SpacedRotation rotation) {
		this.location = location;
		this.xmoFile = xmoFile;
		this.scale = scale;
		this.rotation = rotation;
	}

	public void transformProp(XmoEntity xmoEntity) {
		xmoEntity.getModel().setTranslation(getLocation());
		xmoEntity.getModel().setScale(getScale());
		xmoEntity.getModel().setRotation(Rotations.fromSpaced(getRotation()));
		xmoEntity.getModel().setUserData(this);
		xmoEntity.getModel().updateWorldTransform(true);
	}

	public SpacedRotation getRotation() {
		return rotation;
	}

	public void setRotation(SpacedRotation rotation) {
		this.rotation = rotation;
	}

	public SpacedVector3 getScale() {
		return scale;
	}

	public void setScale(SpacedVector3 scale) {
		this.scale = scale;
	}

	public SpacedVector3 getLocation() {

		return location;
	}

	public void setLocation(SpacedVector3 location) {
		this.location = location;
	}

	public String getXmoFile() {
		return xmoFile;
	}

	public void setXmoFile(String xmoFile) {
		this.xmoFile = xmoFile;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public XmoEntity getXmoEntity() {
		return xmoEntity;
	}

	public void setXmoEntity(XmoEntity xmoEntity) {
		this.xmoEntity = xmoEntity;
		if (createdCallback != null) {
			createdCallback.onCreated();
		}
	}

	@Override
	public String toString() {
		return "Prop{" +
				"location=" + location +
				", rotation=" + rotation +
				", scale=" + scale +
				", xmoFile='" + xmoFile + '\'' +
				'}';
	}

	public void setCreatedCallback(CreatedCallback createdCallback) {
		this.createdCallback = createdCallback;
	}

	public interface CreatedCallback {
		void onCreated();
	}

}
