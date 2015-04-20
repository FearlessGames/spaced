package se.spaced.shared.model;

public interface ActionTypeVisitor<T, U> {
	T visitHeal(U data);

	T visitMiss(U data);

	T visitWound(U data);

	T visitImmune(U data);
}
