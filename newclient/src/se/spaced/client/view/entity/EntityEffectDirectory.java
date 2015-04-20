package se.spaced.client.view.entity;

import com.google.common.collect.Maps;
import se.spaced.client.ardor.effect.AsynchEffect;

import java.util.Map;

public class EntityEffectDirectory {
	private final Map<VisualEntity, Map<String, AsynchEffect>> directory = Maps.newHashMap();

	public void put(VisualEntity entity, String effectName, AsynchEffect effect) {
		Map<String, AsynchEffect> entityEffectMap = getEntityEffectMap(entity);
		entityEffectMap.put(effectName, effect);
	}

	public AsynchEffect findByEntity(VisualEntity entity, String effectName) {
		Map<String, AsynchEffect> entityEffectMap = getEntityEffectMap(entity);
		return entityEffectMap.get(effectName);
	}

	public AsynchEffect remove(VisualEntity entity, String effectName) {
		Map<String, AsynchEffect> entityEffectMap = getEntityEffectMap(entity);
		AsynchEffect effect = entityEffectMap.remove(effectName);

		if (entityEffectMap.isEmpty()) {
			directory.remove(entity);
		}

		return effect;
	}

	private Map<String, AsynchEffect> getEntityEffectMap(VisualEntity entity) {
		Map<String, AsynchEffect> stringEffectMap = directory.get(entity);

		if (stringEffectMap == null) {
			stringEffectMap = Maps.newHashMap();
			directory.put(entity, stringEffectMap);
		}

		return stringEffectMap;
	}
}
