package se.spaced.server.guice.modules;

import com.google.inject.Inject;
import se.spaced.server.model.entity.AppearanceService;
import se.spaced.shared.util.ListenerDispatcher;

public class AppearanceListenerDispatcherConnector {

	@Inject
	public AppearanceListenerDispatcherConnector(
			ListenerDispatcher<AppearanceService> listenerDispatcher,
			AppearanceService appearanceService) {
		listenerDispatcher.addListener(appearanceService);
	}
}
