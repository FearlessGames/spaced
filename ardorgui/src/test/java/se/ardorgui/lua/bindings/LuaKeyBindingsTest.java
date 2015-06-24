package se.ardorgui.lua.bindings;

import com.ardor3d.input.Key;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.lua.LuaVm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;

public class LuaKeyBindingsTest {
	private LuaVm luaVm;
	private LuaKeyBindings luaKeyBindings;

	@Before
	public void setup() {
		luaVm = mock(LuaVm.class);
		luaKeyBindings = new LuaKeyBindings(luaVm);
	}

	@Test
	public void testRegisterOnKeyDown() {
		String buttonName = Key.COLON.name();
		Object callback = mock(Object.class);
		luaKeyBindings.setKeyDownAction(buttonName, callback);

		luaKeyBindings.onKey(',', Key.COMMA, true);
		verifyNever().on(luaVm).luaCall(callback, buttonName);

		luaKeyBindings.onKey(',', Key.COLON, false);
		verifyNever().on(luaVm).luaCall(callback, buttonName);

		luaKeyBindings.onKey(':', Key.COLON, true);

		verifyOnce().on(luaVm).luaCall(callback, buttonName);
	}

	@Test
	public void testIsKeyDown() throws Exception {
		String buttonName = Key.COLON.name();
		assertFalse(luaKeyBindings.isButtonPressed(buttonName));

		luaKeyBindings.onKey(',', Key.COLON, true);

		assertTrue(luaKeyBindings.isButtonPressed(buttonName));

		luaKeyBindings.onKey(',', Key.COLON, false);

		assertFalse(luaKeyBindings.isButtonPressed(buttonName));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsKeyDownBadName() throws Exception {
		luaKeyBindings.isButtonPressed("BLARGH");

	}
}
