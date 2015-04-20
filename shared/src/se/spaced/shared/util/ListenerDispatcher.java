package se.spaced.shared.util;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class ListenerDispatcher<T> {
	private final List<T> listeners;
	private final T proxy;
	private final T proxyReversed;

	@SuppressWarnings("unchecked")
	private ListenerDispatcher(Class<? extends T> interfaceClass) {
		final Logger logger = LoggerFactory.getLogger(interfaceClass.getName());

		listeners = Lists.newArrayList();
		InvocationHandler invocationHandler = new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				for (T listener : listeners) {
					try {
						method.invoke(listener, args);
					} catch (InvocationTargetException e) {
						throw e.getCause();
					}
				}
				return null;
			}

		};
		proxy = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, invocationHandler);
		invocationHandler = new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				int n = listeners.size();
				for (int i = n - 1; i >= 0; i--) {
					T listener = listeners.get(i);
					try {
						method.invoke(listener, args);
					} catch (InvocationTargetException e) {
						throw e.getCause();
					}
				}
				return null;
			}

		};
		proxyReversed = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, invocationHandler);

	}

	public static <T> ListenerDispatcher<T> create(Class<? extends T> interfaceClass) {
		return new ListenerDispatcher<T>(interfaceClass);
	}

	public void addListener(T listener) {
		listeners.add(listener);
	}

	public void removeListener(T listener) {
		listeners.remove(listener);
	}

	public T trigger() {
		return proxy;

	}
	public T triggerReversed() {
		return proxyReversed;

	}
}
