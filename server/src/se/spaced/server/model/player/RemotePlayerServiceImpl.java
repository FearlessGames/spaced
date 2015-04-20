package se.spaced.server.model.player;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.Player;
import se.spaced.server.model.currency.MoneyService;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.items.EquipmentService;
import se.spaced.server.model.items.EquippedItems;
import se.spaced.server.model.items.Inventory;
import se.spaced.server.model.items.InventoryService;
import se.spaced.server.model.items.InventoryType;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.SpellDataFactory;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.player.PlayerService;
import se.spaced.server.services.PlayerConnectedService;
import se.spaced.server.spell.SpellService;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.network.protocol.codec.datatype.SpellData;

import java.util.Collection;

@Singleton
public class RemotePlayerServiceImpl implements RemotePlayerService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final SpellService spellService;
	private final SmrtBroadcaster<S2CProtocol> smrtBroadcaster;
	private final EntityService entityService;

	private final PlayerConnectedService playerConnectedService;

	private final PlayerService playerService;
	private final InventoryService inventoryService;
	private final EquipmentService equipmentService;
	private final MoneyService moneyService;


	@Inject
	public RemotePlayerServiceImpl(
			EntityService entityService,
			PlayerConnectedService playerConnectedService,
			PlayerService playerService,
			SpellService spellService,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			InventoryService inventoryService,
			EquipmentService equipmentService, MoneyService moneyService) {
		this.entityService = entityService;
		this.playerConnectedService = playerConnectedService;
		this.playerService = playerService;
		this.spellService = spellService;

		this.smrtBroadcaster = smrtBroadcaster;
		this.inventoryService = inventoryService;
		this.equipmentService = equipmentService;
		this.moneyService = moneyService;
	}

	@Override
	public void playerLoggedIn(Player player, ClientConnection client) {
		UUID playerId = player.getPk();
		if (entityService.getEntity(playerId) == null) {
			playerService.reloadFromDatabase(player);
			client.setPlayer(player);

			//broadcast to the client its data
			logger.info("Sending login response to " + player.getName());
			EntityData playerData = player.createEntityData();
			EquippedItems equippedItems = equipmentService.getEquippedItems(player);

			S2CProtocol clientReceiver = client.getReceiver();
			clientReceiver.connection().playerLoginResponse(true,
					"Great Success!",
					playerData,
					equippedItems.getEquippedItems(), player.isGm());

			//inform all the other clients about this client
			smrtBroadcaster.create().toAll().send().connection().playerLoggedIn(playerData);

			entityService.addEntity(player, clientReceiver);

			sendSpells(player, clientReceiver);
			sendInventory(player, clientReceiver);
			playerConnectedService.addConnectedPlayer(player);
			equipmentService.applyAuras(player);

			moneyService.notifyEntity(player);
		}
	}

	private void sendInventory(Player player, S2CProtocol clientReceiver) {
		Inventory inventory = inventoryService.getInventory(player, InventoryType.BAG);
		if (inventory != null) {
			clientReceiver.item().sendInventory(inventory);
		}
	}

	private void sendSpells(Player player, S2CProtocol clientReceiver) {
		Collection<ServerSpell> allSpells = spellService.getAllSpells();
		Collection<SpellData> allSpellData = Lists.newArrayList();
		for (ServerSpell spell : allSpells) {
			allSpellData.add(SpellDataFactory.createSpellData(spell));
		}
		clientReceiver.spell().spellData(allSpellData);
	}

	@Override
	public void playerLoggedOut(Player player, ClientConnection clientConnection) {
		smrtBroadcaster.create().to(player).send().connection().logoutResponse();
		clientConnection.setPlayer(null);
		entityService.removeEntity(player);
		playerConnectedService.removeConnectedPlayer(player);
		playerService.updatePlayer(player);
		smrtBroadcaster.create().toAll().send().connection().playerDisconnected(player, player.getName());
	}
}
