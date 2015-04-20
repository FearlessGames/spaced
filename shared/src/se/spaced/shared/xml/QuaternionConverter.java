package se.spaced.shared.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import se.ardortech.math.SpacedRotation;

public class QuaternionConverter implements Converter {
	private static final String X = "x";
	private static final String Y = "y";
	private static final String Z = "z";
	private static final String W = "w";

	@Override
	public void marshal(final Object o, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		SpacedRotation quaternion = (SpacedRotation) o;
		writer.addAttribute(X, Double.toString(quaternion.getX()));
		writer.addAttribute(Y, Double.toString(quaternion.getY()));
		writer.addAttribute(Z, Double.toString(quaternion.getZ()));
		writer.addAttribute(W, Double.toString(quaternion.getW()));
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		if (reader.hasMoreChildren()) {
			return handleNodes(reader);
		} else {
			return handleAttributes(reader);
		}
	}

	private SpacedRotation handleAttributes(HierarchicalStreamReader reader) {
		if (reader.getAttributeCount() == 0) {
			return SpacedRotation.IDENTITY;
		}

		final double x = Double.valueOf(reader.getAttribute(X));
		final double y = Double.valueOf(reader.getAttribute(Y));
		final double z = Double.valueOf(reader.getAttribute(Z));
		final double w = Double.valueOf(reader.getAttribute(W));
		return new SpacedRotation(x, y, z, w);
	}

	private SpacedRotation handleNodes(HierarchicalStreamReader reader) {
		reader.moveDown();
		double x = Double.valueOf(reader.getValue());
		reader.moveUp();
		reader.moveDown();
		final double y = Double.valueOf(reader.getValue());
		reader.moveUp();
		reader.moveDown();
		final double z = Double.valueOf(reader.getValue());
		reader.moveUp();
		reader.moveDown();
		final double w = Double.valueOf(reader.getValue());
		reader.moveUp();
		return new SpacedRotation(x, y, z, w);
	}

	@Override
	public boolean canConvert(final Class aClass) {
		return SpacedRotation.class.equals(aClass);
	}
}
