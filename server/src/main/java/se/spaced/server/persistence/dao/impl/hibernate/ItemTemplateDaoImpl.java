package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.persistence.dao.interfaces.ItemTemplateDao;

public class ItemTemplateDaoImpl extends FindableDaoImpl<ServerItemTemplate> implements ItemTemplateDao {
	@Inject
	public ItemTemplateDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, ServerItemTemplate.class);
	}
}
