package se.spaced.shared.events;

public interface EventHandler {

	void subscribe(String event, Object listenerFunction);

	boolean unsubscribe(String event, Object listenerFunction);

	boolean unsubscribe(String event);

	void fireEvent(String event, Object... params);

	<T extends Enum<T>> void fireEvent(T enumeration, Object... params);

	void fireAsynchEvent(String event, Object... params);

	<T extends Enum<T>> void fireAsynchEvent(T enumeration, Object... params);

	void reset();

	void processAsynchEvents();
}
