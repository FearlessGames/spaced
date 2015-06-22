package se.spaced.server.tools.spawnpattern.presenter;

import org.hibernate.proxy.HibernateProxy;

public class ProxyTool {
	private ProxyTool() {
	}

	@SuppressWarnings("unchecked")
	public static <T> T getRealObject(T t) {
		if (t instanceof HibernateProxy) {
			return (T) ((HibernateProxy) t).
					getHibernateLazyInitializer().
					getImplementation();
		}
		return t;
	}
}
