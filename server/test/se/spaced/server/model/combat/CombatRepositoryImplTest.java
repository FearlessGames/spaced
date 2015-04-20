package se.spaced.server.model.combat;

import org.junit.Before;
import org.junit.Test;
import se.spaced.server.model.ServerEntity;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;

public class CombatRepositoryImplTest {
	private CombatRepository combatRepository;
	private ServerEntity entity1;
	private ServerEntity entity2;

	@Before
	public void setup() {
		combatRepository = new CombatRepositoryImpl();
		entity1 = mock(ServerEntity.class);
		entity2 = mock(ServerEntity.class);
	}

	@Test
	public void addCombat() {
		Combat combat = mock(Combat.class);
		combatRepository.add(entity1, combat);

		assertEquals(combat, combatRepository.getCombat(entity1));
		assertEquals(1, combatRepository.numberOfCombat());
	}

	@Test
	public void removeCombat() {
		Combat combat = mock(Combat.class);
		combatRepository.add(entity1, combat);

		combatRepository.remove(entity1);
		assertEquals(null, combatRepository.getCombat(entity1));
		assertEquals(0, combatRepository.numberOfCombat());
	}


	@Test
	public void addSomeRemoveSome() {
		Combat combat = mock(Combat.class);
		combatRepository.add(entity1, combat);
		combatRepository.add(entity2, combat);

		combatRepository.remove(entity1);
		assertEquals(null, combatRepository.getCombat(entity1));
		assertEquals(1, combatRepository.numberOfCombat());

		combatRepository.remove(entity2);
		assertEquals(null, combatRepository.getCombat(entity2));
		assertEquals(0, combatRepository.numberOfCombat());
	}

	
}
