package se.spaced.server.model.combat;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.mock.MockUtil;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.Player;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.spawn.area.SinglePointSpawnArea;
import se.spaced.server.persistence.dao.impl.hibernate.GraveyardTemplate;
import se.spaced.shared.model.stats.EntityStats;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class DeathServiceImplTest extends ScenarioTestBase {

	private static final double EPSILON = 1e-10;
	private Player player;

	@Before
	public void setUp() throws Exception {
		PlayerMockFactory playerMockFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		player = playerMockFactory.createPlayer("Foo");
		when(graveyardService.getClosestGraveyard(any(SpacedVector3.class))).thenReturn(new GraveyardTemplate(uuidFactory.randomUUID(), "GY",
				new SinglePointSpawnArea(new SpacedVector3(10, 20, 30), new SpacedRotation(1, 0, 0, 0))));
	}

	@Test
	public void killRemovesOocRegen() throws Exception {
		entityService.addEntity(player, MockUtil.deepMock(S2CProtocol.class));
		deathService.kill(player);

		EntityStats baseStats = player.getBaseStats();
		assertEquals(0.0, baseStats.getShieldRecoveryRate().getValue(), EPSILON);
		assertEquals(0.0, baseStats.getHealthRegenRate().getValue(), EPSILON);
	}

	@Test
	public void reviveRestoresOocRegen() throws Exception {
		entityService.addEntity(player, MockUtil.deepMock(S2CProtocol.class));
		EntityStats baseStats = player.getBaseStats();

		double healthRegen = baseStats.getHealthRegenRate().getValue();
		double shieldRegen = baseStats.getShieldRecoveryRate().getValue();

		deathService.kill(player);
		deathService.respawn(player);

		assertEquals(shieldRegen, baseStats.getShieldRecoveryRate().getValue(), EPSILON);
		assertEquals(healthRegen, baseStats.getHealthRegenRate().getValue(), EPSILON);

	}
}
