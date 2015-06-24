package se.spaced.client.ardor.effect;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearless.common.lifetime.ExecutorServiceLifetimeAdapter;
import se.fearless.common.lifetime.LifetimeManager;

import java.util.Collection;
import java.util.concurrent.*;

@Singleton
public class SimpleEffectSystem implements EffectSystem, EffectNode {
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	private final EffectLoader effectLoader;
	private final Collection<Effect> childEffects = new CopyOnWriteArrayList<Effect>();

	private final Callback<Effect> startCallback = new Callback<Effect>() {
		@Override
		public void onReady(Effect result) {
			startEffect(result);
		}
	};

	@Inject
	public SimpleEffectSystem(final EffectLoader effectLoader, LifetimeManager lifetimeManager) {
		this.effectLoader = effectLoader;
		lifetimeManager.addListener(new ExecutorServiceLifetimeAdapter(executorService));
	}

	@Override
	public void startEffect(final Effect effect) {
		childEffects.add(effect);
		effect.setParent(this);
		effect.start();
	}

	@Override
	public Future<Effect> startEffect(final String effectName, final EffectContext context) {
		return loadEffect(effectName, context, startCallback);
	}

	@Override
	public Future<Effect> loadEffect(
			final String effectName, final EffectContext context, final Callback<Effect> callback) {
		return executorService.submit(new Callable<Effect>() {
			@Override
			public Effect call() throws Exception {
				Effect result = effectLoader.loadEffect(effectName, context);
				if (callback != null) {
					try {
						callback.onReady(result);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return result;
			}
		});
	}

	@Override
	public void update(double timePerFrame) {
		for (Effect effect : childEffects) {
			// TODO: Update all Effects to take a ReadOnlyTimer instead of getting dt here
			effect.update(timePerFrame);
		}
	}

	@Override
	public void setParent(final EffectNode effectNode) {
	}

	@Override
	public void detachChild(final EffectNode effectNode) {
		childEffects.remove(effectNode);
	}
}
