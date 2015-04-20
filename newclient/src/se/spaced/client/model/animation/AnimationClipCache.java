package se.spaced.client.model.animation;

import com.ardor3d.extension.animation.skeletal.clip.AnimationClip;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.xmo.ColladaContentLoader;
import se.spaced.shared.model.xmo.ColladaContents;
import se.spaced.shared.util.cache.Cache;
import se.spaced.shared.util.cache.CacheLoader;
import se.spaced.shared.util.cache.CacheManager;
import se.spaced.shared.util.cache.impl.ThreadSafeCache;

import java.util.Map;

@Singleton
public class AnimationClipCache {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Cache<AnimationKey, AnimationClip> animationCache;

	@Inject
	public AnimationClipCache(
			@Named("xmoCachedManager") CacheManager xmoCacheManager,
			final ColladaContentLoader contentLoader,
			final AnimationMapper mapper) {

		animationCache = new ThreadSafeCache<AnimationKey, AnimationClip>(new CacheLoader<AnimationKey, AnimationClip>() {
			@Override
			public AnimationClip load(AnimationKey animationKey) {
				Map<AnimationState, String> map = mapper.getAnimationMap(animationKey.getName());
				AnimationState animationState = animationKey.getAnimationState();
				String fileName = map.get(animationState);
				if (fileName == null) {
					log.info("No animation file for  {} {}", animationKey.getName(), animationState);
					return null;
				}

				final ColladaContents storage = contentLoader.get(fileName);
				return storage.getAnimationClip(animationState.name());
			}
		});

		xmoCacheManager.addManagedCache(animationCache);
	}

	public AnimationClip getClip(AnimationState state, String pieceName) {
		return animationCache.get(new AnimationKey(pieceName, state));
	}

	private static class AnimationKey {
		private final String name;
		private final AnimationState animationState;

		AnimationKey(String name, AnimationState animationState) {
			this.name = name;
			this.animationState = animationState;
		}

		public String getName() {
			return name;
		}

		public AnimationState getAnimationState() {
			return animationState;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			AnimationKey that = (AnimationKey) o;

			if (animationState != that.animationState) {
				return false;
			}
			if (!name.equals(that.name)) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			int result = name.hashCode();
			result = 31 * result + animationState.hashCode();
			return result;
		}
	}
}
