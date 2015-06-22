package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import se.spaced.server.model.currency.PersistedCurrency;
import se.spaced.server.persistence.dao.interfaces.CurrencyDao;

public class CurrencyDaoImpl extends FindableDaoImpl<PersistedCurrency> implements CurrencyDao {
	@Inject
	public CurrencyDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, PersistedCurrency.class);
	}
}