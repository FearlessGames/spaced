package se.spaced.client.net.messagelisteners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.TimeProvider;
import se.spaced.client.ardor.ui.events.CombatGuiEvents;
import se.spaced.client.model.ClientAuraService;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.ClientSpell;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.cooldown.ClientCooldown;
import se.spaced.client.model.cooldown.ClientCooldownService;
import se.spaced.client.model.listener.AbilityModelListener;
import se.spaced.messages.protocol.AuraInstance;
import se.spaced.messages.protocol.ClientAuraInstance;
import se.spaced.messages.protocol.Cooldown;
import se.spaced.messages.protocol.CooldownData;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.Spell;
import se.spaced.messages.protocol.s2c.ServerCombatMessages;
import se.spaced.messages.protocol.s2c.ServerProjectileMessages;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.activecache.Job;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.util.ListenerDispatcher;

@Singleton
public class ServerCombatMessagesImpl implements ServerCombatMessages, ServerProjectileMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ListenerDispatcher<AbilityModelListener> dispatcher;
	private final ActiveCache<Entity, ClientEntity> entityCache;
	private final UserCharacter userCharacter;
	private final EventHandler eventHandler;
	private final EntityJobUtil entityJobUtil;
	private final ActiveCache<Spell, ClientSpell> spellCache;
	private final ClientCooldownService cooldownService;
	private final TimeProvider timeProvider;
	private final ClientAuraService auraService;

	@Inject
	public ServerCombatMessagesImpl(
			UserCharacter userCharacter,
			EventHandler eventHandler,
			ListenerDispatcher<AbilityModelListener> dispatcher,
			ActiveCache<Entity, ClientEntity> entityCache,
			ActiveCache<Spell, ClientSpell> spellCache,
			ClientCooldownService cooldownService, TimeProvider timeProvider, ClientAuraService auraService) {
		this.userCharacter = userCharacter;
		this.eventHandler = eventHandler;
		this.dispatcher = dispatcher;
		this.entityCache = entityCache;
		this.spellCache = spellCache;
		this.cooldownService = cooldownService;
		this.timeProvider = timeProvider;
		this.auraService = auraService;
		entityJobUtil = new EntityJobUtil(entityCache, this.spellCache);
	}

	@Override
	public void combatStatusChanged(Entity entity, final boolean isStart) {

		entityCache.runWhenReady(entity, new Job<ClientEntity>() {
			@Override
			public void run(ClientEntity value) {
				log.debug("combatStatusChanged {} {}", value, isStart);
				if (userCharacter.isUserControlledEntity(value)) {
					userCharacter.setInCombat(isStart);
				}
			}
		});
	}

	@Override
	public void entityStartedSpellCast(final Entity entity, final Entity target, final Spell spell) {
		entityJobUtil.run(entity, target, spell, new EntityEntitySpellJob() {
			@Override
			public void run(ClientEntity first, ClientEntity second, ClientSpell spell) {
				dispatcher.trigger().abilityStarted(first, second, spell);
			}
		});
	}

	@Override
	public void entityCompletedSpellCast(final Entity entity, final Entity target, final Spell spell) {
		entityJobUtil.run(entity, target, spell, new EntityEntitySpellJob() {
			@Override
			public void run(ClientEntity first, ClientEntity second, ClientSpell spell) {
				dispatcher.trigger().abilityCompleted(first, second, spell);
			}
		});
	}

	@Override
	public void entityStoppedSpellCast(final Entity entity, final Spell spell) {
		entityCache.runWhenReady(entity, new Job<ClientEntity>() {
			@Override
			public void run(final ClientEntity clientEntity) {
				spellCache.runWhenReady(spell, new Job<ClientSpell>() {
					@Override
					public void run(ClientSpell value) {
						dispatcher.trigger().abilityStopped(clientEntity, value);
					}
				});
			}
		});
	}

	@Override
	public void entityWasKilled(Entity attacker, Entity target) {
		entityJobUtil.run(attacker, target, new EntityEntityJob() {
			@Override
			public void run(ClientEntity first, ClientEntity second) {
				second.setAlive(false);
			}
		});
	}

	@Override
	public void entityDamaged(
			final Entity from,
			final Entity to,
			final int amount,
			final int newHealth,
			String source,
			final MagicSchool school) {
		entityJobUtil.run(from, to, new EntityEntityJob() {
			@Override
			public void run(ClientEntity first, ClientEntity second) {
				log.info("DAMAGED {} {} {}", second.getName(), amount, newHealth);
				second.getBaseStats().getCurrentHealth().changeValue(newHealth);
				eventHandler.fireEvent(CombatGuiEvents.UNIT_COMBAT,
						first,
						second,
						"WOUND",
						amount,
						school.name());
			}
		});
	}

	@Override
	public void entityHealed(
			final Entity first,
			final Entity to,
			final int amount,
			final int newHealth,
			String source,
			final MagicSchool school) {
		entityJobUtil.run(first, to, new EntityEntityJob() {
			@Override
			public void run(ClientEntity first, ClientEntity second) {
				second.getBaseStats().getCurrentHealth().changeValue(newHealth);
				eventHandler.fireEvent(CombatGuiEvents.UNIT_COMBAT,
						first,
						second,
						"HEAL",
						amount,
						school.name());
			}
		});

	}

	@Override
	public void entityHeatAffected(
			final Entity from, Entity to, final int amount, final int newHeat, String source, final MagicSchool school) {
		entityJobUtil.run(from, to, new EntityEntityJob() {
			@Override
			public void run(ClientEntity first, ClientEntity second) {
				second.getBaseStats().getHeat().setValue(newHeat);
				eventHandler.fireEvent(CombatGuiEvents.UNIT_COMBAT,
						first,
						second,
						"HEAT_CHANGE",
						amount,
						school.name());
			}
		});
	}

	@Override
	public void entityMissed(final Entity from, Entity to, String source, final MagicSchool school) {
		entityJobUtil.run(from, to, new EntityEntityJob() {
			@Override
			public void run(ClientEntity first, ClientEntity second) {
				eventHandler.fireEvent(CombatGuiEvents.UNIT_COMBAT,
						first,
						second,
						"MISS",
						0,
						school.name());
			}
		});
	}

	@Override
	public void effectApplied(final Entity from, final Entity on, final String resource) {
		entityJobUtil.run(from, on, new EntityEntityJob() {
			@Override
			public void run(ClientEntity first, ClientEntity second) {
				dispatcher.trigger().effectApplied(first, second, resource);
			}
		});
	}

	@Override
	public void gainedAura(Entity entity, final AuraInstance aura) {
		entityCache.runWhenReady(entity, new Job<ClientEntity>() {
			@Override
			public void run(ClientEntity value) {
				auraService.applyAura(value, (ClientAuraInstance) aura);
				if (aura.isVisible()) {
					eventHandler.fireAsynchEvent(CombatGuiEvents.ENTITY_GAINED_AURA, value, aura);
				}
				dispatcher.trigger().auraGained(value, aura);
			}
		});
	}

	@Override
	public void lostAura(Entity entity, final AuraInstance aura) {
		entityCache.runWhenReady(entity, new Job<ClientEntity>() {
			@Override
			public void run(ClientEntity value) {
				auraService.removeAura(value, (ClientAuraInstance) aura);
				if (aura.isVisible()) {
					eventHandler.fireAsynchEvent(CombatGuiEvents.ENTITY_LOST_AURA, value, aura);
				}
				dispatcher.trigger().auraLost(value, aura);
			}
		});
	}

	@Override
	public void cooldownConsumed(Cooldown cooldown) {
		cooldownService.runWhenReady(cooldown, new Job<ClientCooldown>() {
			@Override
			public void run(ClientCooldown value) {
				value.consume(timeProvider.now());
				eventHandler.fireAsynchEvent(CombatGuiEvents.COOLDOWN_CONSUMED, value);
			}
		});
	}

	@Override
	public void cooldownData(CooldownData cooldownData) {
		cooldownService.setValue(cooldownData, new ClientCooldown(cooldownData));
	}

	@Override
	public void entityAbsorbedDamaged(
			Entity attacker, Entity target, final int absorbedDamage, final int value, String causeName, final MagicSchool school) {
		entityJobUtil.run(attacker, target, new EntityEntityJob() {
			@Override
			public void run(ClientEntity first, ClientEntity second) {
				log.info("ABSORBED {} {} {}", second.getName(), absorbedDamage, value);
				second.getBaseStats().getShieldStrength().changeValue(value);
				eventHandler.fireEvent(CombatGuiEvents.UNIT_COMBAT,
						first,
						second,
						"ABSORB",
						absorbedDamage,
						school.name());
			}
		});

	}

	@Override
	public void entityRecovered(
			Entity performer, Entity target, final int amount, final int value, String causeName, final MagicSchool school) {
		entityJobUtil.run(performer, target, new EntityEntityJob() {
			@Override
			public void run(ClientEntity first, ClientEntity second) {
				second.getBaseStats().getShieldStrength().changeValue(value);
				eventHandler.fireEvent(CombatGuiEvents.UNIT_COMBAT,
						first,
						second,
						"RECOVER",
						amount,
						school.name());
			}
		});
	}

	@Override
	public void homingProjectileCreated(
			final int projectileId,
			final Entity performer,
			final Entity target,
			final String effectResource,
			final double speed) {
		entityJobUtil.run(performer, target, new EntityEntityJob() {
			@Override
			public void run(ClientEntity first, ClientEntity second) {
				dispatcher.trigger().homingProjectileCreated(projectileId,
						first,
						second,
						effectResource,
						speed);
			}
		});
	}

	@Override
	public void homingProjectileHit(int projectileId) {
	}
}
