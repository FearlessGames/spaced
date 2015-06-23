package se.spaced.server.persistence.dao.impl;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import se.fearless.common.uuid.UUID;
import se.spaced.server.persistence.dao.impl.hibernate.types.*;
import se.spaced.server.persistence.dao.interfaces.Persistable;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@TypeDefs({
		@TypeDef(name = "uuid", typeClass = SpacedUUIDHibernateType.class),
		@TypeDef(name = "vector3", typeClass = Vector3HibernateType.class),
		@TypeDef(name = "quaternion", typeClass = QuaternionHibernateType.class),
		@TypeDef(name = "xml", typeClass = XmlStringType.class),
		@TypeDef(name = "modstat", typeClass = ModStatUserType.class)
})
public abstract class OwnedPersistableBase implements Persistable {
	@Id
	@GenericGenerator(name = "spaced-uuid",
			strategy = "se.spaced.server.persistence.dao.impl.hibernate.HibernateUUIDGenerator")
	@GeneratedValue(generator = "spaced-uuid")
	@Type(type = "uuid")
	@Column(length = 36)
	private UUID pk;

	@Type(type = "uuid")
	@Column(updatable = false)
	private UUID ownerPk;

	protected OwnedPersistableBase(Persistable owner) {
		this.ownerPk = owner.getPk();
	}

	protected OwnedPersistableBase() {
	}

	@Override
	public UUID getPk() {
		return pk;
	}


	public UUID getOwnerPk() {
		return ownerPk;
	}

	@Override
	public void setPk(UUID pk) {
		this.pk = pk;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		OwnedPersistableBase that = (OwnedPersistableBase) o;

		if (pk != null ? !pk.equals(that.pk) : that.pk != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return pk != null ? pk.hashCode() : 0;
	}
}
