package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;
import se.fearless.common.stats.ModStat;
import se.fearless.common.stats.Operator;
import se.fearless.common.uuid.UUID;
import se.spaced.server.model.aura.ModStatAura;
import se.spaced.shared.model.stats.SpacedStatType;

import static org.junit.Assert.assertEquals;

public class ModStatAuraPersistentTest extends PersistentTestBase {

	@Test
	public void testPersist() {
		ModStat speedStat = new ModStat(100, SpacedStatType.SPEED, Operator.ADD);
		ModStat staminaStat = new ModStat(200, SpacedStatType.STAMINA, Operator.ADD);
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

