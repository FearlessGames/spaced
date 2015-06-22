package se.spaced.server.trade;

import com.google.common.collect.UnmodifiableIterator;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.ExchangeResult;
import se.spaced.server.model.items.ItemService;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;

public class TradeExecutor {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final TransactionManager transactionManager;
	private final ItemService itemService;

	public TradeExecutor(TransactionManager transactionManager, ItemService itemService) {
		this.transactionManager = transactionManager;
		this.itemService = itemService;
	}

	ExchangeResult executeTrade(TradeTransaction trade) {
		Transaction transaction = transactionManager.beginTransaction();

		UnmodifiableIterator<ServerItem> initiatorItems = trade.getItemsFromInitiator().iterator();
		UnmodifiableIterator<ServerItem> collaboratorItems = trade.getItemsFromCollaborator().iterator();
		ServerEntity initiator = trade.getInitiator();
		ServerEntity collaborator = trade.getCollaborator();
		while (initiatorItems.hasNext() && collaboratorItems.hasNext()) {
			ExchangeResult exchangeResult = itemService.exchangeItems(initiator,
					initiatorItems.next(),
					collaborator,
					collaboratorItems.next());
			if (exchangeResult != ExchangeResult.SUCCESS) {
				transaction.rollback();
				return exchangeResult;
			}
		}
		while (initiatorItems.hasNext()) {
			ExchangeResult exchangeResult = itemService.transferItem(initiator, collaborator, initiatorItems.next());
			if (exchangeResult != ExchangeResult.SUCCESS) {
				transaction.rollback();
				return exchangeResult;
			}
		}
		while (collaboratorItems.hasNext()) {
			ExchangeResult exchangeResult = itemService.transferItem(collaborator, initiator, collaboratorItems.next());
			if (exchangeResult != ExchangeResult.SUCCESS) {
				transaction.rollback();
				return exchangeResult;
			}
		}
		transaction.commit();
		return ExchangeResult.SUCCESS;
	}

}
