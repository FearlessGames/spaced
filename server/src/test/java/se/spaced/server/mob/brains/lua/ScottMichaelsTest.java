package se.spaced.server.mob.brains.lua;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.io.ClasspathIOLocator;
import se.fearless.common.io.IOLocator;
import se.fearless.common.lua.SimpleLuaSourceProvider;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.krka.kahlua.require.LuaSourceProvider;
import se.mockachino.Invocation;
import se.mockachino.alias.SimpleAlias;
import se.mockachino.annotations.Mock;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.mob.MobInfoProvider;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.mob.brains.MobScriptEnvironment;
import se.spaced.server.mob.brains.NullBrain;
import se.spaced.server.mob.brains.templates.ScriptedBrainTemplate;
import se.spaced.server.model.Mob;
import se.spaced.server.model.Player;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.combat.CombatRepository;
import se.spaced.server.model.combat.CurrentActionService;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.model.entity.AppearanceService;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.server.model.entity.VisibilityServiceImpl;
import se.spaced.server.model.movement.MovementServiceImpl;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.model.spawn.BrainParameterProviderAdapter;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.server.player.PlayerService;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.playback.MovementPoint;
import se.spaced.shared.util.ListenerDispatcher;
import se.spaced.shared.util.math.interval.IntervalInt;
import se.spaced.shared.util.random.RandomProvider;
import se.spaced.shared.world.area.PolygonGraph;

import java.util.Arrays;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;
import static se.mockachino.matchers.Matchers.anyDouble;

public class ScottMichaelsTest {
	private final UUIDFactory uuidFactory = UUIDFactoryImpl.INSTANCE;

	private final MockTimeProvider timeProvider = new MockTimeProvider();
	private MobBrain brain;
	private MobOrderExecutor mobOrderExecutor;
	private Mob mob;
	private Player player;
	private MobInfoProvider mobInfoProvider;
	private MovementServiceImpl movementService;
	private SmrtBroadcaster<S2CProtocol> broadcaster;
	private SpellCombatService spellCombatService;
	private ServerSpell rocket;
	private ServerSpell melee;
	@Mock
	private RandomProvider randomProvider;

	@Before
	public void setup() {
		setupMocks(this);
		// Init mocks
		VisibilityService visibilityService = new VisibilityServiceImpl(
				mock(EntityService.class),
				ListenerDispatcher.create(AppearanceService.class));
		broadcaster = new SmrtBroadcasterImpl(mock(EntityService.class), mock(CombatRepository.class), visibilityService);
		spellCombatService = mock(SpellCombatService.class);
		movementService = new MovementServiceImpl(broadcaster,
				timeProvider,
				mock(ActionScheduler.class),
				spellCombatService,
				visibilityService,
				mock(PlayerService.class),
				mock(CurrentActionService.class)
		);
		mobOrderExecutor = spy(new MobOrderExecutor(movementService, timeProvider, broadcaster,
				spellCombatService, new PolygonGraph()));
		mobInfoProvider = new MobInfoProvider();

		// Set up stubs
		rocket = mock(ServerSpell.class);
		when(rocket.getName()).thenReturn("Pocket rocket");
		when(rocket.getCastTime()).thenReturn(1500);
		when(rocket.getRanges()).thenReturn(new IntervalInt(20, 40));

		melee = mock(ServerSpell.class);
		when(melee.getName()).thenReturn("Hard blow");
		when(melee.getCastTime()).thenReturn(200);
		when(melee.getRanges()).thenReturn(new IntervalInt(0, 6));

		//create the mob
		mob = new MobTemplate.Builder(uuidFactory.randomUUID(), "mob").spells(Arrays.asList(rocket,
				melee)).walkSpeed(3.0).runSpeed(5.0).build().createMob(timeProvider, new UUID(1, 2), randomProvider);

		PlayerMockFactory playerFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		player = playerFactory.createPlayer("Kalle");


		// Init services

		IOLocator locator = new ClasspathIOLocator();
		LuaSourceProvider sourceProvider = new SimpleLuaSourceProvider(locator);

		MobScriptEnvironment scriptEnv = new MobScriptEnvironment(sourceProvider,
				mobOrderExecutor,
				mobInfoProvider);
		ScriptedBrainTemplate template = new ScriptedBrainTemplate(locator, scriptEnv,
				mobOrderExecutor);
		brain = template.createBrain(mob, mock(SpawnArea.class), new BrainParameterProviderAdapter() {
			@Override
			public String getScriptPath() {
				return "mobs/ai/scott_michaels.lua";
			}
		});

		if (brain instanceof NullBrain) {
			throw new IllegalStateException("Could not find lua code");
		}

	}

	@Test
	public void testSequence() {
		movementService.moveAndRotateEntity(player,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(50, 0, 5), SpacedRotation.IDENTITY));
		player.getBaseStats().getStamina().changeValue(1000);
		player.getBaseStats().getCurrentHealth().changeValue(player.getBaseStats().getMaxHealth().getValue());

		movementService.moveAndRotateEntity(mob,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(1, 0, 1), SpacedRotation.IDENTITY));

		brain.getSmrtReceiver().combat().entityDamaged(player, mob, 10, 10, "", MagicSchool.ELECTRICITY);

		// ten seconds of normal mode - should first walking into range and then cast 3 rockets
		for (int i = 0; i < 10; i++) {
			advance();
		}
		verifyExactly(3).on(mobOrderExecutor).castSpell(mob, player, rocket);
		verifyNever().on(mobOrderExecutor).say(mob, "I will kill you Kalle!");

		// ten seconds of chase mode - should cast exactly one hard blow
		for (int i = 0; i < 12; i++) {
			advance();
		}
		//debug(mobOrderExecutor);

		verifyOnce().on(mobOrderExecutor).say(mob, "I will kill you Kalle!");
		verifyExactly(3).on(mobOrderExecutor).castSpell(mob, player, rocket);
		verifyExactly(1).on(mobOrderExecutor).castSpell(mob, player, melee);
		verifyNever().on(mobOrderExecutor).backAwayFrom(mob, any(SpacedVector3.class), anyDouble());

		// Let the mob back into range and fire more rockets
		for (int i = 0; i < 8; i++) {
			advance();
		}

		verifyAtLeast(1).on(mobOrderExecutor).backAwayFrom(mob, any(SpacedVector3.class), anyDouble());

		verifyOnce().on(mobOrderExecutor).say(mob, "I will kill you Kalle!");
		verifyExactly(1).on(mobOrderExecutor).castSpell(mob, player, melee);
		verifyExactly(4).on(mobOrderExecutor).castSpell(mob, player, rocket);
	}

	private void debug(MobOrderExecutor mock) {
		for (Invocation invocation : getData(mock).getInvocations()) {
			System.out.println(invocation.toString());
		}
	}

	private void advance() {
		long millis = 1000;
		timeProvider.advanceTime(millis);
		brain.act(timeProvider.now());
		mobOrderExecutor.executeMoveMap();

		//debugInfo();
	}

	private void debugInfo() {
		SimpleAlias alias = newAlias();
		alias.bind(mobOrderExecutor).castSpell(mob, player, rocket);
		SimpleAlias alias2 = newAlias();
		alias2.bind(mobOrderExecutor).castSpell(mob, player, melee);
		System.out.println("mob distance to player: " + SpacedVector3.distance(mob.getPosition(),
				player.getPosition()) + " rockets fired: " + alias.count() + ", hard blows: " + alias2.count());
	}
}
