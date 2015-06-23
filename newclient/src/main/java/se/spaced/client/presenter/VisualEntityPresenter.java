package se.spaced.client.presenter;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.ardor.effect.EffectContext;
import se.spaced.client.ardor.effect.EffectSystem;
import se.spaced.client.ardor.ui.events.CombatGuiEvents;
import se.spaced.client.ardor.ui.events.EntityEvents;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.Relation;
import se.spaced.client.model.RelationResolver;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.listener.ClientEntityListener;
import se.spaced.client.model.listener.EquipmentListener;
import se.spaced.client.view.entity.EntityView;
import se.spaced.client.view.entity.VisualEntities;
import se.spaced.client.view.entity.VisualEntity;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.activecache.CacheUpdateListener;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.xmo.AttachmentPointIdentifier;
import se.spaced.shared.model.xmo.XmoEntity;
import se.spaced.shared.model.xmo.XmoEntityFactory;
import se.spaced.shared.xml.XmlIOException;

@Singleton
public class VisualEntityPresenter implements ClientEntityListener, EquipmentListener, CacheUpdateListener<Entity, ClientEntity> {
	private final EntityView entityView;
	private final EventHandler luaEventHandler;
	private final UserCharacter userCharacter;
	private final EffectSystem effectSystem;
	private final XmoEntityFactory xmoEntityFactory;
	private final RelationResolver relationResolver;
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	public VisualEntityPresenter(
			EntityView entityView,
			EventHandler luaEventHandler,
			UserCharacter userCharacter,
			EffectSystem effectSystem,
			XmoEntityFactory xmoEntityFactory,
			ActiveCache<Entity, ClientEntity> entityCache,
			RelationResolver relationResolver) {
		this.entityView = entityView;
		this.luaEventHandler = luaEventHandler;
		this.userCharacter = userCharacter;
		this.effectSystem = effectSystem;
		this.xmoEntityFactory = xmoEntityFactory;
		this.relationResolver = relationResolver;
		entityCache.addListener(this);
	}

	@Override
	public void appearanceDataUpdated(final ClientEntity entity) {
	}

	@Override
	public void statsUpdated(final ClientEntity clientEntity) {
		// TODO: this does not really belong here. Should there be some sort of client logic that dispatches to the gui
		luaEventHandler.fireAsynchEvent(EntityEvents.UNIT_STATS_UPDATED, clientEntity);
	}

	@Override
	public void died(final ClientEntity clientEntity) {
		entityView.setEntityAlive(clientEntity.getPk(), clientEntity.isAlive());

		if (userCharacter.isUserControlledEntity(clientEntity)) {
			luaEventHandler.fireEvent(CombatGuiEvents.PLAYER_DIED);
		} else {
			luaEventHandler.fireEvent(CombatGuiEvents.UNIT_DIED, clientEntity);
		}

		EffectContext context = new EffectContext.Builder().target(entityView.getEntity(clientEntity.getPk())).build();
		effectSystem.startEffect("death", context);
	}

	@Override
	public void respawned(final ClientEntity clientEntity) {
		entityView.setEntityAlive(clientEntity.getPk(), clientEntity.isAlive());
	}

	@Override
	public void animationStateChanged(ClientEntity spacedEntity, AnimationState animationState) {
		entityView.getEntity(spacedEntity.getPk()).play(animationState);
	}

	@Override
	public void positionalDataChanged(ClientEntity clientEntity) {
		entityView.getEntity(clientEntity.getPk()).setPositionData(clientEntity.getPosition(), clientEntity.getRotation());
	}

	@Override
	public void itemEquipped(ClientEntity clientEntity, ItemTemplateData clientItem, ContainerType container) {
		VisualEntity visualEntity = entityView.getEntity(clientEntity.getPk());
		String xmoFile = clientItem.getAppearanceData().getModelName();

		AttachmentPointIdentifier where = container.getAttachmentPoint();
		if (xmoFile.isEmpty()) {
			return;
		}
		try {
			XmoEntity xmoEntity = xmoEntityFactory.create(xmoFile, where.name());

			if (where != AttachmentPointIdentifier.VEHICLE) {
				xmoEntity.setSkinToSkinnedMeshesUnderNode(visualEntity.getSkin(),
						xmoEntity.getModel(),
						xmoEntityFactory.getSkinningShader());
			}

			if (visualEntity.hasEquipped(where)) {
				visualEntity.unequip(where);
			}
			visualEntity.equip(xmoEntity, where);
		} catch (XmlIOException e) {
			log.warn("Could not load requested equpiment xmo {}. {}", xmoFile, e.getCause());
		}
	}

	@Override
	public void itemUnequipped(ClientEntity clientEntity, ContainerType container) {
		VisualEntity visualEntity = entityView.getEntity(clientEntity.getPk());
		final AttachmentPointIdentifier point = container.getAttachmentPoint();
		if (visualEntity.hasEquipped(point)) {
			visualEntity.unequip(point);
		}
	}

	@Override
	public void updatedValue(Entity key, ClientEntity oldValue, ClientEntity value) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StringBuilder builder = new StringBuilder();
		for (StackTraceElement element : stackTrace) {
			builder.append(element);
			builder.append("\n");
		}
		log.error("This shouldn't happen - we must have missed an entityDisappeared : " + key.getPk() + " " + builder.toString());
		deletedValue(key, oldValue);
		addedValue(key, value);
	}

	@Override
	public void deletedValue(Entity key, ClientEntity oldValue) {
		entityView.removeEntity(oldValue.getPk());
	}

	@Override
	public void addedValue(Entity key, ClientEntity value) {
		VisualEntity visualEntity = entityView.getEntity(value.getPk());
		if (visualEntity != VisualEntities.EMPTY_ENTITY) {
			return;
		}

		Relation relation;
		if (userCharacter.getUserControlledEntity() == null) {
			relation = Relation.FRIENDLY;
		} else {
			relation = relationResolver.resolveRelation(value, userCharacter.getUserControlledEntity());
		}
		entityView.addEntity(value, relation);
	}
}
