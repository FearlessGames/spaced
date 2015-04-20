package se.spaced.shared.xml;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import se.fearlessgames.common.util.uuid.UUID;

public class UUIDConverter extends AbstractSingleValueConverter {

	@Override
	public boolean canConvert(Class aClass) {
		return aClass == UUID.class;
	}

	@Override
	public Object fromString(String s) {
		return UUID.fromString(s);
	}
}
