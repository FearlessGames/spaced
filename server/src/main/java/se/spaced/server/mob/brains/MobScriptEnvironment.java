package se.spaced.server.mob.brains;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearless.common.lua.LuaVm;
import se.krka.kahlua.require.LuaSourceProvider;
import se.spaced.server.mob.MobInfoProvider;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.model.Mob;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.aggro.AggroManager;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.SpellBook;
import se.spaced.shared.util.math.interval.IntervalInt;

@Singleton
public class MobScriptEnvironment {
	private final LuaVm vm;

	@Inject
	public MobScriptEnvironment(
			LuaSourceProvider luaSourceProvider, MobOrderExecutor mobOrderExecutor, MobInfoProvider infoProvider) {
		vm = new LuaVm(luaSourceProvider);
		vm.getExposer().exposeGlobalFunctions(mobOrderExecutor);
		vm.getExposer().exposeGlobalFunctions(infoProvider);
		vm.getExposer().exposeClass(ServerEntity.class);
		vm.getExposer().exposeClass(AggroManager.class);
		vm.getExposer().exposeClass(ServerSpell.class);
		vm.getExposer().exposeClass(IntervalInt.class);
		vm.getExposer().exposeClass(Mob.class);
		vm.getExposer().exposeClass(SpellBook.class);

	}

	public LuaVm getVm() {
		return vm;
	}
}
