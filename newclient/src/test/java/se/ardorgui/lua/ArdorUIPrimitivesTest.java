package se.ardorgui.lua;

import com.ardor3d.extension.ui.UIComponent;
import com.ardor3d.extension.ui.UIHud;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.renderer.Camera;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.TextureManager;
import se.fearless.common.lua.LuaVm;
import se.mockachino.order.OrderingContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.anyInt;

public class ArdorUIPrimitivesTest {

	private ArdorUIPrimitives ardorUIPrimitives;
	private LuaVm luaVm;

	@Before
	public void setUp() throws Exception {
		UIHud hud = mock(UIHud.class);
		TextureManager textureManager = mock(TextureManager.class);
		luaVm = mock(LuaVm.class);
		DisplaySettings displaySettings = mock(DisplaySettings.class);
		NativeCanvas nativeCanvas = mock(NativeCanvas.class);
		Camera rttCamera = mock(Camera.class);
		ardorUIPrimitives = new ArdorUIPrimitives(1000, 1000, hud, textureManager, luaVm, displaySettings, nativeCanvas, rttCamera);
	}

	@Test
	public void growBeyongMaxSize() throws Exception {
		UIComponent component = new UIComponent() {
		};
		int maxWidth = component.getMaximumLocalComponentWidth();
		int maxHeight = component.getMaximumLocalComponentHeight();

		int contentHeight = component.getContentHeight();
		int contentWidth = component.getContentWidth();
		assertTrue(contentWidth <= maxWidth);
		assertTrue(contentHeight <= maxHeight);
		ardorUIPrimitives.setSize(component, maxWidth + 2, maxHeight + 3);

		assertEquals(maxWidth + 2, component.getContentWidth());
		assertEquals(maxHeight + 3, component.getContentHeight());

	}

	@Test
	public void shrinkSmallerThanMinSize() throws Exception {
		UIComponent component = new UIComponent() {
		};
		int minWidth = component.getMinimumLocalComponentWidth();
		int minHeight = component.getMinimumLocalComponentHeight();

		int contentHeight = component.getContentHeight();
		int contentWidth = component.getContentWidth();
		assertTrue(contentWidth >= minWidth);
		assertTrue(contentHeight >= minHeight);

		ardorUIPrimitives.setSize(component, minWidth - 2, minHeight - 1);

		assertEquals(minWidth -2, component.getContentWidth());
		assertEquals(minHeight -1, component.getContentHeight());

	}

	@Test
	public void resizeComponent() throws Exception {
		UIComponent component = new UIComponent() {
		};
		Object functionObject = mock(Object.class);
		ardorUIPrimitives.addEventListener(component, "OnResize", functionObject);

		ardorUIPrimitives.setSize(component, 100, 200);

		verifyOnce().on(luaVm).luaCall(functionObject, "OnResize", 100, 200);
	}

	@Test
	public void onlyOneResizeEventPerChange() throws Exception {
		UIComponent component = new UIComponent() {
		};
		Object functionObject = mock(Object.class);
		ardorUIPrimitives.addEventListener(component, "OnResize", functionObject);

		ardorUIPrimitives.setSize(component, 100, 200);
		ardorUIPrimitives.setSize(component, 100, 200);
		ardorUIPrimitives.setSize(component, 200, 200);
		ardorUIPrimitives.setSize(component, 200, 200);
		ardorUIPrimitives.setSize(component, 200, 300);

		OrderingContext order = newOrdering();
		order.verify().on(luaVm).luaCall(functionObject, "OnResize", 100, 200);
		order.verify().on(luaVm).luaCall(functionObject, "OnResize", 200, 200);
		order.verify().on(luaVm).luaCall(functionObject, "OnResize", 200, 300);

		verifyExactly(3).on(luaVm).luaCall(functionObject, "OnResize", anyInt(), anyInt());
	}

}
