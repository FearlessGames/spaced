package se.spaced.client.model.animation;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.util.cache.Cache;
import se.spaced.shared.util.cache.CacheLoader;
import se.spaced.shared.util.cache.CacheManager;
import se.spaced.shared.util.cache.impl.ThreadSafeCache;
import se.spaced.shared.xml.XmlIO;
import se.spaced.shared.xml.XmlIOException;

import java.util.Map;

public class AnimationMapper {
	private final Cache<String, Map<AnimationState, String>> animationMappingCache;

	@Inject
	public AnimationMapper(final XmlIO xmlIO, @Named("xmoCachedManager") CacheManager xmoCacheManager) {
		animationMappingCache = new ThreadSafeCache<String, Map<AnimationState, String>>(new CacheLoader<String, Map<AnimationState, String>>() {
			@Override
			public Map<AnimationState, String> load(String mappingName) {
				try {
					return xmlIO.load(AnimationMapping.class, mappingName).getStateToFilenameMap();
				} catch (XmlIOException e) {
					throw new RuntimeException("Couldn't read animation mapping file " + mappingName, e);
				}
			}
		});

		xmoCacheManager.addManagedCache(animationMappingCache);
	}

	public Map<AnimationState, String> getAnimationMap(String mappingName) {
		return animationMappingCache.get(mappingName);
	}
}
