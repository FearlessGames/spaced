package se.spaced.server.model.contribution;

import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.mock.MockUtil;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.uuid.UUIDMockFactory;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.combat.CombatRepositoryImpl;
import se.spaced.server.model.combat.CurrentActionService;
import se.spaced.server.model.combat.DeathService;
import se.spaced.server.model.combat.EntityCombatServiceImpl;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.shared.model.MagicSchool;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class ContributionMapperTest {

	private ContributionService contributionService;
	private ContributionMapper mapper;
	private ServerEntity entityA;
	private ServerEntity entityB;
	private ServerEntity entityC;
	private CombatRepositoryImpl combatRepository;
	private EntityCombatServiceImpl entityCombatService;
	private MockTimeProvider timeProvider;

	@Before
	public void setUp() throws Exception {
		timeProvider = new MockTimeProvider();
		contributionService = spy(new ContributionServiceImpl());
		combatRepository = new CombatRepositoryImpl();
		entityCombatService = new EntityCombatServiceImpl(mock(ActionScheduler.class),
				mock(CurrentActionService.class),
				MockUtil.deepMock(SmrtBroadcasterImpl.class),
				combatRepository,
				mock(DeathService.class));
		mapper = new ContributionMapper(contributionService, combatRepository);

		PlayerMockFactory factory = new PlayerMockFactory.Builder(timeProvider, new UUIDMockFactory()).build();
		entityA = createPlayer(factory, "EntityA");
		entityB = createPlayer(factory, "EntityB");
		entityC = createPlayer(factory, "EntityC");
	}

	private ServerEntity createPlayer(PlayerMockFactory factory, String name) {
		ServerEntity entity = factory.createPlayer(name);
		contributionService.entityAdded(entity);
		return entity;
	}

	@Test
	public void damageContributes() throws Exception {
		mapper.entityDamaged(entityA, entityB, 10, 40, "foo", MagicSchool.FIRE);
		verifyOnce().on(contributionService).addContribution(entityB, entityA, 10);
	}

	@Test
	public void absorbtionContributes() throws Exception {
		mapper.entityAbsorbedDamaged(entityA, entityB, 10, 40, "foo", MagicSchool.FIRE);
		verifyOnce().on(contributionService).addContribution(entityB, entityA, 10);
	}

	@Test
	public void healingContributesWhenTargetIsContributor() throws Exception {
		entityCombatService.enterCombat(entityA, entityC, timeProvider.now(), true);
		mapper.entityDamaged(entityA, entityC, 10, 40, "foo", MagicSchool.FIRE);
		entityCombatService.enterCombat(entityB, entityA, timeProvider.now(), false);
		mapper.entityHealed(entityB, entityA, 5, 35, "bar", MagicSchool.LIGHT);

		verifyOnce().on(contributionService).addContribution(entityC, entityB, 5);
	}

	@Test
	public void healingDoesNotContributesWhenTargetIsntContributor() throws Exception {
		entityCombatService.enterCombat(entityB, entityA, timeProvider.now(), false);
		mapper.entityHealed(entityB, entityA, 5, 35, "bar", MagicSchool.LIGHT);

		verifyNever().on(contributionService).addContribution(entityC, entityB, anyInt());
	}

	@Test
	public void recoverContributesWhenTargetIsContributor() throws Exception {
		entityCombatService.enterCombat(entityA, entityC, timeProvider.now(), true);
		mapper.entityDamaged(entityA, entityC, 10, 40, "foo", MagicSchool.FIRE);
		entityCombatService.enterCombat(entityB, entityA, timeProvider.now(), false);
		mapper.entityRecovered(entityB, entityA, 5, 35, "bar", MagicSchool.LIGHT);

		verifyOnce().on(contributionService).addContribution(entityC, entityB, 5);
	}

	@Test
	public void recoverDoesNotContributesWhenTargetIsntContributor() throws Exception {
		entityCombatService.enterCombat(entityB, entityA, timeProvider.now(), false);
		mapper.entityRecovered(entityB, entityA, 5, 35, "bar", MagicSchool.LIGHT);

		verifyNever().on(contributionService).addContribution(entityC, entityB, anyInt());
	}

	@Test
	public void coolContributesWhenTargetIsContributorAndAmountIsPositive() throws Exception {
		entityCombatService.enterCombat(entityA, entityC, timeProvider.now(), true);
		mapper.entityDamaged(entityA, entityC, 10, 40, "foo", MagicSchool.FIRE);
		entityCombatService.enterCombat(entityB, entityA, timeProvider.now(), false);
		mapper.entityHeatAffected(entityB, entityA, 5, 35, "bar", MagicSchool.LIGHT);

		verifyOnce().on(contributionService).addContribution(entityC, entityB, 5);
	}

	@Test
	public void coolDoesNotContributesWhenTargetIsContributorAndAmountIsNegative() throws Exception {
		entityCombatService.enterCombat(entityA, entityC, timeProvider.now(), true);
		mapper.entityDamaged(entityA, entityC, 10, 40, "foo", MagicSchool.FIRE);
		entityCombatService.enterCombat(entityB, entityA, timeProvider.now(), false);
		mapper.entityHeatAffected(entityB, entityA, -5, 35, "bar", MagicSchool.LIGHT);

		verifyNever().on(contributionService).addContribution(entityC, entityB, anyInt());
	}


	@Test
	public void coolDoesNotContributesWhenTargetIsntContributor() throws Exception {
		entityCombatService.enterCombat(entityB, entityA, timeProvider.now(), false);
		mapper.entityHeatAffected(entityB, entityA, 5, 35, "bar", MagicSchool.LIGHT);
		mapper.entityHeatAffected(entityB, entityA, -5, 35, "bar", MagicSchool.LIGHT);

		verifyNever().on(contributionService).addContribution(entityC, entityB, anyInt());
	}



}
