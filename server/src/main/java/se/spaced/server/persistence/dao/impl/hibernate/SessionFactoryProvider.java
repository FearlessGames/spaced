package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import se.fearless.common.lifetime.LifetimeListener;
import se.fearless.common.lifetime.LifetimeManager;
import se.spaced.server.persistence.DBSettings;
import se.spaced.server.persistence.util.GuiceHibernateInterceptor;

@Singleton
public class SessionFactoryProvider implements Provider<SessionFactory> {
	private final SessionFactory sessionFactory;

	@Inject
	public SessionFactoryProvider(
			GuiceHibernateInterceptor guiceHibernateInterceptor,
			final DBSettings dbSettings,
			LifetimeManager lifetimeManager) {

		lifetimeManager.addListener(new LifetimeListener() {
			@Override
			public void onStart() {
			}

			@Override
			public void onShutdown() {
				sessionFactory.close();
				dbSettings.shutdownDatabase();
			}
		});

		dbSettings.startDatabase();

		Configuration config = new Configuration();

		config.setProperty("hibernate.dialect", dbSettings.getDialect());
		config.setProperty("hibernate.connection.driver_class", dbSettings.getDriver());
		config.setProperty("hibernate.connection.url", dbSettings.getJdbcUrl());
		config.setProperty("hibernate.connection.username", dbSettings.getUsername());
		config.setProperty("hibernate.connection.password", dbSettings.getPassword());


		config.setProperty("hibernate.connection.pool_size", "100").
				setProperty("hibernate.connection.autocommit", "true").
				setProperty("hibernate.show_sql", "false").
				setProperty("hibernate.current_session_context_class", "thread").
				setProperty("hibernate.hbm2ddl.auto", "update");

		annotateClasses(config);
		config.setInterceptor(guiceHibernateInterceptor);
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
		sessionFactory = config.buildSessionFactory(serviceRegistry);
	}


	@Override
	public SessionFactory get() {
		return sessionFactory;
	}

	private void annotateClasses(Configuration config) {
		StandardClassAnnotator.annotateClasses(config);
	}

}
