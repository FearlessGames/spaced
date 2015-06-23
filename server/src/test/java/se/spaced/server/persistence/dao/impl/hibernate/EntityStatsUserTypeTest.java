package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.Session;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.cfg.Configuration;
import org.junit.Test;
import se.fearless.common.time.TimeProvider;
import se.spaced.shared.model.stats.EntityStats;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EntityStatsUserTypeTest extends PersistentTestBase {

	private static final double EPSILON = 1e-10;

	@Test
	public void testUserType() {
		EntityStats stats = new EntityStats(injector.getInstance(TimeProvider.class));
		stats.getStamina().changeValue(10);
		stats.getShieldRecoveryRate().changeValue(0.3);
		stats.getBaseCoolRate().changeValue(EntityStats.IN_COMBAT_COOLRATE);
		stats.getAttackRating().changeValue(2);

		EntityStats otherStats = new EntityStats(injector.getInstance(TimeProvider.class));
		stats.getStamina().changeValue(9);

		Foo f = new Foo();
		f.pk = 3;
		f.stats = stats;

		transactionManager.beginTransaction();
		Session session = transactionManager.getCurrentSession();
		session.saveOrUpdate(f);
		session.flush();
		transactionManager.getCurrentSession().getTransaction().commit();

		transactionManager.beginTransaction();
		session = transactionManager.getCurrentSession();
		Foo f2 = (Foo) session.get(Foo.class, 3);

		assertEquals(0d, stats.getCurrentHealth().getValue(), EPSILON);
		assertEquals(EntityStats.IN_COMBAT_COOLRATE, f2.stats.getBaseCoolRate().getValue(), EPSILON);
		assertEquals(0.3, f2.stats.getShieldRecoveryRate().getValue(), EPSILON);
		assertEquals(2, f2.stats.getAttackRating().getValue(), EPSILON);
		assertEquals(f.stats, f2.stats);
		assertFalse(f2.stats.equals(otherStats));

		transactionManager.getCurrentSession().getTransaction().commit();
	}

	@Override
	public void annotateClasses(Configuration config) {
		config.addAnnotatedClass(Foo.class);
	}

	@Entity(name = "Foo")
	public static class Foo {
		@Type(type = "se.spaced.server.persistence.dao.impl.hibernate.types.EntityStatsUserType")
		@Columns(columns = {
				@Column(name = "STAMINA"),
				@Column(name = "CURRENT_HEALTH"),
				@Column(name = "CURRENT_HEAT"),
				@Column(name = "SHIELD_POWER"),
				@Column(name = "COOL_RATE"),
				@Column(name = "RECOVERY_RATE"),
				@Column(name = "ATTACK_RATING")
		}
		)
		EntityStats stats;

		@Id
		int pk;

		public Foo() {
		}
	}
}
