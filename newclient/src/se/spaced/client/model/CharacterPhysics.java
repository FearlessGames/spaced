package se.spaced.client.model;

import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.shared.model.PositionalData;

public class CharacterPhysics {

	private final PositionalData transform;
	private SpacedVector3 velocity = SpacedVector3.ZERO;
	private boolean groundContact = false;
	private int filteredGroundContact = Integer.MAX_VALUE;
	private SpacedVector3 groundNormal = SpacedVector3.PLUS_I;
	private boolean groundHit;

	public CharacterPhysics(PositionalData positionalData) {
		transform = positionalData;
	}

	public SpacedVector3 getVelocity() {
		return velocity;
	}

	public SpacedRotation getRotation() {
		return transform.getRotation();
	}

	public SpacedVector3 getPosition() {
		return transform.getPosition();
	}

	public boolean getGroundContact() {
		return groundContact;
	}

	public boolean getFilteredGroundContact(int framesAgo) {
		return filteredGroundContact < framesAgo;
	}

	public void setGroundContact(boolean contact) {
		groundContact = contact;
		if (contact) {
			filteredGroundContact = 0;
		} else {
			filteredGroundContact++;			
		}
	}

	public boolean canJump() {
		return getGroundContact();
	}

	public void setGroundNormal(SpacedVector3 groundNormal) {
		this.groundNormal = groundNormal;
	}

	public SpacedVector3 getGroundNormal() {
		return groundNormal;
	}

	public void setGroundHit(boolean groundHit) {
		this.groundHit = groundHit;
	}

	public boolean getGroundHit() {
		return groundHit;
	}

	public void setPosition(SpacedVector3 position) {
		this.transform.setPosition(position);
	}

	public void setVelocity(SpacedVector3 velocity) {
		this.velocity = velocity;
	}

	public void setRotation(SpacedRotation spacedRotation) {
		transform.setRotation(spacedRotation);
	}
}
