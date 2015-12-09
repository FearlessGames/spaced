package se.spaced.server.services;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUIDFactory;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.mob.MobOrder;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.brains.GMPuppeteerBrain;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.mob.brains.templates.BrainTemplate;
import se.spaced.server.model.Mob;
import se.spaced.server.model.PersistedPositionalData;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.Action;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.action.ExceptionAction;
import se.spaced.server.model.currency.MoneyService;
import se.spaced.server.model.currency.PersistedCurrency;
import se.spaced.server.model.currency.PersistedMoney;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.items.*;
import se.spaced.server.model.movement.MovementService;
import se.spaced.server.model.spawn.BrainParameterProviderAdapter;
import se.spaced.server.model.spawn.MobLifecycle;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.SpawnService;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.listeners.auth.GmMobLifecycle;
import se.spaced.server.persistence.migrator.ServerContentPopulator;
import se.spaced.server.persistence.util.transactions.AutoTransaction;
import se.spaced.server.spell.SpellService;
import se.spaced.shared.util.random.RandomProvider;

public class GameMasterServiceImpl implements GameMasterService {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final MovementService movementService;
	private final TimeProvider timeProvider;
	private final InventoryService inventoryService;
	private final ItemService itemService;
	private final SmrtBroadcaster<S2CProtocol> broadcaster;
	private final SpawnService spawnService;
	private final ActionScheduler scheduler;
	private final EntityService entityService;
	private final UUIDFactory uuidFactory;
	private final MobOrderExecutor mobOrderExecutor;
	private final GmMobLifecycle gmMobLifecycle;
	private final ServerContentPopulator serverContentPopulator;
	private final SpellService spellService;
	private final MoneyService moneyService;
	private final RandomProvider randomProvider;

	@Inject
	public GameMasterServiceImpl(
			MovementService movementService,
			TimeProvider timeProvider,
			InventoryService inventoryService,
			ItemService itemService,
			SmrtBroadcaster<S2CProtocol> broadcaster,
			SpawnService spawnService,
			ActionScheduler scheduler,
			EntityService entityService,
			UUIDFactory uuidFactory,
			MobOrderExecutor mobOrderExecutor,
			GmMobLifecycle gmMobLifecycle,
			ServerContentPopulator serverContentPopulator,
			SpellService spellService,
			MoneyService moneyService,
			RandomProvider randomProvider) {
		this.movementService = movementService;
		this.timeProvider = timeProvider;
		this.inventoryService = inventoryService;
		this.itemService = itemService;
		this.broadcaster = broadcaster;
		this.spawnService = spawnService;
		this.scheduler = scheduler;
		this.entityService = entityService;
		this.uuidFactory = uuidFactory;
		this.mobOrderExecutor = mobOrderExecutor;
		this.gmMobLifecycle = gmMobLifecycle;
		this.serverContentPopulator = serverContentPopulator;
		this.spellService = spellService;
		this.moneyService = moneyService;
		this.randomProvider = randomProvider;
	}

	@Override
	@AutoTransaction
	public void visit(ServerEntity gm, ServerEntity entityToVisit) {
		movementService.teleportEntity(gm, entityToVisit.getPosition(), entityToVisit.getRotation(),
				timeProvider.now());
	}

	@Override
	@AutoTransaction
	public void giveItem(ServerEntity gm, ServerEntity player, ServerItemTemplate template, int quantity) {
		Inventory inventory = inventoryService.getInventory(player, InventoryType.BAG);
		for (int i = 0; i < quantity; i++) {
			ServerItem serverItem = template.create();
			itemService.persistItem(serverItem, player);
			try {
				inventoryService.add(inventory, serverItem);
			} catch (InventoryFullException e) {
				broadcaster.create().to(gm).send().gamemaster().failureNotification(
						"Inventory is full, cant give item " + template.getName());
				return;
			}
		}
		broadcaster.create().to(gm).send().gamemaster().successNotification(String.format("%s got %d %s",
				player.getName(),
				quantity,
				template.getName()));
		log.warn("{} was given {} {} by {}", player, quantity, template, gm.getName());
	}

	@Override
	@AutoTransaction
	public void reloadMob(final ServerEntity gm, final ServerEntity entity) {
		final MobLifecycle mobLifecycle = spawnService.getOwner(entity);
		if (mobLifecycle != null) {

			scheduler.add(new Action(timeProvider.now()) {
				@Override
				public void perform() {
					int before = 0;
					if (log.isDebugEnabled()) {
						before = entityService.getAllEntities(null).size();
					}
					mobLifecycle.removeEntity((Mob) entity);
					mobLifecycle.doSpawn();
					broadcaster.create().to(gm).send().gamemaster().successNotification("Respawned mob: " + entity.getName());
					if (log.isDebugEnabled()) {
						int after = entityService.getAllEntities(null).size();
						log.debug("Before {} and after {}", before, after);
					}
					log.warn("Mob {} reloaded by {}", entity, gm.getName());
				}
			});
		}
	}

	@Override
	@AutoTransaction
	public void spawnMob(ServerEntity gm, MobTemplate mobTemplate, BrainTemplate brainTemplate) {
		Mob mob = mobTemplate.createMob(timeProvider, uuidFactory.randomUUID(), randomProvider);
		PersistedPositionalData pos = gm.getPositionalData();
		MobBrain brain = brainTemplate.createBrain(mob,
				new SinglePointSpawnArea(pos.getPosition(), pos.getRotation()), new BrainParameterProviderAdapter());

		// Make the mob GM controllable
		brain = new GMPuppeteerBrain(brain, mobOrderExecutor);

		mob.setEntityInteractionCapabilities(brain.getInteractionCapabilities());

		mob.setPositionalData(new PersistedPositionalData(pos.getPosition(), pos.getRotation()));
		entityService.addEntity(mob, brain.getSmrtReceiver());

		spawnService.registerMob(gmMobLifecycle, mob, brain);
		log.warn("Mob {} spawned by {}", mob, gm.getName());
	}

	@Override
	@AutoTransaction
	public void reloadServerContent(ServerEntity gm) {
		serverContentPopulator.execute();
		broadcaster.create().to(gm).send().gamemaster().successNotification("ServerContent reload initiated");
		log.warn("Server content reloaded by {}", gm.getName());
	}

	@Override
	@AutoTransaction
	public void grantSpell(ServerEntity gm, ServerEntity player, ServerSpell spell) {
		spellService.addSpellForEntity(player, spell);
		broadcaster.create().to(player).send().spell().spellAdded(spell);
		broadcaster.create().to(gm).send().gamemaster().successNotification("Spell " + spell.getName() + " was added to " + player.getName());
		log.warn("{} was granted {} by", player, spell, gm.getName());

	}

	@Override
	@AutoTransaction
	public void sendAiInfo(ServerEntity gm, Mob mob) {
		MobOrder order = mobOrderExecutor.getOrder(mob);
		String message = "MobOrder for " + mob + " - " + order + " : " + mob.getBaseStats();
		broadcaster.create().to(gm).send().gamemaster().successNotification(message);
		if (order.getWalkTo() != null) {
			double distance = SpacedVector3.distance(order.getWalkTo(), mob.getPosition());
			broadcaster.create().to(gm).send().gamemaster().successNotification("Mob is " + distance + " away from current target");
		}

		String aggro = mob.getAggroManager().dumpAggroDebug();
		broadcaster.create().to(gm).send().gamemaster().successNotification("Mob aggro: " + aggro);
	}

	@Override
	@AutoTransaction
	public void giveMoney(ServerEntity gm, ServerEntity player, PersistedCurrency currency, long amount) {
		PersistedMoney money = new PersistedMoney(currency, amount);
		moneyService.awardMoney(player, money);
		broadcaster.create().to(gm).send().gamemaster().successNotification("Awarded " + player.getName() + " " + amount + " " + currency.getName());
		log.warn("{} was awarded {} by {}", player, money, gm.getName());
	}

	@Override
	public void summonEntity(ServerEntity gm, ServerEntity player) {
		movementService.teleportEntity(player, gm.getPosition(), gm.getRotation(),
				timeProvider.now());
		log.warn("{} was summoned by {}", player, gm.getName());
	}

	@Override
	public void forceException(ServerEntity gm, boolean includeActionLoop) {
		log.warn("Force Exception {} was invoked by {}", includeActionLoop, gm.getName());
		if (includeActionLoop) {
			scheduler.add(new ExceptionAction(timeProvider.now()));
		} else {
			throw new NullPointerException("forceExceptionServerSide");
		}
	}
}
