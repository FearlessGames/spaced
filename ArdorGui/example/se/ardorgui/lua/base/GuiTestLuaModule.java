package se.ardorgui.lua.base;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.ardorgui.base.GuiScene;
import se.ardorgui.base.GuiTestModule;
import se.ardorgui.base.GuiUpdater;
import se.ardorgui.lua.bindings.LuaKeyBindings;
import se.fearlessgames.common.io.ClasspathStreamLocator;
import se.fearlessgames.common.lua.LuaVm;
import se.fearlessgames.common.lua.SimpleLuaSourceProvider;

public class GuiTestLuaModule extends GuiTestModule {
	private final String luaFile;

	public GuiTestLuaModule(Class<? extends GuiScene> exampleClass,
			 Class<? extends GuiUpdater> updaterClass,
			 String luaFile) {
		super(exampleClass, updaterClass);
		this.luaFile = luaFile;
	}

	@Override
	protected void configure() {
		super.configure();
		bind(String.class).annotatedWith(BindGuiTestLuaFile.class).toInstance(luaFile);
		bind(LuaKeyBindings.class).in(Singleton.class);
	}

	@Provides
	@Named(value = "gui")
	public LuaVm getLuaVm() {
		return new LuaVm(new SimpleLuaSourceProvider(new ClasspathStreamLocator()));
	}
}