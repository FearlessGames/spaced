package se.spaced.client.net.messagelisteners;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.ardor.ui.events.TradeEvents;
import se.spaced.client.ardor.ui.trade.TradeOffer;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.item.ClientItem;
import se.spaced.client.model.item.ItemJob;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.s2c.ServerTradeMessages;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.activecache.Job;
import se.spaced.shared.events.EventHandler;

import java.util.Map;

@Singleton
public class ServerTradeMessagesImpl implements ServerTradeMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final EventHandler eventHandler;
	private final ActiveCache<SpacedItem, ClientItem> itemCache;
	private final ActiveCache<Entity, ClientEntity> entityCache;
	private final ServerConnection serverConnection;

	private TradeOffer currentOffer;

	private final Map<String, Long> money = Maps.newHashMap();

	@Inject
	public ServerTradeMessagesImpl(
			EventHandler eventHandler,
			ActiveCache<SpacedItem, ClientItem> itemCache,
			ActiveCache<Entity, ClientEntity> entityCache, ServerConnection serverConnection) {
		this.eventHandler = eventHandler;
		this.itemCache = itemCache;
		this.entityCache = entityCache;
		this.serverConnection = serverConnection;
	}

	@Override
	public void tradeInitiated(Entity other, final String checksum) {
		entityCache.runWhenReady(other, new Job<ClientEntity>() {
			@Override
			public void run(ClientEntity value) {
				currentOffer = new TradeOffer(serverConnection.getReceiver().trade(), value, checksum);
				eventHandler.fireAsynchEvent(TradeEvents.TRADE_INITIATED, value);
			}
		});
	}

	@Override
	public void tradeInitFailed(String reason) {
		eventHandler.fireAsynchEvent(TradeEvents.TRADE_INIT_FAILED, reason);
	}

	@Override
	public void itemAdded(final Entity by, final SpacedItem item, final String checksum) {
		log.info("{} added to trade by {} ", item, by);
		entityCache.runWhenReady(by, new Job<ClientEntity>() {
			@Override
			public void run(final ClientEntity clientEntity) {
				itemCache.runWhenReady(item, new ItemJob() {
					@Override
					public void run(ClientItem clientItem) {
						boolean byMe = currentOffer.addItem(by, clientItem, checksum);
						eventHandler.fireAsynchEvent(TradeEvents.TRADE_ITEM_ADDED, clientItem, byMe);
					}
				});
			}
		});

	}

	@Override
	public void negotiating() {
		eventHandler.fireAsynchEvent(TradeEvents.TRADE_NEGOTIATING);
	}

	@Override
	public void addItemToOfferFail(String reason) {
		eventHandler.fireAsynchEvent(TradeEvents.TRADE_FAILED_TO_ADD_ITEM, reason);
	}

	@Override
	public void rejected(final Entity other) {
		currentOffer = null;
		fireEventWhenReady(other, TradeEvents.TRADE_CLOSED);
	}

	@Override
	public void aborted(Entity other) {
		currentOffer = null;
		fireEventWhenReady(other, TradeEvents.TRADE_ABORTED);
	}

	@Override
	public void accepted(Entity other) {
		fireEventWhenReady(other, TradeEvents.TRADE_ACCEPTED);
	}

	@Override
	public void completed() {
		currentOffer = null;
		eventHandler.fireAsynchEvent(TradeEvents.TRADE_COMPLETED);
	}

	@Override
	public void tradeFailedToComplete(String name) {
		currentOffer = null;
		eventHandler.fireAsynchEvent(TradeEvents.TRADE_FAILED_TO_COMPLETE, name);
	}

	@Override
	public void playerMoneyUpdate(String currency, long updateAmount, long totalAmount) {
		money.put(currency, totalAmount);
		eventHandler.fireAsynchEvent(TradeEvents.MONEY_AWARDED, currency, updateAmount, totalAmount);
	}

	@Override
	public void playerMoneySubtracted(String currency, long amount, long totalAmount) {
		money.put(currency, totalAmount);
		eventHandler.fireAsynchEvent(TradeEvents.MONEY_SUBTRACTED, currency, amount, totalAmount);
	}

	private void fireEventWhenReady(final Entity entity, final TradeEvents tradeEvent, final Object... params) {
		entityCache.runWhenReady(entity, new Job<ClientEntity>() {
			@Override
			public void run(ClientEntity value) {
				Object[] allParams = new Object[params.length + 1];
				allParams[0] = value;
				System.arraycopy(params, 0, allParams, 1, params.length);
				eventHandler.fireAsynchEvent(tradeEvent, allParams);
			}
		});
	}

	public TradeOffer getCurrentTrade() {
		return currentOffer;
	}

	public Map<String, Long> getMoney() {
		return ImmutableMap.copyOf(money);
	}
}
