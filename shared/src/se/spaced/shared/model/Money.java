package se.spaced.shared.model;

import se.krka.kahlua.integration.annotations.LuaMethod;

public class Money {
	private final long amount;
	private final String currency;

	public Money(long amount, String currency) {
		this.amount = amount;
		this.currency = currency;
	}

	@LuaMethod(name = "GetAmount")
	public long getAmount() {
		return amount;
	}

	@LuaMethod(name = "GetAmountAsString")
	public String getAmountAsString() {
		return String.valueOf(amount);
	}

	@LuaMethod(name = "GetCurrency")
	public String getCurrency() {
		return currency;
	}

	public static final Money ZERO = new Money(0L, "NONE");


}
