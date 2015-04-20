package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.server.model.currency.Wallet;
import se.spaced.server.persistence.dao.interfaces.WalletDao;

public class WalletDaoImpl extends FindByOwnerDaoImpl<Wallet> implements WalletDao {
	private final Logger log = LoggerFactory.getLogger(getClass());

	public WalletDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, Wallet.class);
	}
}
