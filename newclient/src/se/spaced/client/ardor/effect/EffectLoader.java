package se.spaced.client.ardor.effect;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.fearlessgames.common.lua.LuaVm;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.LuaClosure;
import se.spaced.shared.util.Builder;
import se.spaced.shared.util.random.RandomProvider;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class EffectLoader {
	private final Map<String, EffectPrototype> loadedPrototypes = Maps.newHashMap();
	private final Map<String, LuaClosure> loadedEffectClosures = Maps.newHashMap();
	private final LuaVm luaVm;
	private final RandomProvider random;

	@Inject
	public EffectLoader(@Named("effects") LuaVm luaVm, RandomProvider random) {
		this.luaVm = luaVm;
		this.random = random;
		luaVm.exposeGlobalFunctions(this);
	}

	@LuaMethod(global = true, name = "CreateParticleEffect")
	public ParticleEffect.Builder createParticleEffect(String path) {
		EffectPrototype prototype = loadPrototype("effects/prototypes/particle/" + path);
		return (ParticleEffect.Builder) prototype.createBuilder();
	}

	@LuaMethod(global = true, name = "CreateSoundEffect")
	public SoundEffect.Builder createSoundEffect(String... paths) {
		final String path = paths[random.getInteger(0, paths.length - 1)];
		EffectPrototype prototype = loadPrototype("effects/prototypes/sound/" + path);
		return (SoundEffect.Builder) prototype.createBuilder();
	}

	public synchronized Effect loadEffect(String path, EffectContext effectContext) {
		checkNotNull(effectContext, "effectContext");

		LuaClosure closure = loadClosure(path);
		List<Object> ret = luaVm.runClosure(closure);
		return buildEffect(ret, effectContext);
	}

	public void reset() {
		loadedPrototypes.clear();
		loadedEffectClosures.clear();
	}

	private Effect buildEffect(List<Object> effects, EffectContext effectContext) {
		if (effects.size() == 1) {
			EffectBuilder<? extends Effect> effectBuilder = (EffectBuilder<? extends Effect>) effects.get(0);
			return effectBuilder.buildEffect(effectContext);
		}

		List<Effect> effectList = Lists.newArrayList();
		for (Object effect : effects) {
			EffectBuilder<? extends Effect> effectBuilder = (EffectBuilder<? extends Effect>) effect;
			effectList.add(effectBuilder.buildEffect(effectContext));
		}
		return new CompositeEffect(effectList);
	}

	private synchronized EffectPrototype loadPrototype(String path) {
		if (loadedPrototypes.containsKey(path)) {
			return loadedPrototypes.get(path);
		}

		LuaClosure closure = luaVm.loadClosure(path);
		List<Object> ret = luaVm.runClosure(closure);

		Builder<? extends EffectPrototype> prototypeBuilder = (Builder<? extends EffectPrototype>) ret.get(0);
		EffectPrototype prototype = prototypeBuilder.build();

		loadedPrototypes.put(path, prototype);
		return prototype;
	}

	private synchronized LuaClosure loadClosure(String path) {
		if (loadedEffectClosures.containsKey(path)) {
			return loadedEffectClosures.get(path);
		}

		// A dot in the path indicates that we want to load an effect from a LuaTable
		String[] split = path.split("\\.", 2);

		LuaClosure closure = luaVm.loadClosure("effects/" + split[0]);
		List<Object> ret = luaVm.runClosure(closure);

		LuaClosure returnClosure = null;
		if (split.length == 2) {  //If its asking for a sub entry of an effect file then it loops through all entries to prevent same file loaded multiple times
			KahluaTable table = (KahluaTable) ret.get(0);
			KahluaTableIterator iterator = table.iterator();
			while (iterator.advance()) {
				String key = (String) iterator.getKey();
				LuaClosure entryClosure = (LuaClosure) iterator.getValue();
				loadedEffectClosures.put(split[0] + "." + key, entryClosure);

				if (key.equals(split[1])) {
					returnClosure = entryClosure;
				}
			}
		} else {
			returnClosure = closure;
			loadedEffectClosures.put(path, returnClosure);
		}

		return returnClosure;
	}
}
