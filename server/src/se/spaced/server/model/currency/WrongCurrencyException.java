package se.spaced.server.model.currency;

public class WrongCurrencyException extends Exception {
	public WrongCurrencyException(PersistedCurrency expected, PersistedCurrency actual) {
		super("Wrong currency: expected " + expected + " got " + actual);
	}
}
