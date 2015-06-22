package se.spaced.server.account;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.model.Player;
import se.spaced.server.persistence.DuplicateObjectException;
import se.spaced.server.persistence.ObjectNotFoundException;
import se.spaced.server.persistence.dao.interfaces.AccountDao;
import se.spaced.server.persistence.util.transactions.AutoTransaction;
import se.spaced.shared.network.webservices.informationservice.ServerAccountLoadStatus;

import java.util.List;

@Singleton
public class AccountServiceImpl implements AccountService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final AccountDao accountDao;

	@Inject
	public AccountServiceImpl(AccountDao accountDao) {
		this.accountDao = accountDao;
	}


	/**
	 * Creates a new account with the uuid given
	 *
	 * @param uuid the id in fame for the new account
	 * @param type
	 * @return the new account that was created
	 */
	@Override
	@AutoTransaction
	public Account createAccount(UUID uuid, AccountType type) throws DuplicateObjectException {
		if (accountDao.findByPk(uuid) != null) {
			throw new DuplicateObjectException("Duplicate account with uuid " + uuid);
		}

		Account account = new Account(uuid, type);
		accountDao.persist(account);
		return account;
	}

	@Override
	@AutoTransaction
	public Account getAccount(UUID uuid) throws ObjectNotFoundException {
		try {
			Account account = accountDao.findByPk(uuid);
			if (account != null) {
				return account;
			}
		} catch (HibernateException e) {
			throw new RuntimeException("Failed to getAccount", e);
		}
		throw new ObjectNotFoundException("No account with id " + uuid + " found");
	}

	@Override
	@AutoTransaction
	public List<Account> getAllAccounts() {
		return accountDao.findAll();
	}

	@Override
	@AutoTransaction
	public void bindCharacterToAccount(Account account, Player player) {
		account.addPlayerCharacter(player);
		accountDao.persist(account);
	}


	@Override
	@AutoTransaction
	public int getNumberOfAccounts() {
		return accountDao.getNumberOfAccounts();
	}

	@Override
	@AutoTransaction
	public ServerAccountLoadStatus getServerAccountLoadStatus() {
		return ServerAccountLoadStatus.getFromNrOfAccounts(getNumberOfAccounts());
	}


	@Override
	@AutoTransaction
	public void deleteAll() {
		accountDao.deleteAll();
	}
}
