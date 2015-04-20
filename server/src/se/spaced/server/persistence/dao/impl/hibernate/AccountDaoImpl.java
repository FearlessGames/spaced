package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import se.spaced.server.account.Account;
import se.spaced.server.persistence.dao.interfaces.AccountDao;
import se.spaced.server.persistence.util.transactions.AutoTransaction;

public class AccountDaoImpl extends DaoImpl<Account> implements AccountDao {

	@Inject
	public AccountDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, Account.class);
	}

	@AutoTransaction
	@Override
	public int getNumberOfAccounts() {
		Criteria crit = getSession().createCriteria(Account.class);
		crit.setProjection(Projections.rowCount());
		return ((Long) crit.list().get(0)).intValue();
	}

}
