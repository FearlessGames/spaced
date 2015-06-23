package se.spaced.client.statistics;

public class Trackables {
	public static enum VendorEvents implements Trackable {
		INIT("VENDOR_INIT"),
		BUY("VENDOR_BUY"),
		CLOSE("VENDOR_CLOSE"),
		GET_STOCK("VENDOR_GET_STOCK");

		private final String eventCode;


		VendorEvents(String eventCode) {
			this.eventCode = eventCode;
		}


		@Override
		public String getEventCode() {
			return eventCode;
		}
	}

	public static enum PlayerActionsEvents implements Trackable {
		UNSTUCK("PLAYER_ACTIONS_UNSTUCK"),
		DANCE("PLAYER_DANCE");

		private final String eventCode;

		PlayerActionsEvents(String eventCode) {
			this.eventCode = eventCode;
		}

		@Override
		public String getEventCode() {
			return eventCode;
		}
	}

	public static enum TradeEvents implements Trackable {
		INIT("TRADE_INIT"),
		GET_CURRENT_OFFER("TRADE_GET_OFFER"),
		GET_MONEY("TRADE_GET_MONEY");


		private final String eventCode;

		TradeEvents(String eventCode) {
			this.eventCode = eventCode;
		}

		@Override
		public String getEventCode() {
			return eventCode;
		}
	}

}
