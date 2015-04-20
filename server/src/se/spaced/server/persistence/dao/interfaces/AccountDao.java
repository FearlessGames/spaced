package se.spaced.server.persistence.dao.interfaces;

import se.spaced.server.account.Account;


public interface AccountDao extends Dao<Account> {

	int getNumberOfAccounts();
}