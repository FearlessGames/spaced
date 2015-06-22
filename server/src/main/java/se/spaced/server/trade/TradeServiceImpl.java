package se.spaced.server.trade;

import com.google.common.collect.Maps;
import se.ardortech.math.SpacedVector3;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.ServerItem;
import se.spaced.shared.RangeConstants;

import java.util.Map;

public class TradeServiceImpl implements TradeService {

	private final Map<ServerEntity, Trade> trades = Maps.newHashMap();
	private final TradeTransitionModelProvider transitionModelProvider;


	public TradeServiceImpl(TradeTransitionModelProvider transitionModelProvider) {
		this.transitionModelProvider = transitionModelProvider;
	}

	@Override
	public TradeInitResult initiateTrade(ServerEntity initiator, ServerEntity collaborator) {
		if (SpacedVector3.distanceSq(initiator.getPosition(), collaborator.getPosition()) > RangeConstants.INTERACTION_RANGE_SQ) {
			return TradeInitResult.TOO_FAR_AWAY;
		}

		if (trades.containsKey(initiator) || trades.containsKey(collaborator)) {
			return TradeInitResult.TRADE_ALREADY_IN_PROGRESS;
		}

		Trade trade = new Trade(initiator, collaborator, transitionModelProvider.get());
		trades.put(initiator, trade);
		trades.put(collaborator, trade);

		trade.initiated();

		return TradeInitResult.SUCCESS;
	}

	@Override
	public TradeAddItemResult addItem(ServerEntity entity, ServerItem item) {
		Trade trade = trades.get(entity);
		if (trade == null) {
			return TradeAddItemResult.TRADE_NOT_INITIALIZED;
		}

		if (trade.containsItem(item)) {
			return TradeAddItemResult.DUPLICATE_ITEM;
		}

		if (entity.equals(trade.getInitiator())) {
			trade.addItemFromInitiator(item);
		} else {
			trade.addItemFromCollaborator(item);
		}

		return TradeAddItemResult.SUCCESS;
	}

	@Override
	public TradeAcceptResult acceptOffer(ServerEntity entity, String checksum) {
		Trade trade = trades.get(entity);
		if (trade == null) {
			return TradeAcceptResult.TRADE_NOT_INITIALIZED;
		}
		boolean didTransition;
		if (entity.equals(trade.getInitiator())) {
			didTransition = trade.initiatorAccepted(checksum);
		} else {
			didTransition = trade.collaboratorAccepted(checksum);
		}
		if (trade.getCurrentState() == TradeState.COMPLETED) {
			trades.remove(trade.getInitiator());
			trades.remove(trade.getCollaborator());
		}
		if (!didTransition) {
			return TradeAcceptResult.ACCEPT_FAILED;
		}
		return TradeAcceptResult.SUCCESS;
	}

	@Override
	public void closeTrade(ServerEntity entity) {
		Trade trade = trades.get(entity);

		if (trade == null) {
			return;
		}

		trades.remove(trade.getInitiator());
		trades.remove(trade.getCollaborator());

		if (entity.equals(trade.getInitiator())) {
			trade.initiatorRejected();
		} else {
			trade.collaboratorRejected();
		}

	}

	@Override
	public void retractOffer(ServerEntity entity) {
		Trade trade = trades.get(entity);
		if (trade == null) {
			return;
		}

		if (entity.equals(trade.getInitiator())) {
			trade.initiatorRetracted();
		} else {
			trade.collaboratorRetracted();
		}
	}

	@Override
	public void entityAdded(ServerEntity entity) {
	}

	@Override
	public void entityRemoved(ServerEntity entity) {
		Trade trade = trades.get(entity);

		if (trade == null) {
			return;
		}

		trades.remove(trade.getInitiator());
		trades.remove(trade.getCollaborator());

		trade.abortTrade();
	}
}

