package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.spaced.server.persistence.dao.impl.PersistableBase;
import se.spaced.server.persistence.dao.interfaces.Dao;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class DaoImplTest extends PersistentTestBase {

	private PersistableIntDao dao;
	private Transaction transaction;


	@Before
	public void setup() {
		dao = new PersistableIntDaoImpl(sessionFactory);
		transaction = transactionManager.beginTransaction();
	}

	@After
	public void teardown() {
		if (transaction != null) {
			transaction.rollback();
		}
	}

	@Override
	protected void addAnnotatedClasses(Configuration config) {
		config.addAnnotatedClass(PersistableInt.class);
		config.addAnnotatedClass(PersistableComplex.class);
	}


	@Test
	public void saveSimple() {
		PersistableInt foo = new PersistableInt(3);

		PersistableInt saved = dao.persist(foo);
		//sessionFactory.getCurrentSession().flush();
		assertNotNull(foo.getPk());
		assertEquals("Save does not change values", foo.getValue(), saved.getValue());
	}

	@Test
	public void saveTwiceSucceeds() {
		PersistableInt foo = new PersistableInt(3);

		dao.persist(foo);
		//sessionFactory.getCurrentSession().flush();
		dao.persist(foo);
		//sessionFactory.getCurrentSession().flush();
	}

	@Test
	public void findByPk() {
		PersistableInt saved = dao.persist(new PersistableInt(3));
		//sessionFactory.getCurrentSession().flush();
		PersistableInt retrieved = dao.findByPk(saved.getPk());
		assertNotNull(retrieved);
		assertEquals(saved, retrieved);
	}

	@Test
	public void changeAndDontSave() {
		PersistableInt saved = dao.persist(new PersistableInt(3));
		//sessionFactory.getCurrentSession().flush();
		saved.setValue(4);
		PersistableInt retrieved = dao.findByPk(saved.getPk());
		assertNotNull(retrieved);
		assertEquals(4, retrieved.getValue());
		assertEquals(4, saved.getValue());
	}

	@Test
	public void changeAndSave() {
		PersistableInt saved = dao.persist(new PersistableInt(3));
		//sessionFactory.getCurrentSession().flush();
		saved.setValue(4);
		dao.persist(saved);
		PersistableInt retrieved = dao.findByPk(saved.getPk());
		assertNotNull(retrieved);
		assertEquals(4, retrieved.getValue());
		assertEquals(4, saved.getValue());
	}

	@Test
	public void findAll() {
		dao.persist(new PersistableInt(3));
		dao.persist(new PersistableInt(5));
		dao.persist(new PersistableInt(7));
		//sessionFactory.getCurrentSession().flush();
		Collection<PersistableInt> all = dao.findAll();
		assertEquals("Didn't find all elements", 3, all.size());
	}

	@Test
	public void delete() {
		PersistableInt saved = dao.persist(new PersistableInt(3));
		//sessionFactory.getCurrentSession().flush();
		PersistableInt retrieved = dao.findByPk(saved.getPk());
		assertNotNull(retrieved);
		assertEquals(saved, retrieved);

		dao.delete(saved);
		//sessionFactory.getCurrentSession().flush();
		retrieved = dao.findByPk(saved.getPk());
		assertNull(retrieved);
	}

	@Test
	public void findByField() {
		PersistableInt saved = dao.persist(new PersistableInt(3));
		//sessionFactory.getCurrentSession().flush();
		List<PersistableInt> retrieved = dao.findByValue(2);
		assertNotNull(retrieved);
		assertFalse(retrieved.contains(saved));
		assertTrue(retrieved.isEmpty());

		retrieved = dao.findByValue(3);
		assertNotNull(retrieved);
		assertTrue(retrieved.contains(saved));
		assertEquals(1, retrieved.size());
	}

	@Test
	public void storeComplex() {
		DaoImpl<PersistableComplex> dao2 = new DaoImpl<PersistableComplex>(sessionFactory, PersistableComplex.class);
		PersistableComplex complex = dao2.persist(new PersistableComplex(new PersistableInt(5)));
		//sessionFactory.getCurrentSession().flush();
		PersistableComplex retrieved = dao2.findByPk(complex.getPk());
		assertNotNull(retrieved);
		assertEquals(complex, retrieved);
		assertEquals(5, retrieved.getIntValue().getValue());
	}

	@Test
	public void storeInMultiThreadedEnviroment() {
		Thread thread1 = new Thread(new StoreRunner(dao, new PersistableInt(1), transactionManager));
		Thread thread2 = new Thread(new StoreRunner(dao, new PersistableInt(2), transactionManager));
		Thread thread3 = new Thread(new StoreRunner(dao, new PersistableInt(3), transactionManager));
		Thread thread4 = new Thread(new StoreRunner(dao, new PersistableInt(4), transactionManager));

		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();

		while (thread1.isAlive() &&
				thread2.isAlive() &&
				thread3.isAlive() &&
				thread4.isAlive()) {
			Thread.yield();
		}

	}

	private static class StoreRunner implements Runnable {
		private PersistableIntDao dao;
		private PersistableInt intToSave;
		private TransactionManager transactionManager;

		private StoreRunner(PersistableIntDao dao, PersistableInt intToSave, TransactionManager transactionManager) {
			this.dao = dao;
			this.intToSave = intToSave;
			this.transactionManager = transactionManager;
		}

		@Override
		public void run() {
			Transaction tx = transactionManager.beginTransaction();
			dao.persist(intToSave);
			tx.commit();
		}
	}

	@Entity(name = "PersistableInt")
	private static class PersistableInt extends PersistableBase {
		private int value;


		private PersistableInt(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	}

	@Entity(name = "PersistableComplex")
	private static class PersistableComplex extends PersistableBase {
		@OneToOne
		private PersistableInt intValue;

		public PersistableComplex(PersistableInt persistableInt) {
			intValue = persistableInt;
		}

		public PersistableInt getIntValue() {
			return intValue;
		}
	}

	private interface PersistableIntDao extends Dao<PersistableInt> {
		List<PersistableInt> findByValue(int value);
	}

	private static class PersistableIntDaoImpl extends DaoImpl<PersistableInt> implements PersistableIntDao {

		public PersistableIntDaoImpl(SessionFactory sessionFactory) {
			super(sessionFactory, PersistableInt.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<PersistableInt> findByValue(int value) {
			Criteria crit = getSession().createCriteria(PersistableInt.class);
			crit.add(Restrictions.eq("value", value));
			return (List<PersistableInt>) crit.list();
		}
	}

}
