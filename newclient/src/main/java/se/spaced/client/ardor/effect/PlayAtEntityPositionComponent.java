package se.spaced.client.ardor.effect;

import se.spaced.client.view.entity.VisualEntity;

public class PlayAtEntityPositionComponent<T extends SpatialEffect> implements EffectComponent<T> {
	private final VisualEntity entity;

	public static <T extends SpatialEffect> PlayAtEntityPositionComponent<T> create(VisualEntity entity) {
		return new PlayAtEntityPositionComponent<T>(entity);
	}

	private PlayAtEntityPositionComponent(VisualEntity entity) {
		this.entity = entity;
	}

	@Override
	public void onStart(T effect) {
		effect.setTranslation(entity.getNode().getTranslation());
	}

	@Override
	public void onStop(T effect) {
	}

	@Override
	public void onUpdate(double dt, T effect) {
		effect.setTranslation(entity.getNode().getTranslation());
	}
}