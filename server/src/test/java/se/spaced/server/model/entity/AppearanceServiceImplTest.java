package se.spaced.server.model.entity;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.uuid.UUIDMockFactory;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.Player;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.aura.AuraService;
import se.spaced.server.model.aura.AuraServiceImpl;
import se.spaced.server.model.aura.AuraUpdateListener;
import se.spaced.server.model.combat.EntityCombatService;
import se.spaced.server.model.combat.EntityTargetService;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryEquipmentDao;
import se.spaced.shared.util.ListenerDispatcher;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.anyBoolean;

public class AppearanceServiceImplTest {

	private AppearanceService appearanceService;
	private EntityCombatService combatService;

	@Before
	public void setUp() throws Exception {
		EntityService entityService = mock(EntityService.class);
		combatService = mock(EntityCombatService.class);
		AuraService auraService = new AuraServiceImpl(mock(ActionScheduler.class), ListenerDispatcher.create(AuraUpdateListener.class));
		appearanceService = new AppearanceServiceImpl(entityService, new InMemoryEquipmentDao(), mock(EntityTargetService.class), auraService, combatService);
	}

	@Test
	public void appearedInCombatTriggersCombatStatusUpdate() throws Exception {

		S2CProtocol receiver = MockUtil.deepMock(S2CProtocol.class);
		PlayerMockFactory factory = new PlayerMockFactory.Builder(new MockTimeProvider(), new UUIDMockFactory()).build();
		Player player = factory.createPlayer("Foo");

		when(combatService.isInCombat(player)).thenReturn(true);

		appearanceService.notifyAppeared(receiver, player);

		verifyOnce().on(receiver.combat()).combatStatusChanged(player, true);
	}

	@Test
	public void appearedNotInCombatDoenstTriggerCombatStatusUpdate() throws Exception {

		S2CProtocol receiver = MockUtil.deepMock(S2CProtocol.class);
		PlayerMockFactory factory = new PlayerMockFactory.Builder(new MockTimeProvider(), new UUIDMockFactory()).build();
		Player player = factory.createPlayer("Foo");

		when(combatService.isInCombat(player)).thenReturn(false);

		appearanceService.notifyAppeared(receiver, player);

		verifyNever().on(receiver.combat()).combatStatusChanged(player, anyBoolean());
	}
}
