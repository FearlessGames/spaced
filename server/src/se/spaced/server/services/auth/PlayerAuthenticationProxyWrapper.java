package se.spaced.server.services.auth;

import se.spaced.server.model.Player;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class PlayerAuthenticationProxyWrapper {

	@SuppressWarnings("unchecked")
	public static <T> T wrap(final T t, final PlayerAuthenticator playerAuthenticator) {
		Class<?> clazz = t.getClass();

		InvocationHandler proxy = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if (args.length == 0) {
					throw new RuntimeException("Gm methods needs at least one parameter, the gms character");
				}
				Player first = null;
				try {
					first = (Player) args[0];
				} catch (Exception e) {
					throw new RuntimeException("First parameter has to be a player");
				}
				if (playerAuthenticator.authenticate(first)) {
					return method.invoke(t, args);
				}
				throw new RuntimeException(String.format("Invocation of %s not allowed for %s",
						method.getName(),
						first.getName()));
			}
		};

		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), getInterfaces(clazz), proxy);
	}

	private static Class<?>[] getInterfaces(Class<?> clazz) {
		return clazz.getInterfaces();
	}
}
