package se.spaced.server.trade;

import com.google.common.collect.ImmutableList;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.ServerItem;

import java.util.Collection;

public class TradeTransaction {
	private final ServerEntity initiator;
	private final ImmutableList<ServerItem> itemsFromInitiator;
	private final ImmutableList<ServerItem> itemsFromCollaborator;
	private final ServerEntity collaborator;


	public TradeTransaction(
			ServerEntity initiator,
			ServerEntity collaborator,
			Collection<ServerItem> itemsFromInitiator,
			Collection<ServerItem> itemsFromCollaborator) {
		this.collaborator = collaborator;
		this.initiator = initiator;

		this.itemsFromInitiator = ImmutableList.copyOf(itemsFromInitiator);
		this.itemsFromCollaborator = ImmutableList.copyOf(itemsFromCollaborator);
	}

	public ServerEntity getInitiator() {
		return initiator;
	}

	public ServerEntity getCollaborator() {
		return collaborator;
	}

	public ImmutableList<ServerItem> getItemsFromInitiator() {
		return itemsFromInitiator;
	}

	public ImmutableList<ServerItem> getItemsFromCollaborator() {
		return itemsFromCollaborator;
	}
}
