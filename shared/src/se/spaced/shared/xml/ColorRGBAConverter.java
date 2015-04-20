package se.spaced.shared.xml;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ColorRGBAConverter implements Converter {
	private static final String R = "r";
	private static final String G = "g";
	private static final String B = "b";
	private static final String A = "a";

	@Override
	public void marshal(final Object o, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		final ReadOnlyColorRGBA color = (ReadOnlyColorRGBA) o;
		writer.addAttribute(R, Float.toString(color.getRed()));
		writer.addAttribute(G, Float.toString(color.getGreen()));
		writer.addAttribute(B, Float.toString(color.getBlue()));
		writer.addAttribute(A, Float.toString(color.getAlpha()));
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		final float r = Float.valueOf(reader.getAttribute(R));
		final float g = Float.valueOf(reader.getAttribute(G));
		final float b = Float.valueOf(reader.getAttribute(B));
		final float a = Float.valueOf(reader.getAttribute(A));
		return new ColorRGBA(r, g, b, a);
	}

	@Override
	public boolean canConvert(final Class aClass) {
		return aClass.equals(ColorRGBA.class) || aClass.equals(ReadOnlyColorRGBA.class);
	}
}
