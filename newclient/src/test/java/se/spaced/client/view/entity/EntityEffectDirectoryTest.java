package se.spaced.client.view.entity;

import org.junit.Before;
import org.junit.Test;
import se.spaced.client.ardor.effect.AsynchEffect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static se.mockachino.Mockachino.*;

public class EntityEffectDirectoryTest {
	private EntityEffectDirectory entityEffectDirectory;

	@Before
	public void setUp() throws Exception {
		entityEffectDirectory = new EntityEffectDirectory();
	}

	@Test
	public void testFindByEntity() throws Exception {
		VisualEntity entity = mock(VisualEntity.class);
		AsynchEffect effect = mock(AsynchEffect.class);
		String effectName = "effectName.effect";
				
		entityEffectDirectory.put(entity, effectName, effect);

		AsynchEffect foundEffect = entityEffectDirectory.findByEntity(entity, effectName);

		assertEquals(effect, foundEffect);
	}

	@Test
	public void removesEffect() throws Exception {
		VisualEntity entity = mock(VisualEntity.class);
		AsynchEffect effect = mock(AsynchEffect.class);
		String effectName = "effectName.effect";

		entityEffectDirectory.put(entity, effectName, effect);

		AsynchEffect removedEffect = entityEffectDirectory.remove(entity, effectName);
		AsynchEffect notFoundEffect = entityEffectDirectory.findByEntity(entity, effectName);

		assertEquals(effect, removedEffect);
		assertNull(notFoundEffect);		
	}
}
