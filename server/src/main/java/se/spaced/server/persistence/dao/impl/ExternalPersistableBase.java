package se.spaced.server.persistence.dao.impl;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import se.fearless.common.uuid.UUID;
import se.spaced.server.persistence.dao.impl.hibernate.types.ModStatUserType;
import se.spaced.server.persistence.dao.impl.hibernate.types.QuaternionHibernateType;
import se.spaced.server.persistence.dao.impl.hibernate.types.SpacedUUIDHibernateType;
import se.spaced.server.persistence.dao.impl.hibernate.types.Vector3HibernateType;
import se.spaced.server.persistence.dao.impl.hibernate.types.XmlStringType;
import se.spaced.server.persistence.dao.interfaces.Persistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@TypeDefs({
		@TypeDef(name = "uuid", typeClass = SpacedUUIDHibernateType.class),
		@TypeDef(name = "vector3", typeClass = Vector3HibernateType.class),
		@TypeDef(name = "quaternion", typeClass = QuaternionHibernateType.class),
		@TypeDef(name = "xml", typeClass = XmlStringType.class),
		@TypeDef(name = "modstat", typeClass = ModStatUserType.class)
})
public abstract class ExternalPersistableBase implements Persistable {
	@Id
	@Type(type = "uuid")
	@Column(length = 36)
	private UUID pk;

	protected ExternalPersistableBase(UUID pk) {
		this.pk = pk;
	}

	protected ExternalPersistableBase() {
	}

	@Override
	public UUID getPk() {
		return pk;
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

		ExternalPersistableBase that = (ExternalPersistableBase) o;

		return pk != null ? pk.equals(that.pk) : that.pk == null;
	}

	@Override
	public int hashCode() {
		return pk != null ? pk.hashCode() : 0;
	}
}