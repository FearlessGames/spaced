package se.spaced.shared.world.area;

import se.spaced.shared.xml.XmlIOException;

public interface PolygonGraphLoader {
	PolygonGraph loadPolygonGraph(String path) throws XmlIOException;
}
