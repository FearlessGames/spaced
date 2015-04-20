package se.spaced.server.persistence.util.transactions;

import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.Transaction;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;

/**
 * Usage:
 * In Guice module:
 * AutoTransactionInterceptor autoTransactionInterceptor = new AutoTransactionInterceptor();
 * binder.requestInjection(autoTransactionInterceptor);
 * binder.bindInterceptor(Matchers.any(), Matchers.annotatedWith(AutoTransaction.class), autoTransactionInterceptor);
 */
public class AutoTransactionInterceptor implements MethodInterceptor {

	private TransactionManager transactionManager;

	public AutoTransactionInterceptor() {
	}

	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		boolean hasActiveTransaction = transactionManager.isActive();
		Transaction transaction = null;
		if (!hasActiveTransaction) {
			transaction = transactionManager.beginTransaction();
		}

		try {
			Object ret = methodInvocation.proceed();
			if (!hasActiveTransaction) {
				transaction.commit();
			}
			return ret;

		} catch (Exception e) {
			if (!hasActiveTransaction) {
				transaction.rollback();
			}
			throw e;
		}
	}

	@Inject
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
