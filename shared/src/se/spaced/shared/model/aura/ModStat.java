package se.spaced.shared.model.aura;

import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.shared.model.stats.Operator;
import se.spaced.shared.model.stats.StatType;

import java.io.Serializable;

public class ModStat implements Serializable, Comparable<ModStat> {
	private final double amount;
	private final StatType statType;
	private final Operator operator;

	public ModStat(double amount, StatType statType, Operator operator) {
		this.amount = amount;
		this.statType = statType;
		this.operator = operator;
	}

	@LuaMethod(name = "GetValue")
	public double getValue() {
		return amount;
	}

	@LuaMethod(name = "GetStatType")
	public StatType getStatType() {
		return statType;
	}

	@LuaMethod(name = "GetOperator")
	public Operator getOperator() {
		return operator;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ModStat modStat = (ModStat) o;

		if (Double.compare(modStat.amount, amount) != 0) {
			return false;
		}
		if (operator != modStat.operator) {
			return false;
		}
		if (statType != modStat.statType) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		long temp = amount != +0.0d ? Double.doubleToLongBits(amount) : 0L;
		int result = (int) (temp ^ (temp >>> 32));
		result = 31 * result + statType.hashCode();
		result = 31 * result + operator.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "ModStat{" +
				"amount=" + amount +
				", statType=" + statType +
				'}';
	}

	@Override
	public int compareTo(ModStat o) {
		int statEqual = statType.compareTo(o.getStatType());
		if (statEqual == 0) {
			int operEqual = operator.compareTo(o.getOperator());
			if (operEqual == 0) {
				return (int) (amount - o.getValue());
			}
			return operEqual;
		}
		return statEqual;
	}
}
