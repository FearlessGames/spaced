package se.spaced.server.persistence.migrator.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;
import se.spaced.server.persistence.dao.interfaces.Persistable;

import java.util.ArrayList;
import java.util.List;

public class CachingBaseConverter implements Converter {
	private final Converter baseConverter;
	private final NamedObjectCache<String> namedObjectCache;
	private final List<Converter> subConverters = new ArrayList<Converter>();


	public CachingBaseConverter(Converter baseConverter, NamedObjectCache<String> namedObjectCache) {
		this.baseConverter = baseConverter;
		this.namedObjectCache = namedObjectCache;
	}

	public void addSubConveter(Converter converter) {
		subConverters.add(converter);
	}

	@Override
	public void marshal(Object o, HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext) {
		Converter converter = getConverterForType(o.getClass());
		converter.marshal(o, hierarchicalStreamWriter, marshallingContext);
	}

	private Converter getConverterForType(Class aClass) {
		for (Converter subConverter : subConverters) {
			if (subConverter.canConvert(aClass)) {
				return subConverter;
			}
		}
		return baseConverter;
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
		Converter converter = getConverterForType(unmarshallingContext.getRequiredType());

		Object object = converter.unmarshal(hierarchicalStreamReader, unmarshallingContext);

		if (object instanceof NamedPersistable) {
			String nodeName = hierarchicalStreamReader.getNodeName();
			String name = ((NamedPersistable) object).getName();
			if (name != null && !name.isEmpty()) {
				namedObjectCache.addCacheReference(nodeName, name, object);
			}
		}

		return object;

	}

	@Override
	public boolean canConvert(Class aClass) {
		return Persistable.class.isAssignableFrom(aClass);
	}
}
