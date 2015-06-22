package se.spaced.server.net.listeners.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.c2s.ClientEquipmentMessages;
import se.spaced.server.model.Player;
import se.spaced.server.model.items.EquipmentService;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.net.ClientConnection;
import se.spaced.shared.model.items.ContainerType;

public class ClientEquipmentMessagesAuth implements ClientEquipmentMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ClientConnection clientConnection;
	private final EquipmentService equipmentService;

	public ClientEquipmentMessagesAuth(ClientConnection clientConnection, EquipmentService equipmentService) {
		this.clientConnection = clientConnection;
		this.equipmentService = equipmentService;
	}


	@Override
	public void equipItem(SpacedItem item, ContainerType type) {
		Player player = clientConnection.getPlayer();
		if (player == null) {
			throw new IllegalStateException("Tried to equipItem when not ingame: " + clientConnection);
		}
		if (item == null) {
			log.warn("Trying to equip null item. " + player);
			return;
		}
		equipmentService.equipItem(player, type, (ServerItem) item);
	}

	@Override
	public void unequipItem(ContainerType type) {
		Player player = clientConnection.getPlayer();
		if (player == null) {
			throw new IllegalStateException("Tried to unequipItem when not ingame: " + clientConnection);
		}

		equipmentService.unequipItem(player, type);
	}
}