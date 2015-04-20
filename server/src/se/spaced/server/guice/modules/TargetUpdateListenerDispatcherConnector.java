package se.spaced.server.guice.modules;

import com.google.inject.Inject;
import se.spaced.server.model.combat.TargetUpdateBroadcaster;
import se.spaced.server.model.combat.TargetUpdateListener;
import se.spaced.shared.util.ListenerDispatcher;

public class TargetUpdateListenerDispatcherConnector {

	@Inject
	public TargetUpdateListenerDispatcherConnector(ListenerDispatcher<TargetUpdateListener> listenerDispatcher, TargetUpdateBroadcaster targetUpdateBroadcaster) {
		listenerDispatcher.addListener(targetUpdateBroadcaster);
	}
}
