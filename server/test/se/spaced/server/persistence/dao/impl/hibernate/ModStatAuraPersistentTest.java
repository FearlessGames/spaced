package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.uuid.UUID;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.spaced.server.model.aura.ModStatAura;
import se.spaced.shared.model.aura.ModStat;
import se.spaced.shared.model.stats.Operator;
import se.spaced.shared.model.stats.StatType;

import java.security.SecureRandom;

import static org.junit.Assert.assertEquals;

public class ModStatAuraPersistentTest extends PersistentTestBase {
	private final MockTimeProvider timeProvider = new MockTimeProvider();
	private final UUIDFactory uuidFactory = new UUIDFactoryImpl(timeProvider, new SecureRandom());


	@Test
	public void testPersist() {
		ModStat speedStat = new ModStat(100, StatType.SPEED, Operator.ADD);
		ModStat staminaStat = new ModStat(200, StatType.STAMINA, Operator.ADD);
		ModStatAura modStatAura = new ModStatAura("test", "./", 1000, true, 5, true, speedStat, staminaStat);
		UUID pk = uuidFactory.combUUID();
		modStatAura.setPk(pk);

		Session session = sessionFactory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		session.persist(modStatAura);
		tx.commit();


		session = sessionFactory.getCurrentSession();
		tx = session.beginTransaction();
		modStatAura = (ModStatAura) session.get(ModStatAura.class, pk);
		tx.commit();
		assertEquals(modStatAura.getMods().size(), 2);

	}
}

