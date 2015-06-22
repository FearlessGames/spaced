package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.persistence.dao.interfaces.SpellDao;

public class SpellDaoImpl extends FindableDaoImpl<ServerSpell> implements SpellDao {
	@Inject
	public SpellDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, ServerSpell.class);
	}

}