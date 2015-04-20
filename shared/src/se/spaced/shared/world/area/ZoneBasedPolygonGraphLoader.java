package se.spaced.shared.world.area;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.client.model.Prop;
import se.spaced.client.resources.zone.ZoneXmlReader;
import se.spaced.shared.model.xmo.XmoLoader;
import se.spaced.shared.model.xmo.XmoRoot;
import se.spaced.shared.resources.zone.Zone;
import se.spaced.shared.world.walkmesh.Walkmesh;
import se.spaced.shared.xml.XmlIO;
import se.spaced.shared.xml.XmlIOException;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class ZoneBasedPolygonGraphLoader implements PolygonGraphLoader {
	private final ZoneXmlReader zoneXmlReader;
	private final XmoLoader xmoLoader;
	private final XmlIO xmlIO;
	private final Map<String, XmoRoot> xmoCache = new HashMap<String, XmoRoot>();
	private final Map<String, Walkmesh> walkmeshCache = new HashMap<String, Walkmesh>();

	@Inject
	public ZoneBasedPolygonGraphLoader(ZoneXmlReader zoneXmlReader, XmoLoader xmoLoader, XmlIO xmlIO) {
		this.zoneXmlReader = zoneXmlReader;
		this.xmoLoader = xmoLoader;
		this.xmlIO = xmlIO;
	}

	@Override
	public PolygonGraph loadPolygonGraph(String path) throws XmlIOException {
		Zone rootZone = zoneXmlReader.loadRootZone(path);
		PolygonGraph polygonGraph = new PolygonGraph();

		loadZonesXmos(rootZone, polygonGraph);

		return polygonGraph;
	}

	private void loadZonesXmos(Zone zone, PolygonGraph polygonGraph) throws XmlIOException {
		for (Prop prop : zone.getProps()) {
			addWalkMesh(prop, polygonGraph);
		}

		for (Zone subZone : zone.getSubzones()) {
			loadZonesXmos(subZone, polygonGraph);
		}
	}

	private void addWalkMesh(Prop prop, PolygonGraph polygonGraph) throws XmlIOException {
		XmoRoot xmo = loadXmoRoot(prop.getXmoFile());
		String walkmeshFile = xmo.getWalkmeshFile();
		if (walkmeshFile != null) {
			Walkmesh walkmesh = loadWalkMesh(walkmeshFile);
			walkmesh.addToPolygonGraph(prop.getLocation(), prop.getRotation(), polygonGraph);
		}
	}


	private XmoRoot loadXmoRoot(String xmoFile) throws XmlIOException {
		if (xmoCache.containsKey(xmoFile)) {
			return xmoCache.get(xmoFile);
		}

		XmoRoot xmoRoot = xmoLoader.loadXmo(xmoFile);
		xmoCache.put(xmoFile, xmoRoot);
		return xmoRoot;
	}

	private Walkmesh loadWalkMesh(String walkmeshFile) throws XmlIOException {
		if (walkmeshCache.containsKey(walkmeshFile)) {
			return walkmeshCache.get(walkmeshFile);
		}

		Walkmesh walkmesh = xmlIO.load(Walkmesh.class, walkmeshFile);
		walkmeshCache.put(walkmeshFile, walkmesh);
		return walkmesh;
	}


}
