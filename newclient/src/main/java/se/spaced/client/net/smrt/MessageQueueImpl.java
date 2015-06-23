package se.spaced.client.net.smrt;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.fearlessgames.common.util.TimeProvider;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.messages.protocol.s2c.object.MessageObject;

import java.util.Queue;

@Singleton
public class MessageQueueImpl implements MessageQueue {
	private static final long MAX_HANDLE_TIME = 100L;
	private final Queue<MessageObject> queue;
	private final TimeProvider timeProvider;
	private final S2CProtocol receiver;

	@Inject
	public MessageQueueImpl(Queue<MessageObject> queue, TimeProvider timeProvider, @Named("mainThread") S2CProtocol receiver) {
		this.queue = queue;
		this.timeProvider = timeProvider;
		this.receiver = receiver;
	}

	@Override
	public void handleIncomingQueue() {
		long startTime = timeProvider.now();
		while (startTime + MAX_HANDLE_TIME > timeProvider.now() && !queue.isEmpty()) {
			MessageObject messageObject = queue.poll();
			messageObject.invoke(receiver);
		}
	}
}