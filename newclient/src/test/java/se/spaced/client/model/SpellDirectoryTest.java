package se.spaced.client.model;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.util.SystemTimeProvider;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.spaced.client.ardor.ui.events.SpellEvents;
import se.spaced.client.net.messagelisteners.SpellCacheImpl;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.Spell;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.network.protocol.codec.datatype.SpellData;
import se.spaced.shared.util.math.interval.IntervalInt;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import static se.mockachino.Mockachino.*;


public class SpellDirectoryTest {
	private EventHandler eventHandler;
	private SpellDirectory spellDirectory;
	private UUIDFactory uuidFactory;
	private ActiveCache<Spell, ClientSpell> spellCache;

	@Before
	public void setup() {
		uuidFactory = new UUIDFactoryImpl(new SystemTimeProvider(), new Random());
		eventHandler = mock(EventHandler.class);

		ServerConnection serverConnection = MockUtil.deepMock(ServerConnection.class);
		spellCache = new SpellCacheImpl(serverConnection);

		spellDirectory = new SpellDirectory(eventHandler, spellCache);
	}

	@Test
	public void gotSpellBookInfo() {
		UUID firstId = uuidFactory.randomUUID();
		UUID secondId = uuidFactory.randomUUID();
		List<ClientSpell> spellsFromServer = Arrays.asList(
				new ClientSpell(new SpellData(firstId)),
				new ClientSpell(new SpellData(secondId))
		);
		for (ClientSpell spell : spellsFromServer) {
			spellCache.setValue(spell, spell);
		}

		spellDirectory.setSpellbook(spellsFromServer);

		Collection<ClientSpell> spellCollection = spellDirectory.getUsersSpells();

		compareCollections(spellsFromServer, spellCollection);
	}

	private void pushSpell(ClientSpell spell) {
		spellCache.setValue(spell, spell);
	}

	@Test
	public void updateOfSpellbookTriggersEvent() {
		UUID firstId = uuidFactory.randomUUID();
		UUID secondId = uuidFactory.randomUUID();
		List<? extends Spell> spellsFromServer = Arrays.asList(
				new ClientSpell(new SpellData(firstId)),
				new ClientSpell(new SpellData(secondId))

		);
		spellDirectory.setSpellbook(spellsFromServer);

		UUID thirdId = uuidFactory.randomUUID();
		UUID fourthId = uuidFactory.randomUUID();

		pushSpell(new ClientSpell(new SpellData(thirdId, "foo", 100, MagicSchool.FIRE, true, new IntervalInt(10, 20), "bar", 3, false)));
		pushSpell(new ClientSpell(new SpellData(fourthId, "foo", 100, MagicSchool.FIRE, true, new IntervalInt(10, 20), "bar", 4,
				false)));
		verifyNever().on(eventHandler).fireEvent(SpellEvents.SPELLBOOK_UPDATED);

		pushSpell(new ClientSpell(new SpellData(firstId, "foo", 100, MagicSchool.FIRE, true, new IntervalInt(10, 20), "bar", 3,
				false)));
		verifyOnce().on(eventHandler).fireAsynchEvent(SpellEvents.SPELLBOOK_UPDATED);
	}

	@Test
	public void dataUpdates() {
		UUID firstId = uuidFactory.randomUUID();
		UUID secondId = uuidFactory.randomUUID();
		List<? extends Spell> spellsFromServer = Arrays.asList(
				new ClientSpell(new SpellData(firstId)),
				new ClientSpell(new SpellData(secondId))

		);
		spellDirectory.setSpellbook(spellsFromServer);

		UUID thirdId = uuidFactory.randomUUID();
		SpellData spellData = new SpellData(firstId,
				"foo",
				100,
				MagicSchool.ELECTRICITY,
				true,
				new IntervalInt(10, 30),
				"bar",
				6, false);
		pushSpell(new ClientSpell(new SpellData(thirdId, "foo", 100, MagicSchool.FIRE, true, new IntervalInt(10, 20), "bar", 7,
				false)));
		pushSpell(new ClientSpell(spellData));

		ClientSpell clientSpell = spellCache.getValue(new ClientSpellProxy(firstId));
		assertNotNull(clientSpell);
		assertEquals(spellData.getSchool(), clientSpell.getSchool());
		assertEquals(spellData.getRanges(), clientSpell.getRanges());
		assertEquals(spellData.getCastTime(), clientSpell.getCastTime());
		assertEquals(spellData.getHeat(), clientSpell.getHeat());
	}

	public static <T> void compareCollections(Collection<? extends T> collection1, Collection<? extends T> collection2) {
		for (T t : collection1) {
			assertTrue(collection2.contains(t));
		}
		for (T t : collection2) {
			assertTrue(collection1.contains(t));
		}
	}
}
