package se.spaced.server.model.spawn;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.loot.LootTemplateProbability;
import se.spaced.server.loot.MultiLootTemplate;
import se.spaced.server.loot.PersistableLootTemplate;
import se.spaced.server.loot.SingleItemLootTemplate;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.Inventory;
import se.spaced.server.model.items.InventoryType;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.spell.effect.RangeableEffect;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.util.math.interval.IntervalInt;

import java.util.HashSet;

import static org.junit.Assert.assertNull;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;
import static se.spaced.server.model.items.InventoryAssertUtils.assertItemCountInInventory;


public class KillForLootTest extends ScenarioTestBase {

	private PlayerMockFactory playerMockFactory;

	@Before
	public void setUp() {
		stubReturn(null).on(transactionManager).rebuildFromDataBase(any(Object.class));
		playerMockFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
	}

	private Player createPlayer(String name) {
		Player player = playerFactory.createPlayer(name);
		entityService.addEntity(player, MockUtil.deepMock(S2CProtocol.class));
		return player;
	}

	private void spawnMob(Mob target) {
		entityService.addEntity(target, MockUtil.deepMock(S2CProtocol.class));
		MobLifecycle owner = mock(MobLifecycle.class);
		spawnService.registerMob(owner, target, mock(MobBrain.class));
	}

	private ServerItemTemplate createItemTemplate(String name) {
		itemService.addTemplate(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				name,
				ItemType.CONSUMABLE).build());
		return itemService.getTemplateByName(name);
	}

	private void assertInventorySize(Player entity, int expectedSize) {
		Inventory inventory = inventoryService.getInventory(entity, InventoryType.BAG);
		assertItemCountInInventory(expectedSize, inventory);
	}

	@Test
	public void killingGetsYouLewts() throws Exception {
		ServerItemTemplate canOfSlurm = createItemTemplate("Can of Slurm");

		Player attacker = createPlayer("Attacker");
		PersistableLootTemplate lootTemplate = new SingleItemLootTemplate(uuidFactory.combUUID(),
				"templateName",
				canOfSlurm);
		Mob target = new MobTemplate.Builder(uuidFactory.randomUUID(),
				"Foo").lootTemplate(lootTemplate).build().createMob(timeProvider,
				uuidFactory.randomUUID(), randomProvider);
		inventoryService.createInventory(attacker, 10, InventoryType.BAG);

		spawnMob(target);
		assertInventorySize(attacker, 0);

		RangeableEffect effect = mock(RangeableEffect.class);
		when(effect.getRange()).thenReturn(new IntervalInt(1, 1));
		spellCombatService.doDamage(attacker, target, timeProvider.now(), effect, "Foo");
		smrtBroadcaster.create().toCombat(target).send().combat().entityWasKilled(attacker, target);

		assertInventorySize(attacker, 1);
	}

	@Test
	public void takingPartGetsYouLewts() throws Exception {
		ServerItemTemplate canOfSlurm = createItemTemplate("Can of Slurm");
		ServerItemTemplate popplers = createItemTemplate("Bucket of Popplers");

		Player attacker = createPlayer("Attacker");
		Player killer = createPlayer("Killer");
		PersistableLootTemplate canOfSlurmTemplate = new SingleItemLootTemplate(uuidFactory.combUUID(),
				"slurmTemplate",
				canOfSlurm);
		PersistableLootTemplate popplersTemplate = new SingleItemLootTemplate(uuidFactory.combUUID(),
				"popplersTemplate",
				canOfSlurm);
		HashSet<LootTemplateProbability> lootTemplates = Sets.newHashSet(new LootTemplateProbability(uuidFactory.randomUUID(), canOfSlurmTemplate, 1.0),
				new LootTemplateProbability(uuidFactory.randomUUID(), popplersTemplate, 1.0));
		PersistableLootTemplate composite = new MultiLootTemplate(uuidFactory.randomUUID(), lootTemplates);
		Mob target = new MobTemplate.Builder(uuidFactory.randomUUID(),
				"Foo").lootTemplate(composite).build().createMob(timeProvider,
				uuidFactory.randomUUID(), randomProvider);
		inventoryService.createInventory(attacker, 10, InventoryType.BAG);
		inventoryService.createInventory(killer, 10, InventoryType.BAG);

		spawnMob(target);
		assertInventorySize(attacker, 0);
		assertInventorySize(killer, 0);

		RangeableEffect effect = mock(RangeableEffect.class);
		when(effect.getRange()).thenReturn(new IntervalInt(1, 1));
		spellCombatService.doDamage(attacker, target, timeProvider.now(), effect, "Foo");
		spellCombatService.doDamage(killer, target, timeProvider.now(), effect, "Foo");
		smrtBroadcaster.create().toCombat(target).send().combat().entityWasKilled(killer, target);

		assertInventorySize(attacker, 2);
		assertInventorySize(killer, 2);
	}


	@Test
	public void contributeInAnotherCombatGetsYouNoLoot() throws Exception {
		ServerItemTemplate canOfSlurm = createItemTemplate("Can of Slurm");

		Player attacker = createPlayer("Attacker");
		PersistableLootTemplate lootTemplate = new SingleItemLootTemplate(uuidFactory.combUUID(),
				"templateName",
				canOfSlurm);
		Mob target = new MobTemplate.Builder(uuidFactory.randomUUID(),
				"Foo").lootTemplate(lootTemplate).build().createMob(timeProvider,
				uuidFactory.randomUUID(), randomProvider);
		inventoryService.createInventory(attacker, 10, InventoryType.BAG);

		spawnMob(target);
		assertInventorySize(attacker, 0);

		RangeableEffect effect = mock(RangeableEffect.class);
		when(effect.getRange()).thenReturn(new IntervalInt(1, 1));
		spellCombatService.doDamage(attacker, target, timeProvider.now(), effect, "Foo");

		smrtBroadcaster.create().toAll().send().combat().combatStatusChanged(target, false);
		smrtBroadcaster.create().toAll().send().combat().combatStatusChanged(attacker, false);
		smrtBroadcaster.create().toCombat(target).send().combat().entityWasKilled(attacker, target);
		assertInventorySize(attacker, 0);
	}

	@Test
	public void mobKillingGetsYouNothing() throws Exception {

		ServerEntity attacker = mock(ServerEntity.class);
		ServerEntity target = mock(ServerEntity.class);
		itemService.addTemplate(new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Can of Slurm",
				ItemType.CONSUMABLE).build());

		spawnService.getReceiver().combat().entityWasKilled(attacker, target);

		assertNull(inventoryService.getInventory(attacker, InventoryType.BAG));
	}


	@Test
	public void scenarioTest() throws Exception {
		ServerItemTemplate canOfSlurm = createItemTemplate("Can of Slurm");

		Player attacker = createPlayer("Attacker");
		PersistableLootTemplate lootTemplate = new SingleItemLootTemplate(uuidFactory.combUUID(),
				"templateName",
				canOfSlurm);
		Mob target = new MobTemplate.Builder(uuidFactory.randomUUID(),
				"Foo").lootTemplate(lootTemplate).stamina(1).shieldRecovery(0).maxShield(0).build().createMob(timeProvider,
				uuidFactory.randomUUID(), randomProvider);
		inventoryService.createInventory(attacker, 10, InventoryType.BAG);

		spawnMob(target);

		RangeableEffect effect = mock(RangeableEffect.class);
		when(effect.getRange()).thenReturn(new IntervalInt(1, 1));
		for (int i = 0; i < 7; i++) {
			spellCombatService.doDamage(attacker, target, timeProvider.now(), effect, "Dmg");
			tick(1000);
		}

		assertInventorySize(attacker, 1);
	}
}