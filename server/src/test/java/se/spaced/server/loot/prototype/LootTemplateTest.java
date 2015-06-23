package se.spaced.server.loot.prototype;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDMockFactory;
import se.spaced.server.loot.*;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.shared.util.random.RandomProvider;
import se.spaced.shared.util.random.RealRandomProvider;

import java.util.*;

import static org.junit.Assert.*;
import static se.mockachino.Mockachino.mock;


public class LootTemplateTest {
	private RandomProvider randomProvider;
	private UUIDFactory uuidFactory;

	@Before
	public void setup() {
		randomProvider = new RealRandomProvider(new Random(7));
		uuidFactory = new UUIDMockFactory();
	}

	//@Test invalid
	public void testSimpleCaseNoLootForYou() {
		ServerItemTemplate itemTemplate = mock(ServerItemTemplate.class);
		SingleItemLootTemplate loot = new SingleItemLootTemplate(uuidFactory.combUUID(), "templateName", itemTemplate);

		Collection<Loot> list = loot.generateLoot(randomProvider);

		assertTrue(list.isEmpty());
	}

	@Test
	public void testSimpleCaseAlwaysGetLoot() {
		ServerItemTemplate itemTemplate = mock(ServerItemTemplate.class);
		SingleItemLootTemplate loot = new SingleItemLootTemplate(uuidFactory.combUUID(), "templateName", itemTemplate);

		Collection<Loot> list = loot.generateLoot(randomProvider);

		assertFalse(list.isEmpty());
		assertEquals(itemTemplate, list.iterator().next().getItemTemplate());
	}

	//@Test invalid
	public void testSimpleCaseSometimesGetLoot() {
		ServerItemTemplate itemTemplate = mock(ServerItemTemplate.class);
		SingleItemLootTemplate loot = new SingleItemLootTemplate(uuidFactory.combUUID(), "templateName", itemTemplate);

		int count = 0;
		for (int i = 0; i < 100; i++) {
			Collection<Loot> list = loot.generateLoot(randomProvider);
			assertTrue(1 >= list.size());
			count += list.size();
		}

		assertEquals(49, count);
	}

	@Test
	public void testSimpleMultiLoot() {
		ServerItemTemplate itemTemplate = mock(ServerItemTemplate.class);
		SingleItemLootTemplate loot1 = new SingleItemLootTemplate(uuidFactory.combUUID(), "templateName", itemTemplate);
		SingleItemLootTemplate loot2 = new SingleItemLootTemplate(uuidFactory.combUUID(), "templateName", itemTemplate);
		HashSet<LootTemplateProbability> set = Sets.newHashSet(
				new LootTemplateProbability(uuidFactory.combUUID(), loot1, 1.0),
				new LootTemplateProbability(uuidFactory.combUUID(), loot2, 1.0));
		MultiLootTemplate lootTemplate = new MultiLootTemplate(new UUID(23532, 2342), set);

		Collection<Loot> loots = lootTemplate.generateLoot(randomProvider);
		assertEquals(2, loots.size());
	}

	@Test
	public void testSelectOneOfFour() {
		ServerItemTemplate item1 = mock(ServerItemTemplate.class);
		ServerItemTemplate item2 = mock(ServerItemTemplate.class);
		ServerItemTemplate item3 = mock(ServerItemTemplate.class);
		ServerItemTemplate item4 = mock(ServerItemTemplate.class);
		LootTemplateProbability loot1 = new LootTemplateProbability(uuidFactory.combUUID(), new SingleItemLootTemplate(
				uuidFactory.combUUID(), "templateName", item1), 1.0);
		LootTemplateProbability loot2 = new LootTemplateProbability(uuidFactory.combUUID(), new SingleItemLootTemplate(
				uuidFactory.combUUID(), "templateName", item2), 1.0);
		LootTemplateProbability loot3 = new LootTemplateProbability(uuidFactory.combUUID(), new SingleItemLootTemplate(
				uuidFactory.combUUID(), "templateName", item3), 1.0);
		LootTemplateProbability loot4 = new LootTemplateProbability(uuidFactory.combUUID(), new SingleItemLootTemplate(
				uuidFactory.combUUID(), "templateName", item4), 1.0);

		KofNLootTemplate lootTemplate = new KofNLootTemplate(uuidFactory.combUUID(),
				"templateName",
				1, Sets.<LootTemplateProbability>newHashSet(loot1, loot2, loot3, loot4)
		);

		Multiset<ServerItemTemplate> freq = ConcurrentHashMultiset.create();
		for (int i = 0; i < 1000; i++) {
			Collection<Loot> list = lootTemplate.generateLoot(randomProvider);
			assertEquals(1, list.size());
			freq.add(list.iterator().next().getItemTemplate());
		}
		//System.out.println(freq);
		assertNear(250, freq.count(item1), 25);
		assertNear(250, freq.count(item2), 25);
		assertNear(250, freq.count(item3), 25);
		assertNear(250, freq.count(item4), 25);
	}

	@Test
	public void testSelectOneOfFourWithDifferentProbabilities() {
		ServerItemTemplate item1 = mock(ServerItemTemplate.class);
		ServerItemTemplate item2 = mock(ServerItemTemplate.class);
		ServerItemTemplate item3 = mock(ServerItemTemplate.class);
		ServerItemTemplate item4 = mock(ServerItemTemplate.class);
		LootTemplateProbability loot1 = new LootTemplateProbability(uuidFactory.combUUID(), new SingleItemLootTemplate(
				uuidFactory.combUUID(), "templateName", item1), 0.1);
		LootTemplateProbability loot2 = new LootTemplateProbability(uuidFactory.combUUID(), new SingleItemLootTemplate(
				uuidFactory.combUUID(), "templateName", item2), 0.2);
		LootTemplateProbability loot3 = new LootTemplateProbability(uuidFactory.combUUID(), new SingleItemLootTemplate(
				uuidFactory.combUUID(), "templateName", item3), 0.3);
		LootTemplateProbability loot4 = new LootTemplateProbability(uuidFactory.combUUID(), new SingleItemLootTemplate(
				uuidFactory.combUUID(), "templateName", item4), 0.4);

		KofNLootTemplate lootTemplate = new KofNLootTemplate(uuidFactory.combUUID(),
				"templateName",
				1, Sets.<LootTemplateProbability>newHashSet(loot1, loot2, loot3, loot4)
		);

		Multiset<ServerItemTemplate> freq = ConcurrentHashMultiset.create();
		for (int i = 0; i < 1000; i++) {
			Collection<Loot> list = lootTemplate.generateLoot(randomProvider);
			assertEquals(1, list.size());
			freq.add(list.iterator().next().getItemTemplate());
		}
		//System.out.println(freq);
		assertNear(100, freq.count(item1), 35);
		assertNear(200, freq.count(item2), 35);
		assertNear(300, freq.count(item3), 35);
		assertNear(400, freq.count(item4), 35);
	}

	@Test
	public void testSelectTwoOfFourWithSameProbabilities() {
		ServerItemTemplate item1 = mock(ServerItemTemplate.class);
		ServerItemTemplate item2 = mock(ServerItemTemplate.class);
		ServerItemTemplate item3 = mock(ServerItemTemplate.class);
		ServerItemTemplate item4 = mock(ServerItemTemplate.class);
		LootTemplateProbability loot1 = new LootTemplateProbability(uuidFactory.combUUID(), new SingleItemLootTemplate(
				uuidFactory.combUUID(), "templateName", item1), 0.1);
		LootTemplateProbability loot2 = new LootTemplateProbability(uuidFactory.combUUID(), new SingleItemLootTemplate(
				uuidFactory.combUUID(), "templateName", item2), 0.1);
		LootTemplateProbability loot3 = new LootTemplateProbability(uuidFactory.combUUID(), new SingleItemLootTemplate(
				uuidFactory.combUUID(), "templateName", item3), 0.1);
		LootTemplateProbability loot4 = new LootTemplateProbability(uuidFactory.combUUID(), new SingleItemLootTemplate(
				uuidFactory.combUUID(), "templateName", item4), 0.1);

		KofNLootTemplate lootTemplate = new KofNLootTemplate(uuidFactory.combUUID(),
				"templateName",
				2, Sets.<LootTemplateProbability>newHashSet(loot1, loot2, loot3, loot4)
		);

		Multiset<ServerItemTemplate> freq = ConcurrentHashMultiset.create();
		for (int i = 0; i < 10000; i++) {
			Collection<Loot> list = lootTemplate.generateLoot(randomProvider);
			assertEquals(2, list.size());
			Iterator<Loot> iter = list.iterator();
			freq.add(iter.next().getItemTemplate());
			freq.add(iter.next().getItemTemplate());
		}
		System.out.println(freq);

		// total must be 20000, and distribution should be equal
		assertNear(5000, freq.count(item1), 100);
		assertNear(5000, freq.count(item2), 100);
		assertNear(5000, freq.count(item3), 100);
		assertNear(5000, freq.count(item4), 100);
	}

	@Test
	public void testSelectOneOfFourWithDifferentProbabilitiesInDifferentOrderings() {
		ServerItemTemplate item1 = mock(ServerItemTemplate.class);
		ServerItemTemplate item2 = mock(ServerItemTemplate.class);
		ServerItemTemplate item3 = mock(ServerItemTemplate.class);
		ServerItemTemplate item4 = mock(ServerItemTemplate.class);

		LootTemplateProbability loot1 = new LootTemplateProbability(uuidFactory.combUUID(), new SingleItemLootTemplate(
				uuidFactory.combUUID(), "templateName", item1), 0.1);
		LootTemplateProbability loot2 = new LootTemplateProbability(uuidFactory.combUUID(), new SingleItemLootTemplate(
				uuidFactory.combUUID(), "templateName", item2), 0.2);
		LootTemplateProbability loot3 = new LootTemplateProbability(uuidFactory.combUUID(), new SingleItemLootTemplate(
				uuidFactory.combUUID(), "templateName", item3), 0.3);
		LootTemplateProbability loot4 = new LootTemplateProbability(uuidFactory.combUUID(), new SingleItemLootTemplate(
				uuidFactory.combUUID(), "templateName", item4), 0.4);

		for (int j = 0; j < 10; j++) {
			ArrayList<LootTemplateProbability> inputs = Lists.newArrayList(loot1, loot2, loot3, loot4);
			Collections.shuffle(inputs, new Random(8 + j));
			KofNLootTemplate lootTemplate = new KofNLootTemplate(uuidFactory.combUUID(),
					"templateName",
					1,
					Sets.<LootTemplateProbability>newHashSet(inputs));

			Multiset<ServerItemTemplate> freq = ConcurrentHashMultiset.create();
			for (int i = 0; i < 1000; i++) {
				Collection<Loot> list = lootTemplate.generateLoot(randomProvider);
				assertEquals(1, list.size());
				freq.add(list.iterator().next().getItemTemplate());
			}
			//System.out.println(freq);
			assertNear(100, freq.count(item1), 50);
			assertNear(200, freq.count(item2), 50);
			assertNear(300, freq.count(item3), 50);
			assertNear(400, freq.count(item4), 50);
		}
	}

	private void assertNear(int expected, int actual, int error) {
		int diff = Math.abs(actual - expected);
		if (diff > error) {
			fail(String.format("expected %d to be near %d by a margin of %d", actual, expected, error));
		}
	}

}
