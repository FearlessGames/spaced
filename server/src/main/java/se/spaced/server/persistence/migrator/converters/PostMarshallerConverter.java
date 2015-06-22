package se.spaced.server.persistence.migrator.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class PostMarshallerConverter<T> implements Converter {
	private final Class<T> convertClass;
	private final Converter baseConverter;
	private final PostUnmarshal[] postUnmarshals;


	public PostMarshallerConverter(Class<T> convertClass, Converter baseConverter, PostUnmarshal... postUnmarshals) {
		this.convertClass = convertClass;
		this.baseConverter = baseConverter;
		this.postUnmarshals = postUnmarshals;
	}

	@Override
	public void marshal(
			Object o,
			HierarchicalStreamWriter hierarchicalStreamWriter,
			MarshallingContext marshallingContext) {
		baseConverter.marshal(o, hierarchicalStreamWriter, marshallingContext);
	}

	@Override
	public Object unmarshal(
			HierarchicalStreamReader hierarchicalStreamReader,
			UnmarshallingContext unmarshallingContext) {
		T base = (T) baseConverter.unmarshal(hierarchicalStreamReader, unmarshallingContext);
		for (PostUnmarshal postUnmarshal : postUnmarshals) {
			postUnmarshal.postUnmarshal(base);
		}

		return base;

	}

	@Override
	public boolean canConvert(Class aClass) {
		return convertClass == aClass;
	}
}
