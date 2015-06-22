package se.spaced.server.trade;

import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.ExchangeResult;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.net.broadcast.SmrtBroadcaster;

public class SmrtTradeCallback implements TradeCallback {
	private final SmrtBroadcaster<S2CProtocol> broadcaster;
	private final TradeExecutor tradeExecutor;

	public SmrtTradeCallback(SmrtBroadcaster<S2CProtocol> broadcaster, TradeExecutor tradeExecutor) {
		this.broadcaster = broadcaster;
		this.tradeExecutor = tradeExecutor;
	}

	@Override
	public void initiated(ServerEntity initiator, ServerEntity collaborator, String checksum) {
		broadcaster.create().to(initiator).send().trade().tradeInitiated(collaborator, checksum);
		broadcaster.create().to(collaborator).send().trade().tradeInitiated(initiator, checksum);
	}

	@Override
	public void initiatorAccepted(ServerEntity initiator, ServerEntity collaborator) {
		broadcaster.create().to(collaborator).send().trade().accepted(initiator);
	}

	@Override
	public void collaboratorAccepted(ServerEntity initiator, ServerEntity collaborator) {
		broadcaster.create().to(initiator).send().trade().accepted(collaborator);
	}

	@Override
	public void itemAdded(ServerEntity by, ServerEntity other, ServerItem item, String checksum) {
		broadcaster.create().to(by).to(other).send().trade().itemAdded(by, item, checksum);
	}

	@Override
	public void negotiating(ServerEntity initiator, ServerEntity collaborator) {
		broadcaster.create().to(initiator).to(collaborator).send().trade().negotiating();
	}

	@Override
	public void collaboratorRejected(ServerEntity initiator, ServerEntity collaborator) {
		broadcaster.create().to(initiator).send().trade().rejected(collaborator);
	}

	@Override
	public void initiatorRejected(ServerEntity initiator, ServerEntity collaborator) {
		broadcaster.create().to(collaborator).send().trade().rejected(initiator);
	}

	@Override
	public void aborted(ServerEntity initiator, ServerEntity collaborator) {
		broadcaster.create().to(initiator).send().trade().aborted(collaborator);
		broadcaster.create().to(collaborator).send().trade().aborted(initiator);
	}

	@Override
	public void tradeCompleted(TradeTransaction transaction) {
		ExchangeResult exchangeResult = tradeExecutor.executeTrade(transaction);
		if (exchangeResult == ExchangeResult.SUCCESS) {
			broadcaster.create().to(transaction.getInitiator()).to(transaction.getCollaborator()).send().trade().completed();
		} else {
			broadcaster.create().to(transaction.getInitiator()).to(transaction.getCollaborator()).send().trade().tradeFailedToComplete(exchangeResult.name());
		}
	}
 }
