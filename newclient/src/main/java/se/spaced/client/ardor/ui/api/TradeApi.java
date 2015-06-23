package se.spaced.client.ardor.ui.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.ardor.ui.trade.TradeOffer;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.net.messagelisteners.ServerTradeMessagesImpl;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.client.statistics.Analytics;
import se.spaced.client.statistics.Trackables;

import java.util.Map;

@Singleton
public class TradeApi {
	private final ServerConnection serverConnection;
	private final ServerTradeMessagesImpl tradeMessages;
	private final Analytics analytics;

	@Inject
	public TradeApi(ServerConnection serverConnection, ServerTradeMessagesImpl tradeMessages, Analytics analytics) {
		this.serverConnection = serverConnection;
		this.tradeMessages = tradeMessages;
		this.analytics = analytics;
	}

	@LuaMethod(name = "InitTrade", global = true)
	public void initTrade(ClientEntity other) {
		analytics.track(Trackables.TradeEvents.INIT);
		if (other != null) {
			serverConnection.getReceiver().trade().initiateTrade(other);
		}
	}

	@LuaMethod(name = "GetCurrentTradeOffer", global = true)
	public TradeOffer getCurrentTradeOffer() {
		analytics.track(Trackables.TradeEvents.GET_CURRENT_OFFER);
		return tradeMessages.getCurrentTrade();
	}

	@LuaMethod(name = "GetMoney", global = true)
	public Map<String, Long> getMoney() {
		analytics.track(Trackables.TradeEvents.GET_MONEY);
		return tradeMessages.getMoney();
	}
}
