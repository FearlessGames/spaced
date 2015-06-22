package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import se.spaced.server.model.aura.ServerAura;
import se.spaced.server.persistence.dao.interfaces.AuraDao;

public class AuraDaoImpl extends FindableDaoImpl<ServerAura> implements AuraDao {
	@Inject
	public AuraDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, ServerAura.class);
	}
}