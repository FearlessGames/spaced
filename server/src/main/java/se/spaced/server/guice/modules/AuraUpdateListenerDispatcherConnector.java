package se.spaced.server.guice.modules;

import com.google.inject.Inject;
import se.spaced.server.model.aura.AuraUpdateBroadcaster;
import se.spaced.server.model.aura.AuraUpdateListener;
import se.spaced.shared.util.ListenerDispatcher;

public class AuraUpdateListenerDispatcherConnector {

	@Inject
	public AuraUpdateListenerDispatcherConnector(ListenerDispatcher<AuraUpdateListener> listenerDispatcher,
																AuraUpdateBroadcaster auraUpdateListener) {
		listenerDispatcher.addListener(auraUpdateListener);
	}
}
