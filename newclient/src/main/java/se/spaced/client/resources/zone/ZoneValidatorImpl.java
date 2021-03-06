package se.spaced.client.resources.zone;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.io.IOLocator;
import se.spaced.client.model.Prop;
import se.spaced.shared.resources.zone.Zone;

import java.io.IOException;

@Singleton
public class ZoneValidatorImpl implements ZoneValidator {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final IOLocator streamLocator;

	@Inject
	public ZoneValidatorImpl(IOLocator streamLocator) {
		this.streamLocator = streamLocator;
	}

	@Override
	public boolean validateZone(Zone zone) {
		for (Prop p : zone.getProps()) {
			if (!validateProp(p)) {
				log.warn("Failed to validate zone {} could not find prop {}", zone.getName(), p.getXmoFile());
				return false;
			}
		}
		log.debug("Zone {} validated ok", zone.getName());
		return true;
	}

	@Override
	public boolean validateProp(Prop prop) {
		try {
			streamLocator.getByteSource(prop.getXmoFile()).openStream();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
