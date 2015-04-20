package se.spaced.server.persistence.dao.impl.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.server.model.currency.Wallet;
import se.spaced.server.persistence.dao.interfaces.WalletDao;

public class InMemoryWalletDao extends OwnedInMemoryDao<Wallet> implements WalletDao {
	private final Logger log = LoggerFactory.getLogger(getClass());
}
