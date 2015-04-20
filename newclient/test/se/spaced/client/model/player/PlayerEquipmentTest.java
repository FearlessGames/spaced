package se.spaced.client.model.player;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.mockachino.annotations.*;
import se.spaced.client.core.states.StateChangeListener;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.item.ClientItem;
import se.spaced.client.model.listener.EquipmentListener;
import se.spaced.messages.protocol.AuraTemplate;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.Money;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;

public class PlayerEquipmentTest {
	@Mock
	private EquipmentListener listener;
	@Mock
	private ClientEntity entity;

	private ClientItem itemA;
	private ClientItem itemB;
	private ItemTemplateData templateA;
	private ItemTemplateData templateB;

	private ListenerDispatcher<EquipmentListener> dispatcher;
	private PlayerEntityProvider playerProvider;
	private PlayerEquipment equipment;

	@Before
	public void setUp() {
		setupMocks(this);

		UUIDFactory uuidFactory = UUIDFactoryImpl.INSTANCE;

		dispatcher = ListenerDispatcher.create(EquipmentListener.class);
		dispatcher.addListener(listener);
		playerProvider = new PlayerEntityProvider();
		playerProvider.setPlayerEntity(entity);

		equipment = new PlayerEquipment(dispatcher, playerProvider, ListenerDispatcher.create(StateChangeListener.class));

		templateA = new ItemTemplateData(uuidFactory.combUUID(),
				"Some epic item name",
				mock(AppearanceData.class),
				Sets.immutableEnumSet(ItemType.MAIN_HAND_ITEM, ItemType.OFF_HAND_ITEM), new HashSet<AuraTemplate>(),
				Money.ZERO, null);
		itemA = new ClientItem(uuidFactory.combUUID(), templateA);

		templateB = new ItemTemplateData(uuidFactory.combUUID(),
				"Some epic item name",
				mock(AppearanceData.class),
				Sets.immutableEnumSet(ItemType.TWO_HAND_ITEM), new HashSet<AuraTemplate>(),
				Money.ZERO, null);
		itemB = new ClientItem(uuidFactory.combUUID(), templateB);
	}

	@Test
	public void equipsItem() {
		equipment = new PlayerEquipment(dispatcher, playerProvider, ListenerDispatcher.create(StateChangeListener.class));
		equipment.equip(itemA, ContainerType.MAIN_HAND);

		verifyOnce().on(listener).itemEquipped(entity, templateA, ContainerType.MAIN_HAND);
	}

	@Test
	public void findsFirstEmptyContainer() {
		assertEquals(equipment.findEquippableContainer(itemA),
				ItemType.MAIN_HAND_ITEM.getMainSlot());

		equipment.equip(itemB, ItemType.MAIN_HAND_ITEM.getMainSlot());

		assertEquals(equipment.findEquippableContainer(itemA), ItemType.OFF_HAND_ITEM.getMainSlot());
	}

	@Test
	public void findsContainerForMultiOccupyingItem() {
		assertEquals(equipment.findEquippableContainer(itemB), ItemType.MAIN_HAND_ITEM.getMainSlot());
	}

	@Test
	public void unequipsItem() {
		equipment.equip(itemA, ContainerType.CHEST);

		equipment.unequip(ContainerType.CHEST, itemA);

		verifyOnce().on(listener).itemUnequipped(entity, ContainerType.CHEST);
	}

	@Test
	public void unequipFailsWhenAnotherItemIsInSlot() {
		equipment.equip(itemA, ContainerType.CHEST);

		equipment.unequip(ContainerType.CHEST, itemB);

		verifyNever().on(listener).itemUnequipped(entity, ContainerType.CHEST);
	}

}
