package se.spaced.shared.model;

import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;


public class PositionalData {
	protected SpacedVector3 position;
	protected SpacedRotation rotation;

	public PositionalData(SpacedVector3 pos, SpacedRotation rot) {
		position = pos;
		rotation = rot;
	}

	public PositionalData() {
		position = SpacedVector3.ZERO;
		rotation = SpacedRotation.IDENTITY;
	}

	public SpacedVector3 getPosition() {
		return position;
	}

	public SpacedRotation getRotation() {
		return rotation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		result = prime * result
				+ ((rotation == null) ? 0 : rotation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!getClass().isInstance(obj)) {
			return false;
		}
		PositionalData other = (PositionalData) obj;
		if (position == null) {
			if (other.position != null) {
				return false;
			}
		} else if (!position.equals(other.position)) {
			return false;
		}
		if (rotation == null) {
			if (other.rotation != null) {
				return false;
			}
		} else if (!rotation.equals(other.rotation)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PositionalData[pos: " + position + ", rot: " + rotation + "]";
	}

	public void setPosition(SpacedVector3 position) {
		this.position = position;
	}

	public void setRotation(SpacedRotation rotation) {
		this.rotation = rotation;
	}
}
