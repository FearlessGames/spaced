package se.spaced.server.persistence.migrator.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.hibernate.collection.internal.PersistentSet;

import java.util.HashSet;
import java.util.Set;

public class HibernateSetConverter implements Converter {

	@Override
	public void marshal(
			Object o, HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext) {
		PersistentSet set = (PersistentSet) o;
		Set<?> plainSet = new HashSet(set);
		marshallingContext.convertAnother(plainSet);
	}

	@Override
	public Object unmarshal(
			HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
		Set<?> set = new HashSet();
		unmarshallingContext.convertAnother(set, Set.class);
		return set;
	}

	@Override
	public boolean canConvert(Class aClass) {
		return aClass == PersistentSet.class;
	}
}
