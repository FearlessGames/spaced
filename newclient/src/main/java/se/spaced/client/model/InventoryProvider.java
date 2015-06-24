package se.spaced.client.model;

import com.google.common.collect.HashMultimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearless.common.uuid.UUIDFactory;
import se.spaced.client.model.item.ClientInventory;
import se.spaced.client.model.item.ClientItem;

@Singleton
public class InventoryProvider {
	private ClientInventory playerInventory;

	@Inject
	public InventoryProvider(UUIDFactory uuidFactory) {
		playerInventory = new ClientInventory(uuidFactory.randomUUID(), HashMultimap.<Integer, ClientItem>create());
	}

	public ClientInventory getPlayerInventory() {
		return playerInventory;
	}

	public void setPlayerInventory(ClientInventory clientInventory) {
		playerInventory = clientInventory;
	}
}
