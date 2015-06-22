package se.spaced.server.mob.brains;

import se.krka.kahlua.integration.LuaReturn;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaClosure;
import se.mockachino.proxy.*;
import se.spaced.messages.protocol.s2c.S2CMultiDispatcher;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.messages.protocol.s2c.ServerChatMessages;
import se.spaced.messages.protocol.s2c.ServerCombatMessages;
import se.spaced.messages.protocol.s2c.adapter.S2CAdapters;
import se.spaced.server.mob.MobDecision;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.model.Mob;
import se.spaced.server.model.aggro.AggroDispatcher;

public class ScriptedMobBrain extends AbstractMobBrain {
	private final MobScriptEnvironment scriptEnv;

	private final S2CProtocol receiver;
	private final Object act;
	private final ScriptedMobEventProxyHandler eventHandler;

	public ScriptedMobBrain(
			Mob mob, MobOrderExecutor orderExecutor, final MobScriptEnvironment scriptEnv, LuaClosure closure) {
		super(mob, orderExecutor);
		this.scriptEnv = scriptEnv;

		KahluaTable eventMap = null;
		Object act = null;

		LuaReturn returns = scriptEnv.getVm().luaCall(closure, mob);
		if (returns.isSuccess()) {
			if (returns.size() >= 1) {
				act = returns.get(0);
				if (returns.size() >= 2) {
					Object events = returns.get(1);
					if (events != null && events instanceof KahluaTable) {
						eventMap = (KahluaTable) events;
					}

				}
			}
		}
		if (act == null || eventMap == null) {
			// fail somehow
		}
		this.act = act;

		eventHandler = new ScriptedMobEventProxyHandler(eventMap);
		ServerCombatMessages proxy = (ServerCombatMessages) ProxyUtil.newProxy(ServerCombatMessages.class,
				eventHandler);
		ServerChatMessages chatProxy = (ServerChatMessages) ProxyUtil.newProxy(ServerChatMessages.class,
				eventHandler);
		AggroDispatcher aggrodispatcher = new AggroDispatcher(mob, mob.getAggroManager());
		receiver = new S2CMultiDispatcher(S2CAdapters.createServerCombatMessages(proxy)).add(aggrodispatcher).add(
				chatProxy);
	}

	@Override
	public MobDecision act(long now) {
		if (act != null) {
			eventHandler.executeEvents(scriptEnv);
			LuaReturn returns = scriptEnv.getVm().luaCall(act, now);
			if (returns.isSuccess() && returns.size() >= 1 && returns.get(0) == Boolean.TRUE) {
				return MobDecision.DECIDED;
			}
		}
		return MobDecision.UNDECIDED;
	}

	@Override
	public S2CProtocol getSmrtReceiver() {
		return receiver;
	}

}
