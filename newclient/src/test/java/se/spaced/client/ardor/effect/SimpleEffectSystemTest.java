package se.spaced.client.ardor.effect;

import com.ardor3d.util.ReadOnlyTimer;
import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.lifetime.LifetimeManager;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;


public class SimpleEffectSystemTest {
	private EffectLoader effectLoader;
	private Effect effectA;
	private Effect effectB;
	private ReadOnlyTimer timer;
	private EffectSystem effectSystem;

	@Before
	public void setUp() {
		effectLoader = mock(EffectLoader.class);
		timer = mock(ReadOnlyTimer.class);
		effectA = mock(Effect.class);
		effectB = mock(Effect.class);
		effectSystem = new SimpleEffectSystem(effectLoader, mock(LifetimeManager.class));
	}

	@Test
	public void shouldStartEffect() {
		stubReturn(0.0).on(timer).getTimePerFrame();
		effectSystem.startEffect(effectA);
		effectSystem.update(timer.getTimePerFrame());

		verifyOnce().on(effectA).start();
		verifyOnce().on(effectA).update(0);
	}

	@Test
	public void shouldLoadCorrectEffect() throws ExecutionException, InterruptedException {
		EffectContext effectContext = mock(EffectContext.class);
		stubReturn(effectA).on(effectLoader).loadEffect("someEffect", effectContext);

		Future<Effect> effect = effectSystem.startEffect("someEffect", effectContext);

		verifyOnce().withTimeout(100).on(effectA).start();
		assertEquals(effectA, effect.get());
	}

	@Test
	public void shouldUpdateStartedEffects() {
		stubReturn(1.0).on(timer).getTimePerFrame();
		effectSystem.startEffect(effectA);
		effectSystem.startEffect(effectB);
		effectSystem.update(timer.getTimePerFrame());

		verifyOnce().on(effectA).update(1);
		verifyOnce().on(effectB).update(1);
	}
}
