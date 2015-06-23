package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.spaced.server.model.Player;
import se.spaced.server.model.items.EquippedItems;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.persistence.dao.interfaces.EquipmentDao;
import se.spaced.server.persistence.dao.interfaces.PlayerDao;

import java.security.SecureRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EquipmentDaoImplTest extends PersistentTestBase {

	private final MockTimeProvider timeProvider = new MockTimeProvider();
	private final UUIDFactory uuidFactory = new UUIDFactoryImpl(timeProvider, new SecureRandom());

	private EquipmentDao equipmentDao;
	private PlayerDao playerDao;

	@Before
	public void setup() {
		equipmentDao = new EquipmentDaoImpl(sessionFactory);
		playerDao = new PlayerDaoImpl(sessionFactory);
	}

	@Test
	public void testFindByPlayer() throws Exception {
		Transaction transaction = transactionManager.beginTransaction();
		PlayerMockFactory factory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();

		Player player = factory.createPlayer("foo");
		player.setPk(null);
		playerDao.persist(player);
		equipmentDao.persist(new EquippedItems(player));
		EquippedItems items = equipmentDao.findByOwner(player);
		assertNotNull(items);
		transaction.commit();

		transaction = transactionManager.beginTransaction();
		EquippedItems items2 = equipmentDao.findByOwner(player);
		assertEquals(items, items2);
		transaction.commit();
	}
}
