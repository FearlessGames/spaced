package se.spaced.server.mob.brains;

import se.krka.kahlua.vm.KahluaTable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class ScriptedMobEventProxyHandler implements InvocationHandler {
	private final KahluaTable eventMap;
	private volatile List<Event> events = new ArrayList<Event>();

	ScriptedMobEventProxyHandler(KahluaTable eventMap) {
		this.eventMap = eventMap;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (eventMap != null) {
			String eventName = method.getName();
			Object handler = eventMap.rawget(eventName);
			if (handler != null) {
				synchronized (events) {
					events.add(new Event(handler, args));
				}
			}

		}
		return null;
	}

	public void executeEvents(MobScriptEnvironment scriptEnv) {
		if (events.isEmpty()) {
			List<Event> localEvents = events;
			events = new ArrayList<Event>();
			for (Event event : localEvents) {
				scriptEnv.getVm().luaCall(event.handler, event.args);
			}
		}
	}

	private static class Event {
		private final Object handler;
		private final Object[] args;

		public Event(Object handler, Object[] args) {
			this.handler = handler;
			this.args = args;
		}
	}
}
