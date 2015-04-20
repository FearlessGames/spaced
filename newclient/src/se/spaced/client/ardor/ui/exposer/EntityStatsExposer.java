package se.spaced.client.ardor.ui.exposer;

import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.shared.model.stats.EntityStats;

public class EntityStatsExposer {

	private final EntityStats entityStats;

	public EntityStatsExposer(EntityStats entityStats) {
		this.entityStats = entityStats;
	}

	@LuaMethod(name = "GetBaseStamina")
	public double getBaseStamina() {
		return entityStats.getBaseStamina().getValue();
	}

	@LuaMethod(name = "GetStamina")
	public double getStamina() {
		return entityStats.getStamina().getValue();
	}

	@LuaMethod(name = "GetCoolRate")
	public double getCoolRate() {
		return entityStats.getBaseCoolRate().getValue();
	}

	@LuaMethod(name = "GetCurrentHealth")
	public double getCurrentHealth() {
		return entityStats.getCurrentHealth().getValue();
	}

	@LuaMethod(name = "GetMaxHealth")
	public double getMaxHealth() {
		return entityStats.getMaxHealth().getValue();
	}

	@LuaMethod(name = "GetHealthRegenRate")
	public double getHealthRegenRate() {
		return entityStats.getHealthRegenRate().getValue();
	}

	@LuaMethod(name = "GetCurrentHeat")
	public double getHeat() {
		return entityStats.getHeat().getValue();
	}

	@LuaMethod(name = "GetMaxHeat")
	public double getMaxHeat() {
		return entityStats.getMaxHeat().getValue();
	}

	@LuaMethod(name = "GetShieldStrength")
	public double getShieldPower() {
		return entityStats.getShieldStrength().getValue();
	}

	@LuaMethod(name = "GetMaxShield")
	public double getMaxShield() {
		return entityStats.getMaxShieldStrength().getValue();
	}

	@LuaMethod(name = "GetShieldEfficiency")
	public double getShieldEfficiency() {
		return entityStats.getShieldEfficiency().getValue();
	}

	@LuaMethod(name = "GetShieldRecovery")
	public double getShieldRecovery() {
		return entityStats.getShieldRecoveryRate().getValue();
	}

	@LuaMethod(name = "GetSpeedModifier")
	public double getSpeedModifier() {
		return entityStats.getSpeedModifier().getValue();
	}

	@LuaMethod(name = "GetAttackRating")
	public double getAttackRating() {
		return entityStats.getAttackRating().getValue();
	}

	@LuaMethod(name = "GetAttackModifier")
	public double getAttackModifier() {
		return entityStats.getAttackModifier().getValue();
	}
}
