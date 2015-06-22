package se.spaced.server.model.currency;

import com.google.common.collect.ImmutableMap;
import se.spaced.server.model.ServerEntity;

public interface MoneyService {
	Wallet createWallet(ServerEntity entity);

	void awardMoney(ServerEntity receiver, PersistedMoney money);

	void notifyEntity(ServerEntity entity);

	ImmutableMap<String, PersistedCurrency> getCurrencies();

	void subtractMoney(PersistedMoney money, ServerEntity owner) throws MoneyUnderflowException;
}
