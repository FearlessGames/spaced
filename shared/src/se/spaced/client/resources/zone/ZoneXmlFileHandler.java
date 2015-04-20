package se.spaced.client.resources.zone;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.shared.resources.zone.Zone;
import se.spaced.shared.xml.XmlIO;
import se.spaced.shared.xml.XmlIOException;

@Singleton
public class ZoneXmlFileHandler implements ZoneXmlReader, ZoneXmlWriter {
	private final XmlIO xmlIO;
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	public ZoneXmlFileHandler(XmlIO xmlIO) {
		this.xmlIO = xmlIO;
	}

	@Override
	public Zone loadRootZone(String zoneFile) {
		return loadSubZones(zoneFile, null);
	}

	private Zone loadSubZones(String zoneFile, Zone parentZone) {
		try {
			Zone zone = xmlIO.load(Zone.class, zoneFile);
			zone.setFilename(zoneFile);

			if (parentZone != null) {
				parentZone.addSubZone(zone);
				zone.setParentZone(parentZone);
			}

			for (String fileName : zone.getSubzoneFiles()) {
				loadSubZones(fileName, zone);
			}

			return zone;
		} catch (XmlIOException e) {
			log.error("Could not load zones " + zoneFile, e);
		}
		return null;
	}

	@Override
	public void saveZone(String zoneFile, Zone zone) {
		try {
			xmlIO.save(zone, zoneFile);
			saveSubZones(zone);
		} catch (XmlIOException e) {
			log.error("Failed to save zone " + zone, e);
		}
	}

	private void saveSubZones(Zone zone) throws XmlIOException {
		for (Zone subZone : zone.getSubzones()) {
			String fileName = subZone.getFilename();
			saveZone(fileName, subZone);
		}
	}
}
