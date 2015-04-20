package se.spaced.server.persistence.util;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.persistence.dao.interfaces.Persistable;

import java.io.Serializable;

public class GuiceHibernateInterceptor extends EmptyInterceptor {
	private static final Logger log = LoggerFactory.getLogger(GuiceHibernateInterceptor.class);

	private final Injector injector;

	@Inject
	public GuiceHibernateInterceptor(Injector injector) {
		this.injector = injector;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object instantiate(String entityName, EntityMode entityMode, Serializable id) throws CallbackException {
		try {
			Class clazz = Class.forName(entityName);
			Object o = injector.getInstance(clazz);
			if (o instanceof Persistable) {
				Persistable p = (Persistable) o;
				p.setPk((UUID) id);
				return p;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error("Failed to create instance of " + entityName + " with id " + id, e);
			return null;
		}
	}
}
