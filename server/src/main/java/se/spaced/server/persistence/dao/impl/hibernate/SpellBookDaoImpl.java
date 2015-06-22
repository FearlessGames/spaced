package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import se.spaced.server.model.spell.SpellBook;
import se.spaced.server.persistence.dao.interfaces.SpellBookDao;

public class SpellBookDaoImpl extends FindByOwnerDaoImpl<SpellBook> implements SpellBookDao {
	@Inject
	public SpellBookDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, SpellBook.class);
	}
}