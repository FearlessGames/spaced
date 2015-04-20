package se.spaced.server.net;

import se.fearlessgames.common.util.TimeProvider;
import se.spaced.messages.protocol.c2s.C2SProtocol;
import se.spaced.messages.protocol.c2s.object.MessageObject;
import se.spaced.server.model.action.ActionScheduler;

import java.util.AbstractQueue;
import java.util.Iterator;

public class SmrtEnqueuer extends AbstractQueue<MessageObject> {

	private final ActionScheduler scheduler;
	private final TimeProvider timeProvider;
	private final C2SProtocol receiver;

	public SmrtEnqueuer(ActionScheduler scheduler, TimeProvider timeProvider, C2SProtocol receiver) {
		this.scheduler = scheduler;
		this.timeProvider = timeProvider;
		this.receiver = receiver;
	}

	@Override
	public Iterator<MessageObject> iterator() {
		throw new UnsupportedOperationException("NYI");
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException("NYI");

	}

	@Override
	public boolean offer(MessageObject messageObject) {
		scheduler.add(new SmrtMessageAction(timeProvider.now(), messageObject, receiver));
		return true;
	}

	@Override
	public MessageObject poll() {
		throw new UnsupportedOperationException("NYI");

	}

	@Override
	public MessageObject peek() {
		throw new UnsupportedOperationException("NYI");

	}
}
