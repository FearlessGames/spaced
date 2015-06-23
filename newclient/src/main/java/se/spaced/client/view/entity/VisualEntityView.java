package se.spaced.client.view.entity;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.scenegraph.Node;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.TimeProvider;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.client.ardor.entity.EntityIndicator;
import se.spaced.client.ardor.entity.VisualEntityFactory;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.Relation;
import se.spaced.client.model.player.TargetInfo;
import se.spaced.messages.protocol.Entity;
import se.spaced.shared.activecache.ActiveCache;

import java.util.Map;

@Singleton
public class VisualEntityView implements EntityView {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Map<UUID, VisualEntity> entities = Maps.newHashMap();
	private final ActiveCache<Entity, ClientEntity> activeCache;
	private final VisualEntityFactory visualEntityFactory;
	private final Node entityRootNode;
	private final TimeProvider timeProvider;
	private final EntityIndicator targetIndicator;
	private final EntityIndicator hoverIndicator;

	private VisualEntity lastTarget = VisualEntities.EMPTY_ENTITY;
	private VisualEntity lastHover = VisualEntities.EMPTY_ENTITY;

	@Inject
	public VisualEntityView(
			@Named("entityNode") Node entityRootNode,
			TimeProvider timeProvider,
			VisualEntityFactory visualEntityFactory,
			@Named("targetIndicator") EntityIndicator targetIndicator,
			ActiveCache<Entity, ClientEntity> activeCache,
			@Named("hoverIndicator") EntityIndicator hoverIndicator) {
		this.entityRootNode = entityRootNode;
		this.timeProvider = timeProvider;
		this.visualEntityFactory = visualEntityFactory;
		this.targetIndicator = targetIndicator;
		this.activeCache = activeCache;
		this.hoverIndicator = hoverIndicator;
	}

	@Override
	public void addEntity(ClientEntity entity, Relation relation) {
		VisualEntity visualEntity = visualEntityFactory.create(entity, relation);
		visualEntity.setParent(entityRootNode);
		entities.put(entity.getPk(), visualEntity);
	}

	@Override
	public VisualEntity removeEntity(final UUID uuid) {
		VisualEntity visualEntity = entities.remove(uuid);
		if (visualEntity != null) {
			visualEntity.removeFromParent();
		}
		return visualEntity;
	}

	@Override
	public VisualEntity getEntity(final UUID uuid) {
		if (entities.containsKey(uuid)) {
			return entities.get(uuid);
		}
		logger.info("Could not find visualEntity with id {}", uuid);
		return VisualEntities.EMPTY_ENTITY;
	}

	@Override
	public void setTargetedEntity(final TargetInfo targetInfo) {
		lastTarget.setTargeted(false);
		lastTarget = getEntity(targetInfo.getUuid());
		lastTarget.setTargeted(true);

		targetIndicator.show(lastTarget, entityRootNode, targetColorLookup(targetInfo));
	}

	@Override
	public void clearTargetedEntity() {
		lastTarget.setTargeted(false);
		lastTarget = VisualEntities.EMPTY_ENTITY;
		targetIndicator.hide();
	}

	@Override
	public void setEntityAlive(final UUID uuid, final boolean alive) {
		VisualEntity entity = getEntity(uuid);
		entity.setAlive(alive);
	}

	@Override
	public void setHoveredEntity(final TargetInfo targetInfo) {
		lastHover = getEntity(targetInfo.getUuid());
		hoverIndicator.show(lastHover, entityRootNode, targetColorLookup(targetInfo));
	}

	@Override
	public void clearHoveredEntity() {
		hoverIndicator.hide();
		lastHover = VisualEntities.EMPTY_ENTITY;
	}

	@Override
	public void reset() {
		clearHoveredEntity();
		clearTargetedEntity();
		activeCache.clear();
	}

	@Override
	public void update(double timePerFrame) {
		for (VisualEntity visualEntity : entities.values()) {
			visualEntity.update(timeProvider.now());
		}

		if (lastTarget != VisualEntities.EMPTY_ENTITY) {
			targetIndicator.update(timePerFrame);
		}

		if (lastHover != VisualEntities.EMPTY_ENTITY) {
			hoverIndicator.update(timePerFrame);
		}
	}

	private ReadOnlyColorRGBA targetColorLookup(final TargetInfo targetInfo) {
		if (!targetInfo.isAlive()) {
			return ColorRGBA.GRAY;
		}

		return targetInfo.getRelation() == Relation.HOSTILE ? ColorRGBA.RED : ColorRGBA.GREEN;
	}
}
