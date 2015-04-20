package se.spaced.client.net.messagelisteners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.client.ardor.ui.events.LoadEvents;
import se.spaced.client.resources.zone.LoadListener;
import se.spaced.client.resources.zone.ScenegraphService;
import se.spaced.messages.protocol.c2s.ClientConnectionMessages;
import se.spaced.shared.events.EventHandler;

import java.util.concurrent.atomic.AtomicInteger;

class SceneLoadListener implements LoadListener {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final AtomicInteger maxTasks;
	private final UUID playerId;
	private final ClientConnectionMessages connectionMessages;
	private final EventHandler eventHandler;
	// TODO: remove this parameter
	private final ScenegraphService scenegraphService;

	SceneLoadListener(
			AtomicInteger maxTasks,
			UUID playerId,
			ClientConnectionMessages connection, EventHandler handler, ScenegraphService scenegraphService) {
		this.maxTasks = maxTasks;
		this.playerId = playerId;
		connectionMessages = connection;
		eventHandler = handler;
		this.scenegraphService = scenegraphService;
	}

	@Override
	public void loadCompleted() {
		log.info("loadCompleted");
		if (maxTasks.get() > 0) {
			scenegraphService.setLoadListener(null);
			connectionMessages.loginCharacter(playerId);
		}
	}

	@Override
	public void loadUpdate(int remainingTasks) {
		log.info("loadUpdate {}/{}", remainingTasks, maxTasks.get());
		if (maxTasks.get() < remainingTasks) {
			maxTasks.set(remainingTasks);
		}
		eventHandler.fireAsynchEvent(LoadEvents.LOAD_UPDATE, remainingTasks);
	}
}
