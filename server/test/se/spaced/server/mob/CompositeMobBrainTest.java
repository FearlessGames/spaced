package se.spaced.server.mob;

import com.google.common.collect.Lists;
import org.junit.Test;
import se.mockachino.order.*;
import se.spaced.server.mob.brains.CompositeMobBrain;
import se.spaced.server.mob.brains.MobBrain;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class CompositeMobBrainTest {
	private static final long NOW = 42L;

	@Test
	public void act() {

		MobBrain mobBrainA = mock(MobBrain.class);
		MobBrain mobBrainB = mock(MobBrain.class);
		MobBrain mobBrainC = mock(MobBrain.class);
		Iterable<MobBrain> brains = Lists.newArrayList(mobBrainA, mobBrainB, mobBrainC);
		CompositeMobBrain brain = new CompositeMobBrain(brains);

		when(mobBrainA.act(anyLong())).thenReturn(MobDecision.UNDECIDED);
		when(mobBrainB.act(anyLong())).thenReturn(MobDecision.DECIDED);
		when(mobBrainC.act(anyLong())).thenReturn(MobDecision.DECIDED);

		brain.act(NOW);
		OrderingContext ordering = newOrdering();

		ordering.verify().on(mobBrainA).act(NOW);
		ordering.verify().on(mobBrainB).act(NOW);

		verifyNever().on(mobBrainC).act(NOW);
	}

	@Test
	public void actWhenNoBrainMakesUpItsMind() {

		MobBrain mobBrainA = mock(MobBrain.class);
		MobBrain mobBrainB = mock(MobBrain.class);
		MobBrain mobBrainC = mock(MobBrain.class);
		Iterable<MobBrain> brains = Lists.newArrayList(mobBrainA, mobBrainB, mobBrainC);
		CompositeMobBrain brain = new CompositeMobBrain(brains);


		when(mobBrainA.act(anyLong())).thenReturn(MobDecision.UNDECIDED);
		when(mobBrainB.act(anyLong())).thenReturn(MobDecision.UNDECIDED);
		when(mobBrainC.act(anyLong())).thenReturn(MobDecision.UNDECIDED);


		MobDecision decision = brain.act(NOW);
		assertEquals(MobDecision.UNDECIDED, decision);

		OrderingContext ordering = newOrdering();

		ordering.verify().on(mobBrainA).act(NOW);
		ordering.verify().on(mobBrainB).act(NOW);
		ordering.verify().on(mobBrainC).act(NOW);


	}


	@Test(expected = IllegalArgumentException.class)
	public void actWithoutBrains() {

		Iterable<MobBrain> brains = Lists.newArrayList();
		CompositeMobBrain brain = new CompositeMobBrain(brains);

	}
}
