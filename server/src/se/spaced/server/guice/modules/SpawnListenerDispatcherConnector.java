package se.spaced.server.guice.modules;

import com.google.inject.Inject;
import se.spaced.server.model.spawn.SpawnListener;
import se.spaced.server.model.vendor.VendorService;
import se.spaced.shared.util.ListenerDispatcher;

public class SpawnListenerDispatcherConnector {

	@Inject
	public SpawnListenerDispatcherConnector(ListenerDispatcher<SpawnListener> listenerDispatcher, VendorService vendorService) {
		listenerDispatcher.addListener(vendorService);
	}
}
