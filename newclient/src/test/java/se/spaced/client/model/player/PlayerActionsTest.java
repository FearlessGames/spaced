package se.spaced.client.model.player;

import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.mock.MockUtil;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.SystemTimeProvider;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.ClientSpell;
import se.spaced.client.model.PlayerTargeting;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.control.states.LocalRecorder;
import se.spaced.client.model.item.ClientItem;
import se.spaced.client.model.listener.UserCharacterListener;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.AuraTemplate;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.Money;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.HashSet;
import java.util.Random;

import static se.mockachino.Mockachino.*;

public class PlayerActionsTest {
	private PlayerActions playerActions;
	private UserCharacter userCharacter;
	private ServerConnection serverConnection;
	private UserCharacterListener listener;
	private PlayerTargeting targeting;
	private PlayerEquipment equipment;
	private LocalRecorder recorder;

	@Before
	public void setUp() throws Exception {
		targeting = mock(PlayerTargeting.class);
		equipment = mock(PlayerEquipment.class);
		serverConnection = MockUtil.deepMock(ServerConnection.class);
		listener = MockUtil.deepMock(UserCharacterListener.class);
		ListenerDispatcher<UserCharacterListener> dispatcher = ListenerDispatcher.create(UserCharacterListener.class);
		userCharacter = mock(UserCharacter.class);
		dispatcher.addListener(listener);
		recorder = new LocalRecorder(userCharacter, new MockTimeProvider(), null);
		playerActions = new PlayerActions(userCharacter, serverConnection, dispatcher, targeting, equipment,
				recorder);
	}

	@Test
	public void shouldStartSpellcastAtTarget() {
		ClientSpell spell = mock(ClientSpell.class);
		ClientEntity entity = mock(ClientEntity.class);
		stubReturn(entity).on(targeting).getTarget();

		playerActions.startSpellCast(spell);

		verifyOnce().on(serverConnection.getReceiver().combat()).startSpellCast(entity, spell);
	}

	@Test
	public void shouldStopSpellcast() {
		playerActions.stopSpellCast();
		verifyOnce().on(serverConnection.getReceiver().combat()).stopSpellCast();
	}

	@Test
	public void sendsResurrectRequest() {
		stubReturn(false).on(userCharacter).isAlive();
		playerActions.resurrect();
		verifyOnce().on(listener).resurrectionRequested();
	}

	@Test
	public void equipItemShouldDispatchEvent() {
		ListenerDispatcher<UserCharacterListener> dispatcher = ListenerDispatcher.create(UserCharacterListener.class);
		userCharacter = new UserCharacter(dispatcher);
		ClientEntity clientEntity = mock(ClientEntity.class);
		userCharacter.setControlledEntity(clientEntity);
		dispatcher.addListener(listener);
		recorder = new LocalRecorder(userCharacter, new MockTimeProvider(), null);
		playerActions = new PlayerActions(userCharacter, serverConnection, dispatcher, targeting, equipment,
				recorder);

		UUIDFactory uuidFactory = new UUIDFactoryImpl(new SystemTimeProvider(), new Random());
		ClientItem clientItem = new ClientItem(uuidFactory.randomUUID(),
				new ItemTemplateData(uuidFactory.randomUUID(), "name", new AppearanceData("", ""), new HashSet<ItemType>(),
						new HashSet<AuraTemplate>(), Money.ZERO, null));

		playerActions.equip(clientItem, ContainerType.HEAD);

		verifyOnce().on(listener).equip(clientItem, ContainerType.HEAD);
	}
}
