package se.spaced.server.model.currency;

public class MoneyUnderflowException extends Exception {
	private final PersistedMoney money;
	private final PersistedMoney operand;

	public MoneyUnderflowException(String operation, PersistedMoney money, PersistedMoney operand) {
		super(operation + " on " + money + " with " + operand + " was negative");
		this.money = money;
		this.operand = operand;
	}

	public PersistedMoney getMoney() {
		return money;
	}

	public PersistedMoney getOperand() {
		return operand;
	}
}
