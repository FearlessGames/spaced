package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import se.spaced.server.model.Player;
import se.spaced.server.persistence.dao.interfaces.PlayerDao;
import se.spaced.server.persistence.util.transactions.AutoTransaction;

public class PlayerDaoImpl extends DaoImpl<Player> implements PlayerDao {
	@Inject
	public PlayerDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, Player.class);
	}

	@Override
	@AutoTransaction
	public Player findByName(String name) {
		Criteria crit = getSession().createCriteria(Player.class);
		crit.add(Restrictions.eq("name", name).ignoreCase());
		return (Player) crit.uniqueResult();
	}
}
