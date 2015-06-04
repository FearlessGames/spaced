package se.spaced.shared.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import se.ardortech.math.SpacedVector3;

public class Vector3Converter implements Converter {
	private static final String X = "x";
	private static final String Y = "y";
	private static final String Z = "z";

	@Override
	public void marshal(final Object o, final HierarchicalStreamWriter writer, final MarshallingContext contex) {
		final SpacedVector3 vector = (SpacedVector3) o;
		writer.addAttribute(X, Double.toString(vector.getX()));
		writer.addAttribute(Y, Double.toString(vector.getY()));
		writer.addAttribute(Z, Double.toString(vector.getZ()));
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext contex) {
		if (reader.hasMoreChildren()) {
			return handleNodes(reader);
		} else {
			return handleAttributes(reader);
		}
	}


	private SpacedVector3 handleNodes(HierarchicalStreamReader reader) {
		reader.moveDown();
		final double x = Double.valueOf(reader.getValue());
		reader.moveUp();
		reader.moveDown();
		final double y = Double.valueOf(reader.getValue());
		reader.moveUp();
		reader.moveDown();
		final double z = Double.valueOf(reader.getValue());
		reader.moveUp();
		return new SpacedVector3(x, y, z);
	}

	private SpacedVector3 handleAttributes(HierarchicalStreamReader reader) {
		final double x = Double.valueOf(reader.getAttribute(X));
		final double y = Double.valueOf(reader.getAttribute(Y));
		final double z = Double.valueOf(reader.getAttribute(Z));
		return new SpacedVector3(x, y, z);
	}

	@Override
	public boolean canConvert(final Class aClass) {
		return aClass.equals(SpacedVector3.class);
	}
}
