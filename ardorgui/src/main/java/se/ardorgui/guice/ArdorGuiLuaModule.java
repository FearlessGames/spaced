package se.ardorgui.guice;

import com.google.inject.AbstractModule;
import se.fearless.common.lua.SimpleLuaSourceProvider;
import se.krka.kahlua.require.LuaSourceProvider;

//TODO: maybe this and LuaVmModule in server code should be taken out and placed in shared codebase? 
public class ArdorGuiLuaModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(LuaSourceProvider.class).to(SimpleLuaSourceProvider.class);
	}
}