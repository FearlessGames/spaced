package se.spaced.server.trade;

public enum TradeState {
	START,
	NEGOTIATING,
	INITIATOR_ACCEPT,
	COLLABORATOR_ACCEPT,
	COMPLETED,
	ABORTED
}
