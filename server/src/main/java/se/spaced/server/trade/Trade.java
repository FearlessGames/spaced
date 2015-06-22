package se.spaced.server.trade;

import com.google.common.collect.Sets;
import se.fearlessgames.common.util.Digester;
import se.hiflyer.fettle.Arguments;
import se.hiflyer.fettle.StateMachine;
import se.hiflyer.fettle.StateMachineTemplate;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.ServerItem;

import java.util.Collection;

/**
 * Represents an instance of a trade.
 */
public class Trade {
	private static final Digester DIGESTER = new Digester("Trade");

	private final ServerEntity initiator;
	private final ServerEntity collaborator;
	private final Collection<ServerItem> itemsFromInitiator;
	private final Collection<ServerItem> itemsFromCollaborator;

	final StateMachine<TradeState, TradeActions> state;

	public Trade(
			ServerEntity initiator,
			ServerEntity collaborator,
			StateMachineTemplate<TradeState, TradeActions> tradeTransitionModel) {
		this.initiator = initiator;
		this.collaborator = collaborator;
		itemsFromInitiator = Sets.newLinkedHashSet();
		itemsFromCollaborator = Sets.newLinkedHashSet();
		state = tradeTransitionModel.newStateMachine(TradeState.START);
	}

	public void initiated() {
		state.fireEvent(TradeActions.INITIATED, new Arguments(this));
	}

	public boolean collaboratorAccepted(String checksum) {
		return state.fireEvent(TradeActions.COLLABORATOR_ACCEPT, new Arguments(this, checksum));
	}

	public boolean initiatorAccepted(String checksum) {
		return state.fireEvent(TradeActions.INITIATOR_ACCEPT, new Arguments(this, checksum));
	}

	public void addItemFromInitiator(ServerItem item) {
		state.fireEvent(TradeActions.OFFER_UPDATED, new Arguments(this, item, initiator, collaborator));
	}

	public void addItemFromCollaborator(ServerItem item) {
		state.fireEvent(TradeActions.OFFER_UPDATED, new Arguments(this, item, collaborator, initiator));
	}

	public void collaboratorRetracted() {
		state.fireEvent(TradeActions.COLLABORATOR_RETRACT, new Arguments(this));
	}

	public void initiatorRetracted() {
		state.fireEvent(TradeActions.INITIATOR_RETRACT, new Arguments(this));
	}

	public void abortTrade() {
		state.fireEvent(TradeActions.ABORTED, new Arguments(this));
	}

	public void initiatorRejected() {
		state.fireEvent(TradeActions.INITIATOR_REJECTED, new Arguments(this));
	}

	public void collaboratorRejected() {
		state.fireEvent(TradeActions.COLLABORATOR_REJECTED, new Arguments(this));
	}


	Collection<ServerItem> getItemsFromInitiator() {
		return itemsFromInitiator;
	}


	Collection<ServerItem> getItemsFromCollaborator() {
		return itemsFromCollaborator;
	}

	public ServerEntity getInitiator() {
		return initiator;
	}


	public ServerEntity getCollaborator() {
		return collaborator;
	}

	public TradeState getCurrentState() {
		return state.getCurrentState();
	}

	public boolean containsItem(ServerItem item) {
		return itemsFromInitiator.contains(item) || itemsFromCollaborator.contains(item);
	}


	public String getChecksum() {
		// Enough room for the uuids of 12 items
		StringBuilder builder = new StringBuilder(436);
		for (ServerItem item : itemsFromInitiator) {
			builder.append(item.getPk().toString());
		}
		for (ServerItem item : itemsFromCollaborator) {
			builder.append(item.getPk().toString());
		}
		return DIGESTER.sha512Hex(builder.toString());
	}


	TradeTransaction getTradeTransaction() {
		if (state.getCurrentState() != TradeState.COMPLETED) {
			throw new IllegalStateException("Tried to get trade transaction for trade " + this);
		}
		return new TradeTransaction(initiator, collaborator, itemsFromInitiator, itemsFromCollaborator);
	}


}
