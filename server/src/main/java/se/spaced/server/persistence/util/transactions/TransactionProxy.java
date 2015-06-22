package se.spaced.server.persistence.util.transactions;

import org.hibernate.Transaction;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;

import javax.transaction.TransactionRequiredException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class TransactionProxy implements InvocationHandler {

	protected enum TransactionType {
		DEFAULT {
			@Override
			Object invoke(TransactionProxy proxy, Object obj, Method method, Object[] args) throws Throwable {
				return method.invoke(obj, args);
			}
		},

		REQUIRES {
			@Override
			Object invoke(TransactionProxy proxy, Object obj, Method method, Object[] args) throws Throwable {
				if (proxy.transactionManager.isActive()) {
					return method.invoke(obj, args);
				} else {
					throw new TransactionRequiredException();
				}
			}
		},
		AUTO_TRANSACTION {
			@Override
			Object invoke(TransactionProxy proxy, Object obj, Method method, Object[] args) throws Throwable {
				boolean hasActiveTransaction = proxy.transactionManager.isActive();
				Transaction transaction = null;
				if (!hasActiveTransaction) {
					transaction = proxy.transactionManager.beginTransaction();
				}

				try {
					Object ret = method.invoke(obj, args);
					if (!hasActiveTransaction) {
						transaction.commit();
					}
					return ret;

				} catch (InvocationTargetException e) {
					if (!hasActiveTransaction) {
						transaction.rollback();
					}
					throw e.getCause();
				}
			}
		},
		NEW {
			@Override
			Object invoke(TransactionProxy proxy, Object obj, Method method, Object[] args) throws Throwable {
				throw new RuntimeException("@NewTransaction not yet implemented.");
			}
		};

		abstract Object invoke(TransactionProxy proxy, Object obj, Method method, Object[] args) throws Throwable;
	}

	private final Map<Method, TransactionType> transactions;
	private final Object realObject;
	private final TransactionManager transactionManager;

	protected TransactionProxy(
			Object realObject, Map<Method, TransactionType> transactions, TransactionManager transactionManager) {
		this.realObject = realObject;
		this.transactions = transactions;
		this.transactionManager = transactionManager;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		TransactionType type = transactions.get(method);
		return type.invoke(this, realObject, method, args);
	}

}
