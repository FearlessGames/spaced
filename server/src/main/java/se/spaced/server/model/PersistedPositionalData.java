package se.spaced.server.model;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.server.persistence.dao.impl.PersistableBase;
import se.spaced.shared.model.PositionalData;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class PersistedPositionalData extends PersistableBase {
	@Columns(columns = {
			@Column(name = "x"),
			@Column(name = "y"),
			@Column(name = "z")
	})
	@Type(type = "vector3")
	protected SpacedVector3 position;

	@Column(name = "rot")
	protected SpacedRotation rotation;

	public PersistedPositionalData(SpacedVector3 pos, SpacedRotation rot) {
		position = pos;
		rotation = rot;
	}

	public PersistedPositionalData() {
		position = SpacedVector3.ZERO;
		rotation = SpacedRotation.IDENTITY;
	}

	public SpacedVector3 getPosition() {
		return position;
	}

	public SpacedRotation getRotation() {
		return rotation;
	}

	public PositionalData toPositionalData() {
		return new PositionalData(position, rotation);
	}
	
	@Override
	public String toString() {
		return "PersistedPositionalData[pos: " + position + ", rot: " + rotation + "]";
	}

	public void setPosition(SpacedVector3 position) {
		this.position = position;
	}

	public void setRotation(SpacedRotation rotation) {
		this.rotation = rotation;
	}
}
