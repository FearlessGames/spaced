package se.spaced.client.resources.zone;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.ardor.ui.events.ZoneEvents;
import se.spaced.shared.events.LuaEventHandler;
import se.spaced.shared.resources.zone.Zone;

public class ZoneChangedListenerImpl implements ZoneChangedListener {

	private final LuaEventHandler eventHandler;
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	public ZoneChangedListenerImpl(LuaEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	@Override
	public void zoneChanged(Zone old, Zone newZone) {
		eventHandler.fireAsynchEvent(ZoneEvents.ZONE_CHANGED, old, newZone);
	}
}
