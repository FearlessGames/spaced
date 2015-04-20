package se.spaced.server.trade;

import com.google.inject.Inject;
import com.google.inject.Provider;
import se.hiflyer.fettle.Action;
import se.hiflyer.fettle.Arguments;
import se.hiflyer.fettle.Condition;
import se.hiflyer.fettle.Fettle;
import se.hiflyer.fettle.StateMachine;
import se.hiflyer.fettle.StateMachineTemplate;
import se.hiflyer.fettle.builder.StateMachineBuilder;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.ServerItem;

import java.util.Collection;

public class TradeTransitionModelProvider implements Provider<StateMachineTemplate<TradeState, TradeActions>> {

	private final TradeCallback tradeCallback;
	private final StateMachineTemplate<TradeState, TradeActions> instance;

	@Inject
	public TradeTransitionModelProvider(TradeCallback tradeCallback) {
		this.tradeCallback = tradeCallback;
		instance = buildStateMachine();
	}

	@Override
	public StateMachineTemplate<TradeState, TradeActions> get() {
		return instance;
	}

	private StateMachineTemplate<TradeState, TradeActions> buildStateMachine() {
		StateMachineBuilder<TradeState, TradeActions> builder = Fettle.newBuilder(TradeState.class, TradeActions.class);

		builder.onEntry(TradeState.NEGOTIATING).perform(new NegotiatingEntryAction());
		builder.onEntry(TradeState.COMPLETED).perform(new TradeCompletedAction());
		builder.onEntry(TradeState.ABORTED).perform(new AbortedEntryAction());

		builder.transition().from(TradeState.START).to(TradeState.NEGOTIATING).on(TradeActions.INITIATED).perform(
				new Action<TradeState, TradeActions>() {
					@Override
					public void onTransition(TradeState from, TradeState to, TradeActions cause, Arguments args, StateMachine<TradeState, TradeActions> machine) {
						if (args.getNumberOfArguments() > 0) {
							Trade trade = (Trade) args.getFirst();
							tradeCallback.initiated(trade.getInitiator(), trade.getCollaborator(), trade.getChecksum());
						} else {
							throw new RuntimeException("Bad number of arguments for initiated " + args);
						}
					}
				});

		addOfferUppdates(builder);

		addAccepts(builder);

		addRejects(builder);

		addClosing(builder);

		addTradeViolations(builder);

		return builder.buildTransitionModel();
	}

	private void addOfferUppdates(StateMachineBuilder<TradeState, TradeActions> builder) {
		Condition noDuplicatesCondition = new NoDuplicationCondition();

		builder.transition().from(TradeState.NEGOTIATING).to(TradeState.NEGOTIATING).on(TradeActions.OFFER_UPDATED).when(noDuplicatesCondition);

		builder.transition().from(TradeState.COLLABORATOR_ACCEPT).to(TradeState.NEGOTIATING).on(TradeActions.OFFER_UPDATED).when(noDuplicatesCondition);

		builder.transition().from(TradeState.INITIATOR_ACCEPT).to(TradeState.NEGOTIATING).on(TradeActions.OFFER_UPDATED).when(noDuplicatesCondition);

	}

	private void addAccepts(StateMachineBuilder<TradeState, TradeActions> builder) {
		Condition checksumMatchesCondition = new ChecksumMatchesCondition();

		builder.transition().from(TradeState.NEGOTIATING).to(TradeState.INITIATOR_ACCEPT).on(TradeActions.INITIATOR_ACCEPT).when(checksumMatchesCondition).
				perform(new Action<TradeState, TradeActions>() {
					@Override
					public void onTransition(TradeState from, TradeState to, TradeActions cause, Arguments args, StateMachine<TradeState, TradeActions> machine) {
						if (args.getNumberOfArguments() > 0) {
							Trade trade = (Trade) args.getFirst();
							tradeCallback.initiatorAccepted(trade.getInitiator(), trade.getCollaborator());
						}
					}
				});

		builder.transition().from(TradeState.NEGOTIATING).to(TradeState.COLLABORATOR_ACCEPT).on(TradeActions.COLLABORATOR_ACCEPT).when(checksumMatchesCondition).
				perform(new Action<TradeState, TradeActions>() {
					@Override
					public void onTransition(TradeState from, TradeState to, TradeActions cause, Arguments args, StateMachine<TradeState, TradeActions> machine) {
						if (args.getNumberOfArguments() > 0) {
							Trade trade = (Trade) args.getFirst();
							tradeCallback.collaboratorAccepted(trade.getInitiator(), trade.getCollaborator());
						}
					}
				});


		builder.transition().from(TradeState.COLLABORATOR_ACCEPT).to(TradeState.COMPLETED).on(TradeActions.INITIATOR_ACCEPT).when(checksumMatchesCondition);

		builder.transition().from(TradeState.INITIATOR_ACCEPT).to(TradeState.COMPLETED).on(TradeActions.COLLABORATOR_ACCEPT).when(checksumMatchesCondition);
	}

	private void addRejects(StateMachineBuilder<TradeState, TradeActions> builder) {
		builder.transition().from(TradeState.INITIATOR_ACCEPT).to(TradeState.NEGOTIATING).on(TradeActions.INITIATOR_RETRACT);

		builder.transition().from(TradeState.COLLABORATOR_ACCEPT).to(TradeState.NEGOTIATING).on(TradeActions.INITIATOR_RETRACT);


		builder.transition().from(TradeState.COLLABORATOR_ACCEPT).to(TradeState.NEGOTIATING).on(TradeActions.COLLABORATOR_RETRACT);

		builder.transition().from(TradeState.INITIATOR_ACCEPT).to(TradeState.NEGOTIATING).on(TradeActions.COLLABORATOR_RETRACT);
	}

	private void addClosing(StateMachineBuilder<TradeState, TradeActions> builder) {

		builder.transition().fromAll().to(TradeState.ABORTED).on(TradeActions.COLLABORATOR_REJECTED).
				perform(new Action<TradeState, TradeActions>() {
					@Override
					public void onTransition(TradeState from, TradeState to, TradeActions cause, Arguments args, StateMachine<TradeState, TradeActions> machine) {
						if (args.getNumberOfArguments() > 0) {
							Trade trade = (Trade) args.getFirst();
							tradeCallback.collaboratorRejected(trade.getInitiator(), trade.getCollaborator());
						}
					}
				});

		builder.transition().fromAll().to(TradeState.ABORTED).on(TradeActions.INITIATOR_REJECTED).
				perform(new Action<TradeState, TradeActions>() {
					@Override
					public void onTransition(TradeState from, TradeState to, TradeActions cause, Arguments args, StateMachine<TradeState, TradeActions> machine) {
						if (args.getNumberOfArguments() == 1) {
							Trade trade = (Trade) args.getFirst();
							tradeCallback.initiatorRejected(trade.getInitiator(), trade.getCollaborator());
						} else {
							throw new RuntimeException("Bad number of arguments for initator rejected - " + args);
						}
					}
				});

	}

	private void addTradeViolations(StateMachineBuilder<TradeState, TradeActions> builder) {
		Action<TradeState, TradeActions> tryToUpdateCompletedTradeAction = new ClosedTradeViolationAction();

		builder.transition().from(TradeState.COMPLETED).to(TradeState.COMPLETED).on(TradeActions.ABORTED).perform(tryToUpdateCompletedTradeAction);

		builder.transition().from(TradeState.COMPLETED).to(TradeState.COMPLETED).on(TradeActions.COLLABORATOR_REJECTED).perform(tryToUpdateCompletedTradeAction);

		builder.transition().from(TradeState.COMPLETED).to(TradeState.COMPLETED).on(TradeActions.INITIATOR_REJECTED).perform(tryToUpdateCompletedTradeAction);

		builder.transition().from(TradeState.COMPLETED).to(TradeState.COMPLETED).on(TradeActions.OFFER_UPDATED).perform(tryToUpdateCompletedTradeAction);

		builder.transition().from(TradeState.ABORTED).to(TradeState.ABORTED).on(TradeActions.OFFER_UPDATED).perform(tryToUpdateCompletedTradeAction);

		builder.transition().fromAll().to(TradeState.ABORTED).on(TradeActions.ABORTED);
	}

	private static class ChecksumMatchesCondition implements Condition {
		@Override
		public boolean isSatisfied(Arguments args) {
			if (args.getNumberOfArguments() < 2) {
				return false;
			}
			try {
				Trade trade = (Trade) args.getFirst();
				String checksum = (String) args.getArgument(1);
				return checksum.equals(trade.getChecksum());
			} catch (ClassCastException e) {
				return false;
			}
		}

	}

	private static class NoDuplicationCondition implements Condition {
		@Override
		public boolean isSatisfied(Arguments arguments) {
			if (arguments.getNumberOfArguments() < 2) {
				return false;
			}
			Trade trade = (Trade) arguments.getFirst();
			ServerItem item = (ServerItem) arguments.getArgument(1);
			return !trade.containsItem(item);
		}

	}

	private class NegotiatingEntryAction implements Action<TradeState, TradeActions> {
		@Override
		public void onTransition(TradeState from, TradeState to, TradeActions cause, Arguments args, StateMachine<TradeState, TradeActions> machine) {
			if (args.getNumberOfArguments() < 1) {
				throw new RuntimeException("Bad number of arguments for negotiating entry - " + args);
			}
			Trade trade = (Trade) args.getFirst();
			ServerEntity initiator = trade.getInitiator();
			ServerEntity collaborator = trade.getCollaborator();
			tradeCallback.negotiating(initiator, collaborator);
			if (args.getNumberOfArguments() < 3) {
				return;
			}

			ServerItem item = (ServerItem) args.getArgument(1);
			ServerEntity by = (ServerEntity) args.getArgument(2);
			ServerEntity other = (ServerEntity) args.getArgument(3);
			Collection<ServerItem> items = by == initiator ? trade.getItemsFromInitiator() : trade.getItemsFromCollaborator();
			items.add(item);
			tradeCallback.itemAdded(by, other, item, trade.getChecksum());
		}

	}

	private class AbortedEntryAction implements Action<TradeState, TradeActions> {
		@Override
		public void onTransition(TradeState from, TradeState to, TradeActions cause, Arguments args, StateMachine<TradeState, TradeActions> machine) {
			if (args.getNumberOfArguments() > 0) {
				Trade trade = (Trade) args.getFirst();

				tradeCallback.aborted(trade.getInitiator(), trade.getCollaborator());
			}
		}

	}

	private class TradeCompletedAction implements Action<TradeState, TradeActions> {
		@Override
		public void onTransition(TradeState from, TradeState to, TradeActions causedBy, Arguments args, StateMachine<TradeState, TradeActions> machine) {
			if (args.getNumberOfArguments() > 0) {
				Trade trade = (Trade) args.getFirst();
				tradeCallback.tradeCompleted(trade.getTradeTransaction());
			}
		}

	}

	private static class ClosedTradeViolationAction implements Action<TradeState, TradeActions> {
		@Override
		public void onTransition(TradeState from, TradeState to, TradeActions cause, Arguments args, StateMachine<TradeState, TradeActions> machine) {
			if (args.getNumberOfArguments() > 0) {
				Trade trade = (Trade) args.getFirst();
				throw new IllegalStateException("Tried to add an item to a rejected trade in state " + trade.getCurrentState());
			}
		}

	}
}
