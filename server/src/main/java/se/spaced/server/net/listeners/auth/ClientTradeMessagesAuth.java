package se.spaced.server.net.listeners.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.c2s.ClientTradeMessages;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.ItemService;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.trade.TradeAddItemResult;
import se.spaced.server.trade.TradeInitResult;
import se.spaced.server.trade.TradeService;

public class ClientTradeMessagesAuth implements ClientTradeMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ClientConnection clientConnection;
	private final TradeService tradeService;
	private final ItemService itemService;

	public ClientTradeMessagesAuth(
			ClientConnection clientConnection, TradeService tradeService, ItemService itemService) {
		this.clientConnection = clientConnection;
		this.tradeService = tradeService;
		this.itemService = itemService;
	}

	@Override
	public void initiateTrade(Entity collaborator) {
		Player player = clientConnection.getPlayer();

		if (player == null) {
			String message = "Tried to initiate trade when not logged in";
			clientConnection.getReceiver().trade().tradeInitFailed(message);
			throw new IllegalStateException(message);
		}

		if (collaborator == null) {
			String message = "Tried to initiate trade with unknown entity";
			log.warn(message);
			clientConnection.getReceiver().trade().tradeInitFailed(message);
			return;
		}
		ServerEntity other = (ServerEntity) collaborator;
		TradeInitResult initResult = tradeService.initiateTrade(player, other);
		switch (initResult) {
			case TOO_FAR_AWAY:
				String tooFarMessage = "Tried to init trade when too far away " + SpacedVector3.distance(player.getPosition(),
						other.getPosition());
				log.info(tooFarMessage);
				clientConnection.getReceiver().trade().tradeInitFailed(tooFarMessage);
				break;
			case TRADE_ALREADY_IN_PROGRESS:
				String message = "Tried to init trade when trade already in progress";
				log.info(message);
				clientConnection.getReceiver().trade().tradeInitFailed(message);
				break;
			default:
				break;
		}
	}

	@Override
	public void addItemToOffer(SpacedItem item) {
		ServerItem serverItem = (ServerItem) item;
		Player player = clientConnection.getPlayer();
		if (player == null) {
			throw new IllegalStateException("Tried to useItem when not ingame: " + clientConnection);
		}
		if (item == null) {
			throw new IllegalStateException("Trying to use null item. " + player);
		}

		if (itemService.isOwner(player, serverItem)) {
			TradeAddItemResult result = tradeService.addItem(player, serverItem);
			switch (result) {
				case DUPLICATE_ITEM:
					clientConnection.getReceiver().trade().addItemToOfferFail("Item already added!");
					break;
				case TRADE_NOT_INITIALIZED:
					clientConnection.getReceiver().trade().addItemToOfferFail("Theres no active trade!");
					break;
				case SUCCESS:
					//nothing, trade callback sends it
					break;
			}
		} else {
			throw new IllegalStateException("Trying to use someone elses item " + player + " - " + item);
		}
	}

	@Override
	public void acceptOffer(String checksum) {
		Player player = clientConnection.getPlayer();
		if (player == null) {
			throw new IllegalStateException("Tried to accept offer when not ingame: " + clientConnection);
		}
		tradeService.acceptOffer(player, checksum);

	}

	@Override
	public void rejectOffer() {
		Player player = clientConnection.getPlayer();
		tradeService.closeTrade(player);
	}

	@Override
	public void retractOffer() {
		Player player = clientConnection.getPlayer();
		tradeService.retractOffer(player);
	}
}