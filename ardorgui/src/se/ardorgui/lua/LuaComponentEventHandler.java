package se.ardorgui.lua;

import se.fearlessgames.common.lua.LuaVm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuaComponentEventHandler {
    private final Map<String, List<Object>> eventHandlers = new HashMap<String, List<Object>>();
	private final LuaVm luaVm;

	public LuaComponentEventHandler(LuaVm luaVm) {
        this.luaVm = luaVm;
	}

	public void addEventListener(String event, Object functionObject) {
		List<Object> functionObjects = eventHandlers.get(event);
		if (functionObjects == null) {
			functionObjects = new ArrayList<Object>();
			eventHandlers.put(event, functionObjects);
		}
		functionObjects.add(functionObject);
	}

	public void removeListener(String event) {
		eventHandlers.remove(event);
	}

	public void removeListener(String event, Object functionObject) {
		List<Object> functionObjects = eventHandlers.get(event);
		if (functionObjects != null) {
			functionObjects.remove(functionObject);
			if (functionObjects.isEmpty()) {
				eventHandlers.remove(event);
			}
		}
	}
	

	public void runScript(String event, Object... params) {
		List<Object> functionObjects = eventHandlers.get(event);
		if (functionObjects != null) {
			for (Object functionObject : functionObjects) {
				luaVm.luaCall(functionObject, params);
			}
		}
	}
}