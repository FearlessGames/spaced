package se.spaced.server.persistence.dao.impl.inmemory;

import se.spaced.server.model.currency.PersistedCurrency;
import se.spaced.server.persistence.dao.interfaces.CurrencyDao;

public class InMemoryCurrencyDao extends FindableInMemoryDao<PersistedCurrency> implements CurrencyDao {
}