package se.spaced.shared.network.protocol.codec;

import se.smrt.core.remote.DefaultReadCodec;
import se.smrt.core.remote.DefaultWriteCodec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;

public class EnumSetCodec {
	public void writeEnumSet(
			DefaultWriteCodec codec,
			OutputStream output,
			EnumSet<?> enumSet) throws IOException {


		String className = enumSet.getClass().toString();

		if (className.endsWith("java.util.RegularEnumSet")) {
			writeRegularEnumSet(codec, output, enumSet);
		} else if (className.endsWith("java.util.JumboEnumSet")) {
			writeJumboEnumSet(codec, output, enumSet);
		} else {
			throw new RuntimeException("Unknown EnumSet type, was " + className);
		}


	}

	private void writeRegularEnumSet(
			DefaultWriteCodec codec,
			OutputStream output,
			EnumSet<?> enumSet) throws IOException {

		long elements = (Long) getEnumSetElementsFieldValue(enumSet);
		codec.writeLong(output, elements);
	}

	private void writeJumboEnumSet(
			DefaultWriteCodec codec,
			OutputStream output,
			EnumSet<?> enumSet) throws IOException {

		long[] elements = (long[]) getEnumSetElementsFieldValue(enumSet);
		codec.writeInt(output, elements.length);
		for (long element : elements) {
			codec.writeLong(output, element);
		}
	}


	private Object getEnumSetElementsFieldValue(EnumSet<?> enumSet) {
		try {
			return getEnumSetElementsField(enumSet).get(enumSet);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Unable to access elements field in JumboEnumSet");
		}
	}

	private Field getEnumSetElementsField(EnumSet<?> enumSet) {
		try {
			Field elementsField = enumSet.getClass().getDeclaredField("elements");
			elementsField.setAccessible(true);
			return elementsField;
		} catch (NoSuchFieldException nfe) {
			throw new RuntimeException("Unable to find elements field on enumSet");
		}
	}


	public <E extends Enum<E>> EnumSet<E> readEnumSet(
			DefaultReadCodec codec,
			InputStream input,
			Class<E> enumClass) throws IOException {

		Enum<?>[] enumConstants = enumClass.getEnumConstants();

		if (enumConstants.length <= 64) {
			return readRegularEnumSet(codec, input, enumClass);
		} else {
			return readJumboEnumSet(codec, input, enumClass);
		}

	}


	private <E extends Enum<E>> EnumSet<E> readRegularEnumSet(
			DefaultReadCodec codec,
			InputStream input,
			Class<E> enumClass) throws IOException {

		long value = codec.readLong(input);

		return createAndUpdateEnumSet(enumClass, value);
	}


	private <E extends Enum<E>> EnumSet<E> readJumboEnumSet(
			DefaultReadCodec codec,
			InputStream input,
			Class<E> enumClass) throws IOException {

		int length = codec.readInt(input);
		long[] values = new long[length];
		for (int i = 0; i < length; i++) {
			values[i] = codec.readLong(input);
		}

		EnumSet<E> enumSet = createAndUpdateEnumSet(enumClass, values);
		try {
			Method recalculateSizeMethod = enumSet.getClass().getDeclaredMethod("recalculateSize");
			recalculateSizeMethod.setAccessible(true);
			recalculateSizeMethod.invoke(enumSet);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException("Unable invoke recalculateSize size on jumboEnumSet", e);
		}

		return enumSet;


	}

	private <E extends Enum<E>> EnumSet<E> createAndUpdateEnumSet(Class<E> enumClass, Object value) {
		EnumSet<E> enumSet = EnumSet.noneOf(enumClass);
		try {
			Field field = getEnumSetElementsField(enumSet);
			field.set(enumSet, value);
			return enumSet;
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Unable to set field value to jumboEnumSet");
		}
	}

}
