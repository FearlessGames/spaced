package se.spaced.server.tools.spawnpattern.presenter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import se.spaced.shared.world.area.Geometry;

public class GeometryFactory {
	private final XStream xstream;

	public GeometryFactory(XStream xstream) {
		this.xstream = xstream;
	}

	@SuppressWarnings("unchecked")
	<T extends Geometry> T getGeometryFromContent(String content) throws GeometryException {
		try {
			return (T) xstream.fromXML(content);
		} catch (StreamException se) {
			throw new GeometryException("Faulty Geometry XML");
		} catch (ClassCastException cce) {
			throw new GeometryException("Wrong type of Geometry XML");
		}
	}
}
