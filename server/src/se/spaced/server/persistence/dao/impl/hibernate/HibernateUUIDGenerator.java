package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;
import se.fearlessgames.common.util.SystemTimeProvider;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.spaced.server.persistence.dao.interfaces.Persistable;

import java.io.Serializable;
import java.security.SecureRandom;

public class HibernateUUIDGenerator implements IdentifierGenerator {
	// TODO: How do we get rid of this ugly hardcode?
	private final UUIDFactory uuidFactory = new UUIDFactoryImpl(new SystemTimeProvider(), new SecureRandom());

	@Override
	public Serializable generate(SessionImplementor sessionImplementor, Object o) throws HibernateException {
		if (o instanceof Persistable) {
			if (((Persistable) o).getPk() != null) {
				return ((Persistable) o).getPk();
			}
		}
		return uuidFactory.combUUID();
	}
}