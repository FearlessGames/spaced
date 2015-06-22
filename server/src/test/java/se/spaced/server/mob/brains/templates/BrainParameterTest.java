package se.spaced.server.mob.brains.templates;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.io.StreamLocator;
import se.fearlessgames.common.util.TimeProvider;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDMockFactory;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.brains.MobScriptEnvironment;
import se.spaced.server.model.combat.EntityCombatService;
import se.spaced.server.model.relations.RelationsService;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.tools.spawnpattern.view.InputType;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.mockachino.Mockachino.*;

public class BrainParameterTest {

	private UUIDFactory uuidFactory;

	@Before
	public void setUp() throws Exception {
		uuidFactory = new UUIDMockFactory();
	}

	@Test
	public void getSimple() throws Exception {
		NullBrainTemplate nbt = new NullBrainTemplate(uuidFactory.combUUID(), "null-brain");
		Set<? extends BrainParameter> requiredParameters = nbt.getRequiredParameters();
		assertNotNull(requiredParameters);
		assertEquals(0, requiredParameters.size());
	}

	@Test
	public void getScriptedParameters() throws Exception {
		ScriptedBrainTemplate scriptedBrainTemplate = new ScriptedBrainTemplate(mock(StreamLocator.class), mock(MobScriptEnvironment.class), mock(MobOrderExecutor.class));
		Set<? extends BrainParameter> requiredParameters = scriptedBrainTemplate.getRequiredParameters();
		assertNotNull(requiredParameters);
		assertEquals(1, requiredParameters.size());
		BrainParameter brainParameter = Iterables.getOnlyElement(requiredParameters);
		assertNotNull(brainParameter);
		assertEquals(ScriptedBrainTemplate.class, brainParameter.getBrain());
		assertEquals(ScriptedBrainTemplate.SCRIPT_PATH, brainParameter.getName());
	}

	@Test
	public void scriptedParametersUpdates() throws Exception {
		ScriptedBrainTemplate scriptedBrainTemplate = new ScriptedBrainTemplate(mock(StreamLocator.class), mock(MobScriptEnvironment.class), mock(MobOrderExecutor.class));
		Set<? extends BrainParameter> requiredParameters = scriptedBrainTemplate.getRequiredParameters();

		BrainParameter brainParameter = Iterables.getOnlyElement(requiredParameters);
		MobTemplate mobTemplate = mock(MobTemplate.class);

		MobSpawnTemplate mobSpawnTemplate = mock(MobSpawnTemplate.class);
		Object value = brainParameter.retrieveValue(mobTemplate, mobSpawnTemplate);
		verifyOnce().on(mobTemplate).getScriptPath();

		assertFalse(brainParameter.isEditable());

		try {
			brainParameter.updateValue(mobSpawnTemplate, "foo");
			fail("Not allowed to set script path");
		} catch (UnsupportedOperationException e) {
			verifyNever().on(mobTemplate).setScriptPath("foo");
		}
	}


	@Test
	public void getAggroingParameters() throws Exception {
		AggroingBrainTemplate aggroingBrainTemplate = new AggroingBrainTemplate(mock(MobOrderExecutor.class), mock(TimeProvider.class), mock(RelationsService.class), mock(EntityCombatService.class));
		Set<? extends BrainParameter> requiredParameters = aggroingBrainTemplate.getRequiredParameters();
		assertEquals(2, requiredParameters.size());
		BrainParameter proximity = Iterables.get(requiredParameters, 0);
		assertEquals(AggroingBrainTemplate.PROXIMITY, proximity.getName());
		BrainParameter social = Iterables.get(requiredParameters, 1);
		assertEquals(AggroingBrainTemplate.SOCIAL, social.getName());
	}

	@Test
	public void updateAggroingParameters() throws Exception {
		AggroingBrainTemplate aggroingBrainTemplate = new AggroingBrainTemplate(mock(MobOrderExecutor.class), mock(TimeProvider.class), mock(RelationsService.class), mock(EntityCombatService.class));
		Set<? extends BrainParameter> requiredParameters = aggroingBrainTemplate.getRequiredParameters();

		BrainParameter brainParameter = Iterables.get(requiredParameters, 0);
		MobTemplate mobTemplate = mock(MobTemplate.class);
		MobSpawnTemplate mobSpawnTemplate = mock(MobSpawnTemplate.class);
		Object value = brainParameter.retrieveValue(mobTemplate, mobSpawnTemplate);
		verifyOnce().on(mobTemplate).getProximityAggroDistance();

		assertFalse(brainParameter.isEditable());

		try {
			brainParameter.updateValue(mobSpawnTemplate, 3);
			fail("Not allowed to set aggro params");
		} catch (UnsupportedOperationException e) {
		}

	}

	@Test
	public void getCompositeParameters() throws Exception {
		ScriptedBrainTemplate scriptedBrainTemplate = new ScriptedBrainTemplate(mock(StreamLocator.class), mock(MobScriptEnvironment.class), mock(MobOrderExecutor.class));
		AggroingBrainTemplate aggroingBrainTemplate = new AggroingBrainTemplate(mock(MobOrderExecutor.class), mock(TimeProvider.class), mock(RelationsService.class), mock(EntityCombatService.class));
		CompositeBrainTemplate compositeBrainTemplate = CompositeBrainTemplate.create(uuidFactory.combUUID(), aggroingBrainTemplate, scriptedBrainTemplate);
		Set<? extends BrainParameter> requiredParameters = compositeBrainTemplate.getRequiredParameters();
		assertEquals(scriptedBrainTemplate.getRequiredParameters().size() + aggroingBrainTemplate.getRequiredParameters().size(), requiredParameters.size());

		Iterable<String> names = Iterables.transform(requiredParameters, new Function<BrainParameter, String>() {
			@Override
			public String apply(BrainParameter o) {
				return o.getName();
			}
		});
		assertTrue(Iterables.contains(names, ScriptedBrainTemplate.SCRIPT_PATH));
		assertTrue(Iterables.contains(names, AggroingBrainTemplate.PROXIMITY));
	}

	@Test
	public void getPatrollingParameters() throws Exception {
		PatrollingBrainTemplate patrollingBrainTemplate = new PatrollingBrainTemplate(mock(MobOrderExecutor.class));
		ImmutableSet<BrainParameter> requiredParameters = patrollingBrainTemplate.getRequiredParameters();
		assertEquals(1, requiredParameters.size());
		BrainParameter parameter = Iterables.getOnlyElement(requiredParameters);
		assertEquals(PatrollingBrainTemplate.PATROL_PATH, parameter.getName());
		assertEquals(InputType.GEOMETRY, parameter.getType());
	}
}
