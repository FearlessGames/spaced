package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import se.spaced.server.loot.PersistableLootTemplate;
import se.spaced.server.persistence.dao.interfaces.LootTemplateDao;

public class LootTemplateDaoImpl extends FindableDaoImpl<PersistableLootTemplate> implements LootTemplateDao {
	@Inject
	public LootTemplateDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, PersistableLootTemplate.class);
	}
}