package se.spaced.shared.util.argument;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static se.spaced.shared.util.argument.ArgumentParser.DOUBLE_ARG;
import static se.spaced.shared.util.argument.ArgumentParser.INT_ARG;
import static se.spaced.shared.util.argument.ArgumentParser.STRING_ARG;
import static se.spaced.shared.util.argument.ArgumentParser.STRING_REST_ARG;

public class ArgumentParserTest {

	@Test
	public void testInteger1() {
		String input = "1123  1456";
		ArgumentParser<Integer, Integer, NA, NA, NA, NA> tokenizer = ArgumentParser.get(INT_ARG, INT_ARG);
		ArgumentValues<Integer, Integer, NA, NA, NA, NA> values = tokenizer.consume(input);

		assertEquals(1123, values.a.intValue());
		assertEquals(1456, values.b.intValue());
	}

	@Test
	public void testInteger2() {
		String input = "1123,  1456";
		ArgumentParser<Integer, Integer, NA, NA, NA, NA> tokenizer = ArgumentParser.get(INT_ARG, INT_ARG);
		ArgumentValues<Integer, Integer, NA, NA, NA, NA> values = tokenizer.consume(input);

		assertEquals(1123, values.a.intValue());
		assertEquals(1456, values.b.intValue());
	}

	@Test
	public void testDouble1() {
		String input = "1123  1456";
		ArgumentParser<Double, Double, NA, NA, NA, NA> tokenizer = ArgumentParser.get(DOUBLE_ARG, DOUBLE_ARG);
		ArgumentValues<Double, Double, NA, NA, NA, NA> values = tokenizer.consume(input);

		assertEquals(1123.0, values.a.doubleValue(), 2);
		assertEquals(1456.0, values.b.doubleValue(), 2);
	}

	@Test
	public void testDouble2() {
		String input = "1123.125,  1456.125";
		ArgumentParser<Double, Double, NA, NA, NA, NA> tokenizer = ArgumentParser.get(DOUBLE_ARG, DOUBLE_ARG);
		ArgumentValues<Double, Double, NA, NA, NA, NA> values = tokenizer.consume(input);

		assertEquals(1123.125, values.a.doubleValue(), 2);
		assertEquals(1456.125, values.b.doubleValue(), 2);
	}

	@Test
	public void testString1() {
		String input = "Hello, world! what is this?";
		ArgumentParser<String, String, String, NA, NA, NA> tokenizer = ArgumentParser.get(STRING_ARG, STRING_ARG, STRING_REST_ARG);
		ArgumentValues<String, String, String, NA, NA, NA> values = tokenizer.consume(input);

		assertEquals("Hello", values.a);
		assertEquals("world!", values.b);
		assertEquals("what is this?", values.c);
	}

	@Test
	public void testMixed1() {
		String input = "Hello, 51124.25,  world 123";
		ArgumentParser<String, Double, String, Integer, NA, NA> tokenizer = ArgumentParser.get(STRING_ARG, DOUBLE_ARG, STRING_ARG, INT_ARG);
		ArgumentValues<String, Double, String, Integer, NA, NA> values = tokenizer.consume(input);

		assertEquals("Hello", values.a);
		assertEquals(51124.25, values.b.doubleValue(), 2);
		assertEquals("world", values.c);
		assertEquals(123, values.d.intValue());
	}


	@Test
	public void testFail1() {
		String input = "Hello, world what is this";
		ArgumentParser<String, String, Integer, NA, NA, NA> tokenizer = ArgumentParser.get(STRING_ARG, STRING_ARG, INT_ARG);
		ArgumentValues values = tokenizer.consume(input);

		assertEquals("Hello", values.a);
		assertEquals("world", values.b);
		assertEquals(null, values.c);
	}

	@Test
	public void testFail2() {
		String input = "Hello, world";
		ArgumentParser<String, String, String, NA, NA, NA> tokenizer = ArgumentParser.get(STRING_ARG, STRING_ARG, STRING_ARG);
		ArgumentValues values = tokenizer.consume(input);

		assertEquals("Hello", values.a);
		assertEquals("world", values.b);
		assertEquals(null, values.c);
	}


	@Test
	public void testEmptyRest() {
		String input = "Hello, world";
		ArgumentParser<String, String, String, NA, NA, NA> tokenizer = ArgumentParser.get(STRING_ARG, STRING_ARG, STRING_REST_ARG);
		ArgumentValues values = tokenizer.consume(input);

		assertEquals("Hello", values.a);
		assertEquals("world", values.b);
		assertEquals("", values.c);
	}
}
