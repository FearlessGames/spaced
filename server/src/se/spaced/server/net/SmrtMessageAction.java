package se.spaced.server.net;

import se.spaced.messages.protocol.c2s.C2SProtocol;
import se.spaced.messages.protocol.c2s.object.MessageObject;
import se.spaced.server.model.action.Action;

public class SmrtMessageAction extends Action {
	private final C2SProtocol receiver;
	private final MessageObject messageObject;

	public SmrtMessageAction(long executionTime, MessageObject messageObject, C2SProtocol receiver) {
		super(executionTime);
		this.messageObject = messageObject;
		this.receiver = receiver;
	}

	@Override
	public void perform() {
		messageObject.invoke(receiver);
	}
}
