package se.spaced.client.model;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.spaced.client.model.listener.ClientEntityListener;
import se.spaced.client.model.listener.UserCharacterListener;
import se.spaced.shared.model.*;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.util.ListenerDispatcher;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;


public class UserCharacterTest {
	private UserCharacter userCharacter;
	private UserCharacterListener listener;
	private ClientEntity userEntity;

	@Before
	public void setUp() {
		ListenerDispatcher<UserCharacterListener> dispatcher = ListenerDispatcher.create(UserCharacterListener.class);
		listener = mock(UserCharacterListener.class);
		dispatcher.addListener(listener);

		ClientEntity target = mock(ClientEntity.class);

		TimeProvider timeProvider = new MockTimeProvider();
		EntityData entityData = new EntityData(
				UUID.ZERO,
				"",
				new PositionalData(),
				new AppearanceData("", ""),
				new CreatureType(""),
				new EntityStats(timeProvider),
				new Faction(""),
				AnimationState.IDLE, EntityState.ALIVE);
		ListenerDispatcher<ClientEntityListener> entityListenerDispatcher = ListenerDispatcher.create(ClientEntityListener.class);
		userEntity = new ClientEntity(entityData, entityListenerDispatcher);
		userCharacter = new UserCharacter(dispatcher);
		userCharacter.setControlledEntity(userEntity);
	}

	@Test
	public void shouldEnterCombat() {
		userCharacter.setInCombat(true);
		verifyExactly(1).on(listener).combatStateUpdated(true);
	}

	@Test
	public void shouldNotEnterCombatWhenInCombat() {
		userCharacter.setInCombat(true);
		userCharacter.setInCombat(true);

		verifyExactly(1).on(listener).combatStateUpdated(true);
	}

	@Test
	public void shouldLeaveCombat() {
		userCharacter.setInCombat(true);
		userCharacter.setInCombat(false);

		verifyExactly(1).on(listener).combatStateUpdated(true);
		verifyExactly(1).on(listener).combatStateUpdated(false);
	}

	@Test
	public void shouldNotMoveWhileDead() {
		userEntity.setAlive(false);
		userCharacter.setPositionalData(mock(PositionalData.class));
		verifyNever().on(listener).positionalDataUpdated(any(PositionalData.class));
	}
}
