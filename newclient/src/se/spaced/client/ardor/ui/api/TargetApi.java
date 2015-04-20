package se.spaced.client.ardor.ui.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.PlayerTabSelector;
import se.spaced.client.model.PlayerTargeting;
import se.spaced.client.model.TargetsInView;
import se.spaced.messages.protocol.Entity;

import java.util.List;

@Singleton
public class TargetApi {
	private final TargetsInView targetsInView;
	private final PlayerTabSelector playerTabSelector;
	private final PlayerTargeting playerTargeting;

	@Inject
	public TargetApi(TargetsInView targetsInView, PlayerTabSelector playerTabSelector, PlayerTargeting playerTargeting) {
		this.targetsInView = targetsInView;
		this.playerTabSelector = playerTabSelector;
		this.playerTargeting = playerTargeting;
	}

	@LuaMethod(name = "SetTarget", global = true)
	public boolean setTarget(ClientEntity spacedEntity) {
		if (spacedEntity == null) {
			return false;
		}

		playerTargeting.setTarget(spacedEntity);
		return true;
	}

	@LuaMethod(name = "TabTarget", global = true)
	public boolean tabTarget() {
		return playerTabSelector.tabTarget(200);
	}

	@LuaMethod(name = "GetTargetsInView", global = true)
	public List<ClientEntity> getDistanceSortedTargets(double maxRange, boolean ignoreDeadTargets) {
		return targetsInView.getDistanceSortedTargets(maxRange, ignoreDeadTargets);
	}

	@LuaMethod(name = "GetCurrentHover", global = true)
	public Entity getHover() {
		return playerTargeting.getHover();
	}
}