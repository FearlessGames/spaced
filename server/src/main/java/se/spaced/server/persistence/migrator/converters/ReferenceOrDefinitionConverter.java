package se.spaced.server.persistence.migrator.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import se.fearless.common.uuid.UUID;
import se.spaced.server.persistence.dao.interfaces.FindableDao;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;

public class ReferenceOrDefinitionConverter<T extends NamedPersistable> implements Converter {
	private static final String REFERENCE = "reference";
	protected final FindableDao<T> dao;
	protected final Converter baseConverter;
	private final NamedObjectCache<String> namedObjectCache;
	private final Class<T> clazz;
	private final PostUnmarshal[] postUnmarshals;

	public ReferenceOrDefinitionConverter(
			FindableDao<T> dao, Converter baseConverter, NamedObjectCache<String> namedObjectCache, Class<T> clazz, PostUnmarshal... postUnmarshals) {
		this.dao = dao;
		this.baseConverter = baseConverter;
		this.namedObjectCache = namedObjectCache;
		this.clazz = clazz;
		this.postUnmarshals = postUnmarshals;

	}

	public static <T extends NamedPersistable> ReferenceOrDefinitionConverter<T> create(
			FindableDao<T> dao, Converter baseConverter, NamedObjectCache<String> namedObjectCache, Class<T> clazz, PostUnmarshal... postUnmarshals) {
		return new ReferenceOrDefinitionConverter<T>(dao, baseConverter, namedObjectCache, clazz, postUnmarshals);
	}

	@Override
	public void marshal(
			Object o, HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext) {
		NamedPersistable named = (NamedPersistable) o;
		String name = named.getName();
		if (name == null) {
			throw new NullPointerException("Can't marshall objects with null name: " + o);
		}
		hierarchicalStreamWriter.addAttribute(REFERENCE, name);
	}

	@Override
	public Object unmarshal(
			HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
		String reference = hierarchicalStreamReader.getAttribute(REFERENCE);

		Object o;
		if (reference == null) {
			o = baseConverter.unmarshal(hierarchicalStreamReader, unmarshallingContext);
		} else {
			o = unmarshalWithReference(reference, hierarchicalStreamReader, unmarshallingContext);
		}

		for (PostUnmarshal postUnmarshal : postUnmarshals) {
			postUnmarshal.postUnmarshal(o);
		}

		return o;
	}

	private Object unmarshalWithReference(String reference, HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {

		try {
			UUID uuid = UUID.fromString(reference);
			return lookupById(uuid);
		} catch (IllegalArgumentException e) {
			try {
				return lookupInCache(hierarchicalStreamReader.getNodeName(), reference);
			} catch (RuntimeException re) {
				return lookupByName(reference);
			}
		}
	}

	private Object lookupInCache(String nodeName, String reference) {
		return namedObjectCache.getCachedReference(nodeName, reference);
	}

	private Object lookupByName(String reference) {
		Object o = dao.findByName(reference);
		if (o == null) {
			throw new RuntimeException("Failed to lookup reference of class " + clazz + " with name " + reference);
		}
		return o;
	}

	private Object lookupById(UUID uuid) {
		return dao.findByPk(uuid);
	}


	@Override
	public boolean canConvert(Class aClass) {
		return aClass == clazz;
	}
}
