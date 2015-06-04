package se.spaced.shared.events;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.fearlessgames.common.lua.LuaVm;
import se.krka.kahlua.integration.annotations.Desc;
import se.krka.kahlua.integration.annotations.LuaMethod;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

@Singleton
public class LuaEventHandler implements EventHandler {
	private final Map<String, Set<Object>> listeners;

	private final ConcurrentLinkedQueue<AsynchEvent> asynchEvents;

	private final LuaVm luaVm;

	@Inject
	public LuaEventHandler(@Named("gui") LuaVm luaVm) {
		this.luaVm = luaVm;
		listeners = new HashMap<String, Set<Object>>();
		asynchEvents = new ConcurrentLinkedQueue<AsynchEvent>();
	}

	@Override
	@LuaMethod(global = true, name = "RegisterEvent")
	public void subscribe(
			String event, @Desc("The Lua closure that should listen to the event") Object listenerFunction) {
		Set<Object> listenersForThisEvent = listeners.get(event);
		if (listenersForThisEvent == null) {
			listenersForThisEvent = new HashSet<Object>();
			listeners.put(event, listenersForThisEvent);
		}
		listenersForThisEvent.add(listenerFunction);
	}

	@Desc("true if it was removed")
	@LuaMethod(global = true, name = "UnregisterEvent")
	@Override
	public boolean unsubscribe(
			String event, @Desc("The Lua closure that should stop listening to the event") Object listenerFunction) {
		Collection<Object> listenersForThisEvent = listeners.get(event);
		if (listenersForThisEvent == null) {
			return false;
		}
		return listenersForThisEvent.remove(listenerFunction);
	}

	@Override
	@LuaMethod(global = true, name = "UnregisterAllEvents")
	public boolean unsubscribe(
			String event) {
		return listeners.remove(event) != null;
	}

	@Override
	@LuaMethod(global = true, name = "FireGlobalEvent")
	public void fireEvent(String event, Object... params) {
		Collection<Object> listenersForThisEvent = listeners.get(event);
		if (listenersForThisEvent == null) {
			return;
		}

		Object[] params2 = new Object[params.length + 1];
		params2[0] = event;
		System.arraycopy(params, 0, params2, 1, params.length);
		for (Object listener : listenersForThisEvent) {
			luaVm.luaCall(listener, params2);
		}
	}


	@Override
	public <T extends Enum<T>> void fireEvent(T enumeration, Object... params) {
		fireEvent(enumeration.name(), params);
	}

	@Override
	public void fireAsynchEvent(String event, Object... params) {
		asynchEvents.add(new AsynchEvent(event, params));
	}

	@Override
	public <T extends Enum<T>> void fireAsynchEvent(T enumeration, Object... params) {
		asynchEvents.add(new AsynchEvent(enumeration.name(), params));
	}

	@Override
	public void reset() {
		listeners.clear();
		asynchEvents.clear();
	}

	@Override
	public void processAsynchEvents() {
		while (!asynchEvents.isEmpty()) {
			AsynchEvent event = asynchEvents.remove();
			fireEvent(event.event, event.params);
		} 
	}

	private static class AsynchEvent {
		private final String event;
		private final Object[] params;

		public AsynchEvent(String event, Object[] params) {
			this.event = event;
			this.params = params;
		}
	}

}
