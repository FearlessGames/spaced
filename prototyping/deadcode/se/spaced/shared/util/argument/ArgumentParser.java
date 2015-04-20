package se.spaced.shared.util.argument;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentParser<A, B, C, D, E, F> {
	final Argument<A> argA;
	final Argument<B> argB;
	final Argument<C> argC;
	final Argument<D> argD;
	final Argument<E> argE;
	final Argument<F> argF;

	private ArgumentParser(Argument<A> a, Argument<B> b, Argument<C> c, Argument<D> d, Argument<E> e, Argument<F> f) {
		argA = a;
		argB = b;
		argC = c;
		argD = d;
		argE = e;
		argF = f;
	}

	public static <A, B, C, D, E, F> ArgumentParser<A, B, C, D, E, F> get(Argument<A> a, Argument<B> b, Argument<C> c, Argument<D> d, Argument<E> e, Argument<F> f) {
		return new ArgumentParser<A, B, C, D, E, F>(a, b, c, d, e, f);
	}

	public static <A, B, C, D, E> ArgumentParser<A, B, C, D, E, NA> get(Argument<A> a, Argument<B> b, Argument<C> c, Argument<D> d, Argument<E> e) {
		return get(a, b, c, d, e, NULL_ARG);
	}

	public static <A, B, C, D> ArgumentParser<A, B, C, D, NA, NA> get(Argument<A> a, Argument<B> b, Argument<C> c, Argument<D> d) {
		return get(a, b, c, d, NULL_ARG, NULL_ARG);
	}

	public static <A, B, C> ArgumentParser<A, B, C, NA, NA, NA> get(Argument<A> a, Argument<B> b, Argument<C> c) {
		return get(a, b, c, NULL_ARG, NULL_ARG, NULL_ARG);
	}

	public static <A, B> ArgumentParser<A, B, NA, NA, NA, NA> get(Argument<A> a, Argument<B> b) {
		return get(a, b, NULL_ARG, NULL_ARG, NULL_ARG, NULL_ARG);
	}

	public static <A> ArgumentParser<A, NA, NA, NA, NA, NA> get(Argument<A> a) {
		return get(a, NULL_ARG, NULL_ARG, NULL_ARG, NULL_ARG, NULL_ARG);
	}

	public ArgumentValues<A, B, C, D, E, F> consume(String input) {
		return new ArgumentValues<A, B, C, D, E, F>(input, this);
	}

	public static final Argument<Integer> INT_ARG = new Argument<Integer>() {
		private final Pattern pattern = Pattern.compile("^[ ]*([0-9]+)(.*)$");

		@SuppressWarnings("unchecked")
		@Override
		public Integer consume(ArgumentValues values) {
			Matcher matcher = pattern.matcher(values.input);
			if (!matcher.matches()) {
				return null;
			}
			values.input = matcher.group(2);
			return new Integer(matcher.group(1));
		}
	};

	public static final Argument<Double> DOUBLE_ARG = new Argument<Double>() {
		private final Pattern pattern = Pattern.compile("^[ ]*([0-9]+(\\.[0-9]+)?)(.*)$");

		@SuppressWarnings("unchecked")
		@Override
		public Double consume(ArgumentValues values) {
			Matcher matcher = pattern.matcher(values.input);
			if (!matcher.matches()) {
				return null;
			}
			values.input = matcher.group(3);
			return new Double(matcher.group(1));
		}
	};

	public static final Argument<Float> FLOAT_ARG = new Argument<Float>() {
		private final Pattern pattern = Pattern.compile("^[ ]*([0-9]+(\\.[0-9]+)?)(.*)$");

		@SuppressWarnings("unchecked")
		@Override
		public Float consume(ArgumentValues values) {
			Matcher matcher = pattern.matcher(values.input);
			if (!matcher.matches()) {
				return null;
			}
			values.input = matcher.group(3);
			return new Float(matcher.group(1));
		}
	};

	public static final Argument<NA> NULL_ARG = new Argument<NA>() {
		@Override
		public NA consume(ArgumentValues values) {
			return null;
		}
	};

	public static final Argument<String> STRING_ARG = new Argument<String>() {

		private final Pattern pattern = Pattern.compile("^[ ]*([^, ]+)(.*)$");

		@SuppressWarnings("unchecked")
		@Override
		public String consume(ArgumentValues values) {
			Matcher matcher = pattern.matcher(values.input);
			if (!matcher.matches()) {
				return null;
			}
			values.input = matcher.group(2);
			return matcher.group(1);
		}
	};

	public static final Argument<String> STRING_REST_ARG = new Argument<String>() {

		private final Pattern pattern = Pattern.compile("^[ ]*(.*)$");

		@SuppressWarnings("unchecked")
		@Override
		public String consume(ArgumentValues values) {
			Matcher matcher = pattern.matcher(values.input);
			if (!matcher.matches()) {
				return null;
			}
			values.input = "";
			return matcher.group(1);
		}
	};

}

