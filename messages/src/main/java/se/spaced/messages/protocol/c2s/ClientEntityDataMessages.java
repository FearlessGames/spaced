package se.spaced.messages.protocol.c2s;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.Entity;

@SmrtProtocol
public interface ClientEntityDataMessages {
	void whoRequest(Entity requested);

	void requestResurrection();

	void setTarget(Entity newTarget);

	void clearTarget();

	void unstuck();
}