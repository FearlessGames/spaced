package se.spaced.client.resources.zone;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.model.Prop;
import se.spaced.shared.resources.zone.Zone;

import java.util.List;

public class ScenegraphZoneActivationListener implements ZoneActivationListener {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final ScenegraphService scenegraphService;

	@Inject
	public ScenegraphZoneActivationListener(ScenegraphService scenegraphService) {
		this.scenegraphService = scenegraphService;
	}

	@Override
	public void zoneWasLoaded(Zone zone) {
		List<Prop> propList = zone.getProps();
		for (Prop prop : propList) {
			if (prop.getXmoFile() == null) {
				log.error("Prop in " + zone.getFilename() + " is missing xmoFile entry, prop is located at: " + prop.getLocation());
			} else if (prop.getLocation() == null) {
				log.error("Prop " + prop.getXmoFile() + " in " + zone.getFilename() + " is missing location, prop has rotation: " + prop.getRotation());
			} else if (prop.getRotation() == null) {
				log.error("Prop " + prop.getXmoFile() + " in " + zone.getFilename() + " is missing rotation, prop is located at: " + prop.getLocation());
			} else if (prop.getScale() == null) {
				log.error("Prop " + prop.getXmoFile() + " in " + zone.getFilename() + " is missing scale, prop is located at: " + prop.getLocation());
			} else {
				scenegraphService.addProp(prop, zone);
			}
		}

		scenegraphService.attachNode(zone.getNode());


	}

	@Override
	public void zoneWasUnloaded(Zone zone) {
		scenegraphService.removeProps(zone.getProps());
		scenegraphService.detachNode(zone.getNode());
	}
}
