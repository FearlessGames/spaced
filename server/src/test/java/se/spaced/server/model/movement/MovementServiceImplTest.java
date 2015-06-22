package se.spaced.server.model.movement;

import org.junit.Test;
import se.fearlessgames.common.mock.MockUtil;
import se.mockachino.matchers.*;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.spell.effect.RangeableEffect;

import static se.mockachino.Mockachino.*;

public class MovementServiceImplTest extends ScenarioTestBase {

	@Test
	public void testHitGround() throws Exception {
		PlayerMockFactory factory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		ServerEntity entity = factory.createPlayer("Bob");
		entityService.addEntity(entity, MockUtil.deepMock(S2CProtocol.class));

		verifyNever().on(spellCombatService).doDamage(Matchers.any(ServerEntity.class), entity, Matchers.eq(timeProvider.now()), Matchers.any(RangeableEffect.class), Matchers.any(String.class));
		movementService.hitGround(entity, 20);

		timeProvider.advanceTime(1);
		actionScheduler.tick(timeProvider.now());

		verifyOnce().on(spellCombatService).doDamage(entity, entity, Matchers.eq(0L), Matchers.any(RangeableEffect.class), Matchers.any(String.class));
		
	}
}
