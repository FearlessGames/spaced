package se.spaced.server.persistence.util;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import se.fearless.common.stats.StatType;
import se.spaced.shared.model.stats.SpacedStatType;

class StatTypeConverter extends AbstractSingleValueConverter {
	@Override
	public boolean canConvert(Class type) {
		return StatType.class.isAssignableFrom(type);
	}

	@Override
	public Object fromString(String str) {
		return SpacedStatType.valueOf(str);
	}
}
