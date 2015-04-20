package se.spaced.client.presenter;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.client.ardor.entity.AuraVisualiser;
import se.spaced.client.ardor.entity.AuraVisualiserEvent;
import se.spaced.client.ardor.ui.events.SpellEvents;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.ClientSpell;
import se.spaced.client.model.listener.AbilityModelListener;
import se.spaced.client.view.AbilityView;
import se.spaced.client.view.entity.EntityView;
import se.spaced.client.view.entity.VisualEntity;
import se.spaced.messages.protocol.AuraTemplate;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.model.AnimationState;

@Singleton
public class AbilityPresenter implements AbilityModelListener {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final AbilityView abilityView;
	private final EventHandler luaEventHandler;
	private final EntityView entityView;
	private final AuraVisualiser auraVisualiser;

	private static final UUID FORTITUDE = UUID.fromString("55fecd2a-ec26-40f7-9880-9d9f39873cd6");

	@Inject
	public AbilityPresenter(
			AbilityView abilityView, EventHandler luaEventHandler, EntityView entityView, AuraVisualiser auraVisualiser) {
		this.abilityView = abilityView;
		this.luaEventHandler = luaEventHandler;
		this.entityView = entityView;
		this.auraVisualiser = auraVisualiser;
	}

	@Override
	public void abilityCompleted(final ClientEntity source, final ClientEntity target, final ClientSpell spell) {
		luaEventHandler.fireEvent(SpellEvents.SPELLCAST_COMPLETED, source, spell);
		entityView.getEntity(source.getPk()).play(AnimationState.IDLE);
	}

	@Override
	public void abilityStarted(final ClientEntity source, final ClientEntity target, final ClientSpell spell) {
		luaEventHandler.fireEvent(SpellEvents.SPELLCAST_STARTED,
				source,
				target,
				spell);
		abilityView.startAbilityCharge(source, target, spell);

		entityView.getEntity(source.getPk()).play(AnimationState.COMBAT_DEFAULT_ATTACK);
	}

	@Override
	public void abilityStopped(final ClientEntity source, final ClientSpell spell) {
		luaEventHandler.fireEvent(SpellEvents.SPELLCAST_STOPPED, source, spell);
		abilityView.stopAbilityCharge(source, spell);
		entityView.getEntity(source.getPk()).play(AnimationState.IDLE);
	}

	@Override
	public void homingProjectileCreated(
			int projectileId, ClientEntity performer, ClientEntity target, String effectResource, double speed) {
		abilityView.startAbilityProjectile(projectileId, performer, target, effectResource, speed);
	}

	@Override
	public void effectApplied(ClientEntity from, ClientEntity to, String resource) {
		log.info("Got effectApplied {}", resource);
		if (!resource.equals("")) {
			abilityView.startEffectApplied(from, to, resource);
		}
	}

	@Override
	public void auraGained(ClientEntity entity, AuraTemplate aura) {
		if (!aura.getPk().equals(FORTITUDE)) {
			return;
		}
		final VisualEntity visualEntity = entityView.getEntity(entity.getPk());
		auraVisualiser.fireEvent(AuraVisualiserEvent.FORTITUDE_GAINED, visualEntity);
	}

	@Override
	public void auraLost(ClientEntity entity, AuraTemplate aura) {
		if (!aura.getPk().equals(FORTITUDE)) {
			return;
		}
		final VisualEntity visualEntity = entityView.getEntity(entity.getPk());
		auraVisualiser.fireEvent(AuraVisualiserEvent.FORTITUDE_LOST, visualEntity);
	}
}
