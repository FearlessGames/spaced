package se.spaced.client.model;

import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.TimeProvider;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.client.model.listener.ClientEntityListener;
import se.spaced.messages.protocol.Entity;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.CreatureType;
import se.spaced.shared.model.EntityState;
import se.spaced.shared.model.Faction;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.util.ListenerDispatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ClientEntityEqualityTest {
	private Entity a1;
	private Entity a2;
	private Entity a3;
	private Entity b1;
	private Entity b2;
	private TimeProvider timeProvider;

	@Before
	public void setUp() throws Exception {
		timeProvider = new MockTimeProvider();
		a1 = new ClientEntity(new EntityData(new UUID(0,1), "A1", new PositionalData(), new AppearanceData(), CreatureType.NULL_TYPE, new EntityStats(timeProvider), new Faction("The A Team"), AnimationState.IDLE, EntityState.ALIVE), ListenerDispatcher.create(ClientEntityListener.class));
		a2 = new ClientEntityProxy(new UUID(0,1));
		a3 = new ClientEntityProxy(new UUID(0,1));
		b1 = new ClientEntity(new EntityData(new UUID(0,2), "B1", new PositionalData(), new AppearanceData(), CreatureType.NULL_TYPE, new EntityStats(timeProvider), new Faction("The B Team"), AnimationState.IDLE, EntityState.ALIVE), ListenerDispatcher.create(ClientEntityListener.class));
		b2 = new ClientEntityProxy(new UUID(0,2));
	}

	@Test
	public void reflexive() throws Exception {
		assertEquals(a1, a1);
		assertEquals(a2, a2);
		assertEquals(b1, b1);
		assertEquals(b2, b2);
	}

	@Test
	public void symmetric() throws Exception {
		assertEquals(a1, a2);
		assertEquals(a2, a1);

		assertEquals(b1, b2);
		assertEquals(b2, b1);

		assertFalse(a1.equals(b1));
		assertFalse(a1.equals(b2));
		assertFalse(a2.equals(b1));
		assertFalse(a2.equals(b2));

		assertFalse(b1.equals(a1));
		assertFalse(b1.equals(a2));
		assertFalse(b2.equals(a1));
		assertFalse(b2.equals(a2));
	}

	@Test
	public void transitive() throws Exception {
		assertEquals(a1, a2);
		assertEquals(a2, a3);
		assertEquals(a1, a3);
	}

	@Test
	public void nullSafe() throws Exception {
		assertFalse(a1.equals(null));
		assertFalse(a2.equals(null));
		assertFalse(a3.equals(null));
		assertFalse(b1.equals(null));
		assertFalse(b2.equals(null));
	}

	@Test
	public void hashcode() throws Exception {
		assertEquals(a1.hashCode(), a2.hashCode());
		assertEquals(b1.hashCode(), b2.hashCode());
		assertFalse(a1.hashCode() == b1.hashCode());
	}
}
