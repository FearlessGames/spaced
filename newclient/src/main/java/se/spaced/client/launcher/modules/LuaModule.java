package se.spaced.client.launcher.modules;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.fearlessgames.common.lua.LuaVm;
import se.fearlessgames.common.lua.SimpleLuaSourceProvider;
import se.krka.kahlua.require.LuaSourceProvider;
import se.spaced.client.ardor.effect.LuaEffectApi;
import se.spaced.client.ardor.effect.ParticleEffect;
import se.spaced.client.ardor.effect.ParticleEffectPrototype;
import se.spaced.client.ardor.effect.SoundEffect;
import se.spaced.client.ardor.effect.SoundEffectPrototype;

public final class LuaModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(LuaSourceProvider.class).to(SimpleLuaSourceProvider.class);
	}

	@Provides
	@Singleton
	@Named("effects")
	public LuaVm getLuaFormatLoader(LuaEffectApi api, LuaSourceProvider luaSourceProvider) {
		LuaVm luaVm = new LuaVm(luaSourceProvider);
		luaVm.exposeClass(ParticleEffectPrototype.Builder.class);
		luaVm.exposeClass(ParticleEffect.Builder.class);
		luaVm.exposeClass(SoundEffectPrototype.Builder.class);
		luaVm.exposeClass(SoundEffect.Builder.class);
		luaVm.exposeGlobalFunctions(api);
		return luaVm;
	}

	@Provides
	@Singleton
	@Named("gui")
	public LuaVm getGuiLuaVm(LuaSourceProvider luaSourceProvider) {
		return new LuaVm(luaSourceProvider);
	}
}