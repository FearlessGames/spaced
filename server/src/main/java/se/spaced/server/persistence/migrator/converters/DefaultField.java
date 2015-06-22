package se.spaced.server.persistence.migrator.converters;

import com.google.common.base.Supplier;

import java.lang.reflect.Field;

public class DefaultField implements PostUnmarshal {
	private final String name;
	private final Supplier<?> value;

	public DefaultField(String name, Supplier<?> value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public void postUnmarshal(Object unmarshaledObject) {
		try {
			final Field field = unmarshaledObject.getClass().getDeclaredField(name);
			field.setAccessible(true);
			if (field.get(unmarshaledObject) == null) {
				field.set(unmarshaledObject, value.get());
			}
		} catch (NullPointerException npe) {
			System.out.println("NPE FAIL!!!");
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
