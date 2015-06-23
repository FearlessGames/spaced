package se.spaced.client.core;

import com.google.inject.Inject;
import se.spaced.client.net.smrt.MessageQueue;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.scheduler.JobManager;

public class MainTickerServiceImpl implements MainTickerService {
	private final JobManager jobManager;
	private final EventHandler eventHandler;
	private final MessageQueue messageQueue;

	@Inject
	public MainTickerServiceImpl(JobManager jobManager, EventHandler eventHandler, MessageQueue messageQueue) {
		this.jobManager = jobManager;
		this.eventHandler = eventHandler;
		this.messageQueue = messageQueue;
	}

	@Override
	public void tick() {
		jobManager.tick();
		messageQueue.handleIncomingQueue();
		eventHandler.processAsynchEvents();
	}
}