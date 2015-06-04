package se.ardorgui.lua.bindings;

import com.ardor3d.input.Key;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.ardortech.input.KeyListener;
import se.fearlessgames.common.lua.LuaVm;
import se.krka.kahlua.integration.annotations.LuaMethod;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Singleton
public class LuaKeyBindings implements KeyListener {
	private final LuaVm luaVm;

	private final Map<Key, Object> keyPressedBinds;
	private final Map<Key, Object> keyReleasedBinds;
	private final Set<Key> pressedKeys;

	@Inject
	public LuaKeyBindings(@Named("gui") LuaVm luaVm) {
		this.luaVm = luaVm;
		keyPressedBinds = new EnumMap<Key, Object>(Key.class);
		keyReleasedBinds = new EnumMap<Key, Object>(Key.class);
		pressedKeys = EnumSet.noneOf(Key.class);
	}

	@LuaMethod(global = true, name = "OnKeyDown")
	public void setKeyDownAction(String keyString, final Object action) {
		keyPressedBinds.put(Key.valueOf(keyString), action);
	}

	@LuaMethod(global = true, name = "OnKeyUp")
	public void setKeyUpAction(String keyString, final Object action) {
		keyReleasedBinds.put(Key.valueOf(keyString), action);
	}



	public void clearBinds() {
		keyPressedBinds.clear();
		keyReleasedBinds.clear();
	}

	@Override
	public boolean onKey(char character, Key keyCode, boolean pressed) {
		if (pressed) {
			pressedKeys.add(keyCode);
			Object action = keyPressedBinds.get(keyCode);
			if (action != null) {
				luaVm.luaCall(action, keyCode.name());
				return true;
			}
		} else {
			pressedKeys.remove(keyCode);
			Object action = keyReleasedBinds.get(keyCode);
			if (action != null) {
				luaVm.luaCall(action, keyCode.name());
				return true;
			}
		}
		return false;
	}

	@LuaMethod(name = "IsButtonDown", global = true)
	public boolean isButtonPressed(String keyString) {
		return pressedKeys.contains(Key.valueOf(keyString));
	}
}