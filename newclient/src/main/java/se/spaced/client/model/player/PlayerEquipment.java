package se.spaced.client.model.player;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.core.states.GameState;
import se.spaced.client.core.states.StateChangeListener;
import se.spaced.client.core.states.WorldGameState;
import se.spaced.client.model.item.ClientItem;
import se.spaced.client.model.listener.EquipmentListener;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.Map;
import java.util.Set;

@Singleton
public class PlayerEquipment implements StateChangeListener {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final Map<ContainerType, ClientItem> equippedItems = Maps.newEnumMap(ContainerType.class);
	private final ListenerDispatcher<EquipmentListener> dispatcher;
	private final PlayerEntityProvider playerProvider;

	@Inject
	public PlayerEquipment(
			ListenerDispatcher<EquipmentListener> dispatcher,
			PlayerEntityProvider playerProvider,
			ListenerDispatcher<StateChangeListener> stateChangeListener) {
		this.dispatcher = dispatcher;
		this.playerProvider = playerProvider;
		stateChangeListener.addListener(this);
	}

	public void equip(ClientItem item, ContainerType container) {
		log.debug("Equipped item {}", item);
		equippedItems.put(container, item);
		dispatcher.trigger().itemEquipped(playerProvider.get(), item.getItemTemplateData(), container);
	}

	public void unequip(ContainerType container, ClientItem clientItem) {
		ClientItem currentItem = equippedItems.get(container);
		if (currentItem != null && currentItem.equals(clientItem)) {
			equippedItems.remove(container);
			dispatcher.trigger().itemUnequipped(playerProvider.get(), container);
		}
	}

	public ClientItem findEquippedItem(ContainerType container) {
		return equippedItems.get(container);
	}

	public ContainerType findEquippableContainer(final ClientItem item) {
		if (item == null) {
			return null;
		}

		final Set<ItemType> itemTypes = item.getItemTypes();
		for (final ItemType itemType : itemTypes) {
			if (itemType == ItemType.BAG) {
				continue;
			}
			final ContainerType container = itemType.getMainSlot();
			if (equippedItems.get(container) == null) {
				return container;
			}
		}

		return itemTypes.iterator().next().getMainSlot();
	}

	@Override
	public void onStateChange(GameState oldState, GameState newState) {
		if (oldState instanceof WorldGameState) {
			log.info("OnStateChange: clearing equipped items");
			equippedItems.clear();
		}
	}
}
