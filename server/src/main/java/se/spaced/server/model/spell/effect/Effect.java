package se.spaced.server.model.spell.effect;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.persistence.dao.impl.hibernate.types.QuaternionHibernateType;
import se.spaced.server.persistence.dao.impl.hibernate.types.SpacedUUIDHibernateType;
import se.spaced.server.persistence.dao.impl.hibernate.types.Vector3HibernateType;
import se.spaced.server.persistence.dao.interfaces.Persistable;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.network.protocol.codec.datatype.SpellEffect;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@TypeDefs({
		@TypeDef(name = "uuid", typeClass = SpacedUUIDHibernateType.class),
		@TypeDef(name = "vector3", typeClass = Vector3HibernateType.class),
		@TypeDef(name = "quaternion", typeClass = QuaternionHibernateType.class)
})
public abstract class Effect implements Persistable, SpellEffect {

	@Transient
	protected final SmrtBroadcaster<S2CProtocol> smrtBroadcaster;

	@Id
	@Type(type = "uuid")
	@Column(length = 36)
	private UUID pk;

	private String resourceName = "";

	@Override
	public UUID getPk() {
		return pk;
	}

	@Override
	public void setPk(UUID pk) {
		this.pk = pk;
	}

	@Enumerated(EnumType.STRING)
	@Column(length = 11, name = "school", nullable = false)
	private MagicSchool school;

	protected Effect(SmrtBroadcaster<S2CProtocol> smrtBroadcaster) {
		this.smrtBroadcaster = smrtBroadcaster;
	}

	protected Effect(MagicSchool school, SmrtBroadcaster<S2CProtocol> smrtBroadcaster) {
		this.school = school;
		this.smrtBroadcaster = smrtBroadcaster;
	}

	public MagicSchool getSchool() {
		return school;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public abstract void apply(long now, ServerEntity performer, ServerEntity target, String causeName);

	public abstract void fail(long now, ServerEntity performer, ServerEntity target, String causeName);
}
