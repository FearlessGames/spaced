package se.spaced.server.model.currency;

import se.spaced.server.persistence.dao.impl.PersistableBase;

import javax.persistence.Embedded;
import javax.persistence.Entity;

@Entity
public class WalletCompartment extends PersistableBase {
	@Embedded
	private PersistedMoney money;

	WalletCompartment() {
	}

	public WalletCompartment(PersistedMoney money) {
		this.money = money;
	}

	public PersistedMoney getMoney() {
		return money;
	}

	public synchronized PersistedMoney add(PersistedMoney moneyToAdd) throws WrongCurrencyException, MoneyUnderflowException {
		PersistedMoney moneyTemp = money.add(moneyToAdd);
		if (moneyTemp.getAmount() < 0) {
			throw new MoneyUnderflowException("adding", money, moneyToAdd);
		}
		money = moneyTemp;
		return money;
	}

	public synchronized PersistedMoney subtract(PersistedMoney moneyToSubtract) throws WrongCurrencyException, MoneyUnderflowException {
		PersistedMoney moneyTemp = money.subtract(moneyToSubtract);
		if (moneyTemp.getAmount() < 0) {
			throw new MoneyUnderflowException("subtracting", money, moneyToSubtract);
		}
		money = moneyTemp;
		return money;
	}
}
