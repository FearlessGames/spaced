package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDMockFactory;
import se.spaced.server.CommandLineParser;
import se.spaced.server.guice.modules.*;
import se.spaced.server.persistence.util.GuiceHibernateInterceptor;
import se.spaced.server.persistence.util.transactions.TransactionProxyWrapper;

import java.util.concurrent.atomic.AtomicInteger;

@Ignore
public abstract class PersistentTestBase {

	protected final TimeProvider timeProvider = new MockTimeProvider();
	protected final UUIDFactory uuidFactory = new UUIDMockFactory();
	protected SessionFactory sessionFactory;
	protected TransactionManager transactionManager;
	protected DbType dbType = DbType.H2;
	protected final AtomicInteger projectileIdCounter = new AtomicInteger();
	protected TransactionProxyWrapper transactionProxyWrapper;
	protected DaoFactory daoFactory;
	protected Injector injector;

	@Before
	public void _setup() {
		Configuration config = new Configuration();

		config.setProperty("hibernate.connection.pool_size", "1").
				setProperty("hibernate.connection.autocommit", "true").
				setProperty("hibernate.show_sql", "false").
				setProperty("hibernate.current_session_context_class", "thread").
				setProperty("hibernate.hbm2ddl.auto", "create-drop");

		dbType.setup(config);

		injector = Guice.createInjector(
				new CommandLineParser(new H2MemDBSettings()).getModule(),
				new ServiceModule(),
				new PersistanceDaoModule(),
				new MockTimeModule(),
				new MockResourcesModule(), new XStreamModule());
		config.setInterceptor(new GuiceHibernateInterceptor(injector));
		annotateClasses(config);
		addAnnotatedClasses(config);

		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();
		sessionFactory = config.buildSessionFactory(serviceRegistry);
		sessionFactory.openSession();
		Session session = sessionFactory.getCurrentSession();
		System.out.println("session: " + session.hashCode());
		transactionManager = new TransactionManager(sessionFactory);
		transactionProxyWrapper = new TransactionProxyWrapper(transactionManager);
		daoFactory = new DaoFactory(sessionFactory, transactionProxyWrapper);
	}

	public void annotateClasses(Configuration config) {
		StandardClassAnnotator.annotateClasses(config);
	}

	protected void addAnnotatedClasses(Configuration config) {

	}

	@After
	public void tearDown() {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}


	enum DbType {
		MYSQL {
			@Override
			public void setup(Configuration config) {
				config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
				config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
				config.setProperty("hibernate.connection.url", "jdbc:mysql://localhost/spacedtestdb");
				config.setProperty("hibernate.connection.username", "root");
				config.setProperty("hibernate.connection.password", "");
			}
		},
		H2 {
			@Override
			public void setup(Configuration config) {
				config.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
				config.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
				config.setProperty("hibernate.connection.url", "jdbc:h2:mem:spaced_test_db");
			}
		};

		public abstract void setup(Configuration config);
	}

}
