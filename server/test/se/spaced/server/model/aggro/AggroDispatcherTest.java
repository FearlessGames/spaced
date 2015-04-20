package se.spaced.server.model.aggro;

import org.junit.Before;
import org.junit.Test;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.shared.model.MagicSchool;

import java.util.Random;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class AggroDispatcherTest {
	private AggroDispatcher aggroDispatcher;
	private ServerEntity self;
	private AggroManager aggro;

	@Before
	public void setup() {
		self = mock(ServerEntity.class);
		aggro = spy(new SimpleAggroManager(1.1, new Random()));
		aggroDispatcher = new AggroDispatcher(self, aggro);
	}

	@Test
	public void testCombatStatusChanged() throws Exception {
		aggroDispatcher.combatStatusChanged(self, true);
		verifyNever().on(aggro).clearAll();

		getData(aggro).resetCalls();

		aggroDispatcher.combatStatusChanged(self, false);
		verifyOnce().on(aggro).clearAll();

	}

	@Test
	public void testEntityStartedSpellCast() throws Exception {
		ServerEntity entity = mock(ServerEntity.class);
		aggroDispatcher.entityStartedSpellCast(self, entity, mock(ServerSpell.class));
		aggroDispatcher.entityStartedSpellCast(entity, self, mock(ServerSpell.class));
		verifyNever().on(aggro).addHate(entity, anyInt());
	}

	@Test
	public void testEntityCompletedSpellCast() throws Exception {
		ServerEntity entity = mock(ServerEntity.class);
		aggroDispatcher.entityCompletedSpellCast(self, entity, mock(ServerSpell.class));
		aggroDispatcher.entityCompletedSpellCast(entity, self, mock(ServerSpell.class));
		verifyNever().on(aggro).addHate(entity, anyInt());

	}

	@Test
	public void testEntityStoppedSpellCast() throws Exception {
		ServerEntity entity = mock(ServerEntity.class);
		aggroDispatcher.entityStoppedSpellCast(self, mock(ServerSpell.class));
		aggroDispatcher.entityStoppedSpellCast(entity, mock(ServerSpell.class));
		verifyNever().on(aggro).addHate(entity, anyInt());
	}

	@Test
	public void testEntityWasKilled() throws Exception {
		ServerEntity target = mock(ServerEntity.class);
		aggroDispatcher.entityWasKilled(self, target);
		verifyOnce().on(aggro).clearHate(target);
	}


	@Test
	public void testSelfWasKilled() throws Exception {
		ServerEntity target = mock(ServerEntity.class);
		aggroDispatcher.entityWasKilled(target, self);
		verifyOnce().on(aggro).clearAll();
	}

	@Test
	public void testEntityDamaged() throws Exception {
		ServerEntity entity = mock(ServerEntity.class);
		aggroDispatcher.entityDamaged(entity, self, 10, 100, "foo", MagicSchool.FROST);
		verifyOnce().on(aggro).addHate(entity, 10);
	}

	@Test
	public void testEntityHealedTargetNotOnHatelist() throws Exception {
		ServerEntity entity = mock(ServerEntity.class);
		ServerEntity target = mock(ServerEntity.class);
		aggroDispatcher.entityHealed(entity, target, 10, 100, "foo", MagicSchool.LIGHT);
		verifyNever().on(aggro).addHate(entity, anyInt());
	}

	@Test
	public void testEntityHealedTargetOnHatelist() throws Exception {
		ServerEntity entity = mock(ServerEntity.class);
		ServerEntity target = mock(ServerEntity.class);
		aggro.addHate(target, 1);
		aggroDispatcher.entityHealed(entity, target, 10, 100, "foo", MagicSchool.LIGHT);
		verifyOnce().on(aggro).addHate(entity, 10);
	}
}
