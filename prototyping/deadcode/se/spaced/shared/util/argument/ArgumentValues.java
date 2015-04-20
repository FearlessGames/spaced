package se.spaced.shared.util.argument;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentValues<A, B, C, D, E, F> {
	public A a;
	public B b;
	public C c;
	public D d;
	public E e;
	public F f;

	String input;

	public ArgumentValues(String input, ArgumentParser<A, B, C, D, E, F> parser) {
		this.input = input;

		consumeBlanks();
		if ((a = parser.argA.consume(this)) == null) {
			return;
		}


		consumeBlanks();
		if ((b = parser.argB.consume(this)) == null) {
			return;
		}


		consumeBlanks();
		if ((c = parser.argC.consume(this)) == null) {
			return;
		}


		consumeBlanks();
		if ((d = parser.argD.consume(this)) == null) {
			return;
		}


		consumeBlanks();
		if ((e = parser.argE.consume(this)) == null) {
			return;
		}


		consumeBlanks();
		if ((f = parser.argF.consume(this)) == null) {
			return;
		}
	}

	private final Pattern pattern = Pattern.compile("^[ ]*(,|([ ]*))(.*)");

	private void consumeBlanks() {
		Matcher matcher = pattern.matcher(input);
		if (matcher.matches()) {
			input = matcher.group(3);
		}
	}

}

