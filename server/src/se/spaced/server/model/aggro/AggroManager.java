package se.spaced.server.model.aggro;

import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.server.model.ServerEntity;

public interface AggroManager {
	@LuaMethod(name = "GetMostHated")
	ServerEntity getMostHated();

	@LuaMethod(name = "AddHate")
	void addHate(ServerEntity entity, int hate);

	@LuaMethod(name = "GetHate")
	int getHate(ServerEntity entity);

	@LuaMethod(name = "ClearHate")
	void clearHate(ServerEntity entity);

	@LuaMethod(name = "ClearAll")
	void clearAll();

	@LuaMethod(name = "IsAggroWith")
	boolean isAggroWith(ServerEntity enemy);

	@LuaMethod(name = "GetRandomHated")
	ServerEntity getRandomHated(ServerEntity... exclude);

	String dumpAggroDebug();

}
