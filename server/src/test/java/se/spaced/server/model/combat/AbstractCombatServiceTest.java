package se.spaced.server.model.combat;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.spawn.MobTemplate;


public class AbstractCombatServiceTest extends ScenarioTestBase {
	protected Player attacker;
	protected ServerEntity target;
	protected ServerEntity target2;

	protected S2CProtocol attackerReceiver;
	protected S2CProtocol target1Receiver;
	protected S2CProtocol target2Receiver;
	protected PersistedCreatureType creatureType;

	@Before
	public void setup() {
		attackerReceiver = MockUtil.deepMock(S2CProtocol.class);
		target1Receiver = MockUtil.deepMock(S2CProtocol.class);
		target2Receiver = MockUtil.deepMock(S2CProtocol.class);

		creatureType = new PersistedCreatureType(uuidFactory.combUUID(), "humanoid");
		PlayerMockFactory factory = new PlayerMockFactory.Builder(timeProvider,
				uuidFactory).creatureType(creatureType).build();
		attacker = factory.createPlayer("bob");
		entityService.addEntity(attacker, attackerReceiver);
		MobTemplate mobTemplate = new MobTemplate.Builder(uuidFactory.randomUUID(), "grunt").stamina(10).build();
		target = mobTemplate.createMob(timeProvider, uuidFactory.randomUUID(), randomProvider);

		entityService.addEntity(target, target1Receiver);
		target2 = mobTemplate.createMob(timeProvider, uuidFactory.randomUUID(), randomProvider);
		entityService.addEntity(target2, target2Receiver);
	}

	@Test
	public void dummyTest() {
		//ant sucks!
	}
}
