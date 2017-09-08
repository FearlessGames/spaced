package se.spaced.server.persistence.util;

import com.google.common.collect.Maps;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.AbstractMap;
import java.util.EnumMap;
import java.util.Map;

public class EnumDoubleMapConverter<K extends Enum<K>> implements Converter {


	private final Class<K> enumClass;

	EnumDoubleMapConverter(Class<K> enumClass) {
		this.enumClass = enumClass;
	}

	@Override
	public boolean canConvert(Class clazz) {
		return EnumMap.class.isAssignableFrom(clazz);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		Map<K, Double> map = (AbstractMap<K, Double>) value;
		for (Map.Entry<K, Double> entry : map.entrySet()) {
			writer.startNode(entry.getKey().toString());
			writer.setValue(entry.getValue().toString());
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Map<K, Double> map = Maps.newEnumMap(enumClass);

		while (reader.hasMoreChildren()) {
			reader.moveDown();
			String nodeName = reader.getNodeName();
			K key = Enum.valueOf(enumClass, nodeName);

			String value = reader.getValue();
			Double doubleValue = Double.valueOf(value);
			map.put(key, doubleValue);
			reader.moveUp();
		}
		return map;
	}
}
