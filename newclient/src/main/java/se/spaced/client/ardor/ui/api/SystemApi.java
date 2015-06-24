package se.spaced.client.ardor.ui.api;

import com.ardor3d.extension.ui.UIHud;
import com.google.inject.Inject;
import se.fearless.common.time.TimeProvider;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.ReturnValues;

public class SystemApi {

	private final TimeProvider timeProvider;
	private final UIHud uiHud;

	@Inject
	public SystemApi(TimeProvider timeProvider, UIHud uiHud) {
		this.timeProvider = timeProvider;
		this.uiHud = uiHud;
	}


	@LuaMethod(global = true, name = "GetMousePosition")
	public void getMousePosition(ReturnValues ret) {
		ret.push(uiHud.getLastMouseX());
		ret.push(uiHud.getLastMouseY());
	}


	@LuaMethod(global = true, name = "GetTime")
	public long getTime() {
		return timeProvider.now();
	}
}
