package se.spaced.shared.util.argument;

public interface Argument<Type> {
	Type consume(ArgumentValues values);
}

