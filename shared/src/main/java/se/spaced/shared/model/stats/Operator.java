package se.spaced.shared.model.stats;

public enum Operator {
	ADD {
		@Override
		public double perform(double oper1, double oper2) {
			return oper1 + oper2;
		}
	},
	POST_MULTIPLY {
		@Override
		public double perform(double oper1, double oper2) {
			return oper1 * oper2;
		}
	};

	public abstract double perform(double oper1, double oper2);
}
