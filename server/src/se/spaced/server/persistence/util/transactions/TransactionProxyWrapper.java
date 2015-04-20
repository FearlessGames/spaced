package se.spaced.server.persistence.util.transactions;

import com.google.common.collect.Maps;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

public class TransactionProxyWrapper {
	private final TransactionManager transactionManager;

	public TransactionProxyWrapper(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@SuppressWarnings("unchecked")
	public <T> T wrap(T t) {
		Class<?> clazz = t.getClass();

		HashMap<Method, TransactionProxy.TransactionType> methods = Maps.newHashMap();

		for (Method m : clazz.getMethods()) {
			TransactionProxy.TransactionType type;
			if (m.isAnnotationPresent(NewTransaction.class)) {
				type = TransactionProxy.TransactionType.NEW;
			} else if (m.isAnnotationPresent(RequireTransaction.class)) {
				type = TransactionProxy.TransactionType.REQUIRES;
			} else if (m.isAnnotationPresent(AutoTransaction.class)) {
				type = TransactionProxy.TransactionType.AUTO_TRANSACTION;
			} else {
				type = TransactionProxy.TransactionType.DEFAULT;
			}
			setMethods(clazz, m.getName(), m.getParameterTypes(), type, methods);
		}

		InvocationHandler proxy = new TransactionProxy(t, methods, transactionManager);

		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), getInterfaces(clazz), proxy);
	}

	private static Class<?>[] getInterfaces(Class<?> clazz) {
		return clazz.getInterfaces();
	}

	private void setMethods(Class<?> clazz, String name, Class<?>[] parameterTypes, TransactionProxy.TransactionType type,
									HashMap<Method, TransactionProxy.TransactionType> methods) {

		if (clazz == null) {
			return;
		}

		for (Method m : clazz.getMethods()) {
			if (name.equals(m.getName()) && sameParameter(parameterTypes, m.getParameterTypes())) {
				methods.put(m, type);
			}
		}

		setMethods(clazz.getSuperclass(), name, parameterTypes, type, methods);
		for (Class<?> interfaze : clazz.getInterfaces()) {
			setMethods(interfaze, name, parameterTypes, type, methods);
		}
	}

	private boolean sameParameter(Class<?>[] parameterTypes, Class<?>[] parameterTypes2) {
		int n = parameterTypes.length;
		if (parameterTypes2.length != n) {
			return false;
		}
		for (int i = 0; i < n; i++) {
			if (!(parameterTypes[i].equals(parameterTypes2[i]))) {
				return false;
			}
		}
		return true;
	}
}
