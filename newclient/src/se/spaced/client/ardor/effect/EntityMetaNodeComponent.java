package se.spaced.client.ardor.effect;

import se.spaced.client.view.entity.VisualEntity;

public class EntityMetaNodeComponent<T extends SpatialEffect> implements EffectComponent<T> {
	private final VisualEntity entity;
	private final String metaNodeName;

	public EntityMetaNodeComponent(VisualEntity entity, String metaNodeName) {
		this.entity = entity;
		this.metaNodeName = metaNodeName;
	}

	@Override
	public void onStart(final T effect) {
		effect.setTranslation(entity.getMetaNodePosition(metaNodeName));
	}

	@Override
	public void onStop(final T effect) {
	}

	@Override
	public void onUpdate(final double dt, final T effect) {
	}
}
