package se.spaced.server.model.currency;

import com.google.common.base.Preconditions;
import se.spaced.shared.model.Money;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class PersistedMoney {
	public static final PersistedMoney ZERO = new PersistedMoney(PersistedCurrency.NONE, 0);

	@ManyToOne
	private final PersistedCurrency currency;

	private final long amount;

	PersistedMoney() {
		this(PersistedCurrency.NONE, 0L);
	}

	public PersistedMoney(PersistedCurrency currency, long amount) {
		Preconditions.checkNotNull(currency, "currency can't be null");
		this.currency = currency;
		this.amount = amount;
	}

	public PersistedCurrency getCurrency() {
		return currency;
	}

	public long getAmount() {
		return amount;
	}

	public PersistedMoney add(PersistedMoney other) throws WrongCurrencyException {
		checkCurrency(other);
		return new PersistedMoney(currency, amount + other.amount);
	}

	public PersistedMoney subtract(PersistedMoney other) throws WrongCurrencyException {
		checkCurrency(other);
		return new PersistedMoney(currency, amount - other.amount);
	}

	private void checkCurrency(PersistedMoney other) throws WrongCurrencyException {
		if (!other.currency.equals(currency)) {
			throw new WrongCurrencyException(currency, other.currency);
		}
	}

	public boolean isZero() {
		return amount == 0;
	}

	@Override
	public String toString() {
		return amount + " " + currency.getName();
	}

	public Money asMoney() {
		return new Money(getAmount(), getCurrency().getName());
	}
}
