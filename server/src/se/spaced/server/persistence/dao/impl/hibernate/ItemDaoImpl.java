package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.persistence.dao.interfaces.ItemDao;

public class ItemDaoImpl extends DaoImpl<ServerItem> implements ItemDao {
	@Inject
	public ItemDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, ServerItem.class);
	}

	@Override
	public boolean isOwner(ServerEntity entity, ServerItem serverItem) {
		long count = (Long) getSession().
				createQuery("select count(distinct si.pk) from ServerItem si where si.pk = :pk and si.owner.pk = :ownerPK").
				setParameter("pk", serverItem.getPk()).
				setParameter("ownerPK", entity.getPk()).
				uniqueResult();
		return count != 0;
	}

	@Override
	public void persistVirtualItem(ServerItem item) {
		getSession().save(item);
	}
}
