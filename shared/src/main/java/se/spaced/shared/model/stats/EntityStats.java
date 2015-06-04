package se.spaced.shared.model.stats;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import se.fearlessgames.common.util.TimeProvider;

import java.util.Map;

public class EntityStats {

	static final double STAMINA_TO_HEALTH_FACTOR = 7;
	public static final double IN_COMBAT_COOLRATE = 0.8;
	public static final double IN_COMBAT_REGEN = 0;
	public static final double SECONDS_TO_FULL_HEALTH_REGEN = 90.0;
	public static final double SECONDS_TO_FULL_SHIELD_RECOVERY = 5.0;
	public static final double ATTACK_RATING_PER_ATTACK_PERCENT_MULTIPLIER = 5.0;

	private final SimpleStat baseStamina;
	private final MaxHitPoints maxHealth;
	private final HealthStat currentHealth;
	private final SimpleStat baseCoolRate;
	private final SimpleStat maxHeat;
	private final SimpleStat baseHealthRegenRate;
	private final OutOfCombatHealthRegen outOfCombatHealthRegen;
	private final AbstractStat healthRegenRate;
	private final HeatStat currentHeat;
	private final SimpleStat baseShieldCharge;
	private final ShieldStrength shieldStrength;
	private final OutOfCombatHealthRegen outOfCombatShieldRecovery;
	private final CompoundRegenStat shieldRecovery;
	private final SimpleStat baseShieldRecoveryRate;
	private final TimeProvider timeProvider;
	private final SimpleStat baseShieldEfficiency;
	private final MaxShieldStrength maxShieldStrength;
	private final SpeedModifierStat baseSpeedModifier;

	private final SimpleStat baseAttackRating;
	private final AttackModifier attackModifier;

	private final Map<StatType, AuraStats> auraStats = Maps.newEnumMap(StatType.class);


	public EntityStats(TimeProvider timeProvider) {
		this(timeProvider, new StatData());
	}

	public EntityStats(TimeProvider timeProvider, StatData inits) {
		this.timeProvider = timeProvider;

		baseStamina = new SimpleStat("baseStamina", inits.getStamina());
		AuraStats stamina = new AuraStats(baseStamina);
		auraStats.put(StatType.STAMINA, stamina);
		maxHealth = new MaxHitPoints(stamina, STAMINA_TO_HEALTH_FACTOR);

		baseHealthRegenRate = new SimpleStat("baseHealthRegen", IN_COMBAT_REGEN);
		outOfCombatHealthRegen = new OutOfCombatHealthRegen(maxHealth, SECONDS_TO_FULL_HEALTH_REGEN);
		healthRegenRate = new CompoundRegenStat(baseHealthRegenRate, outOfCombatHealthRegen, "HealthRegenRate");

		maxHeat = new SimpleStat("maxHeat", 100);
		baseCoolRate = new SimpleStat("baseCoolRate", inits.getBaseCoolRate(), Double.NEGATIVE_INFINITY);

		baseShieldCharge = new SimpleStat("BaseShieldCharge", inits.getBaseShieldCharge());
		AuraStats shieldCharge = new AuraStats(baseShieldCharge);
		auraStats.put(StatType.SHIELD_CHARGE, shieldCharge);

		baseShieldEfficiency = new SimpleStat("BaseShieldEfficiency", inits.getBaseShieldEfficiency());
		AuraStats shieldEfficiency = new AuraStats(baseShieldEfficiency);
		auraStats.put(StatType.SHIELD_EFFICIENCY, shieldEfficiency);

		maxShieldStrength = new MaxShieldStrength(shieldEfficiency, shieldCharge);
		baseShieldRecoveryRate = new SimpleStat("BaseShieldRecoveryRate", inits.getShieldRecovery());
		outOfCombatShieldRecovery = new OutOfCombatHealthRegen(maxShieldStrength, SECONDS_TO_FULL_SHIELD_RECOVERY);

		baseAttackRating = new SimpleStat("AttackRating", inits.getBaseAttackRating());
		AuraStats attackRating = new AuraStats(baseAttackRating);
		auraStats.put(StatType.ATTACK_RATING, attackRating);
		attackModifier = new AttackModifier(attackRating, EntityStats.ATTACK_RATING_PER_ATTACK_PERCENT_MULTIPLIER);

		baseSpeedModifier = new SpeedModifierStat(1.0);

		AuraStats coolrate = new AuraStats(baseCoolRate);
		AuraStats speedModifier = new AuraStats(baseSpeedModifier);
		AuraStats recoveryModifier = new AuraStats(baseShieldRecoveryRate);
		shieldRecovery = new CompoundRegenStat(recoveryModifier, outOfCombatShieldRecovery, "ShieldRegenRate");

		auraStats.put(StatType.COOL_RATE, coolrate);
		auraStats.put(StatType.SPEED, speedModifier);
		auraStats.put(StatType.SHIELD_RECOVERY, recoveryModifier);
		currentHeat = new HeatStat(timeProvider, maxHeat, coolrate);

		shieldStrength = new ShieldStrength(timeProvider, maxShieldStrength, shieldRecovery);
		currentHealth = new HealthStat(timeProvider, maxHealth.getValue(), maxHealth, healthRegenRate);
	}

	public EntityStats(EntityStats stats) {
		this(stats.timeProvider,
				new StatData(stats.getStamina().getValue(),
						stats.getBaseShieldCharge().getValue(),
						stats.getShieldRecoveryRate().getValue(),
						stats.getBaseShieldEfficiency().getValue(),
						stats.getBaseCoolRate().getValue(), 0.0));
		currentHealth.changeValue(stats.getCurrentHealth().getValue());
		baseHealthRegenRate.changeValue(stats.getBaseHealthRegenRate().getValue());
		maxHeat.changeValue(stats.getMaxHeat().getValue());
		baseCoolRate.changeValue(stats.getBaseCoolRate().getValue());
		currentHeat.setValue(stats.getHeat().getValue());
	}

	public Stat getMaxHealth() {
		return maxHealth;
	}

	public HealthStat getCurrentHealth() {
		return currentHealth;
	}

	public Stat getHealthRegenRate() {
		return healthRegenRate;
	}

	public MutableStat getStamina() {
		return auraStats.get(StatType.STAMINA);
	}

	public Stat getBaseStamina() {
		return baseStamina;
	}

	public ShieldStrength getShieldStrength() {
		return shieldStrength;
	}

	public HeatStat getHeat() {
		return currentHeat;
	}

	public SimpleStat getBaseCoolRate() {
		return baseCoolRate;
	}
	
	public MutableStat getCoolRate() {
		return auraStats.get(StatType.COOL_RATE);
	}

	public SimpleStat getMaxHeat() {
		return maxHeat;
	}

	public AbstractStat getMaxShieldStrength() {
		return maxShieldStrength;
	}

	public MutableStat getShieldRecoveryRate() {
		return shieldRecovery;
	}

	public MutableStat getShieldEfficiency() {
		return auraStats.get(StatType.SHIELD_EFFICIENCY);
	}

	public SimpleStat getBaseShieldCharge() {
		return baseShieldCharge;
	}


	public MutableStat getShieldCharge() {
		return auraStats.get(StatType.SHIELD_CHARGE);
	}

	public SimpleStat getBaseShieldEfficiency() {
		return baseShieldEfficiency;
	}

	public AuraStats getSpeedModifier() {
		return auraStats.get(StatType.SPEED);
	}

	public void update(EntityStats entityStats) {
		baseStamina.changeValue(entityStats.getStamina().getValue());
		currentHealth.changeValue(entityStats.getCurrentHealth().getValue());
		baseHealthRegenRate.changeValue(entityStats.getBaseHealthRegenRate().getValue());

		outOfCombatHealthRegen.setEnabled(entityStats.getOutOfCombatHealthRegen().isEnabled());

		maxHeat.changeValue(entityStats.getMaxHeat().getValue());
		baseCoolRate.changeValue(entityStats.getBaseCoolRate().getValue());
		currentHeat.setValue(entityStats.getHeat().getValue());

		baseShieldCharge.changeValue(entityStats.getBaseShieldCharge().getValue());
		baseShieldEfficiency.changeValue(entityStats.getBaseShieldEfficiency().getValue());
		baseShieldRecoveryRate.changeValue(entityStats.getShieldRecoveryRate().getValue());
		outOfCombatShieldRecovery.setEnabled(entityStats.getOutOfCombatShieldRecovery().isEnabled());

		baseAttackRating.changeValue(entityStats.getBaseAttackRating().getValue());

		baseSpeedModifier.changeValue(entityStats.getSpeedModifier().getValue());
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		EntityStats stats = (EntityStats) o;

		if (!attackModifier.equals(stats.attackModifier)) {
			return false;
		}
		if (!getAttackRating().equals(stats.getAttackRating())) {
			return false;
		}
		if (!baseCoolRate.equals(stats.baseCoolRate)) {
			return false;
		}
		if (!baseHealthRegenRate.equals(stats.baseHealthRegenRate)) {
			return false;
		}
		if (!baseShieldCharge.equals(stats.baseShieldCharge)) {
			return false;
		}
		if (!baseShieldEfficiency.equals(stats.baseShieldEfficiency)) {
			return false;
		}
		if (!baseShieldRecoveryRate.equals(stats.baseShieldRecoveryRate)) {
			return false;
		}
		if (!getSpeedModifier().equals(stats.getSpeedModifier())) {
			return false;
		}
		if (!baseStamina.equals(stats.baseStamina)) {
			return false;
		}
		if (!currentHealth.equals(stats.currentHealth)) {
			return false;
		}
		if (!currentHeat.equals(stats.currentHeat)) {
			return false;
		}
		if (!healthRegenRate.equals(stats.healthRegenRate)) {
			return false;
		}
		if (!maxHealth.equals(stats.maxHealth)) {
			return false;
		}
		if (!maxHeat.equals(stats.maxHeat)) {
			return false;
		}
		if (!maxShieldStrength.equals(stats.maxShieldStrength)) {
			return false;
		}
		if (!outOfCombatHealthRegen.equals(stats.outOfCombatHealthRegen)) {
			return false;
		}
		if (!outOfCombatShieldRecovery.equals(stats.outOfCombatShieldRecovery)) {
			return false;
		}
		if (!shieldRecovery.equals(stats.shieldRecovery)) {
			return false;
		}
		if (!shieldStrength.equals(stats.shieldStrength)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = baseStamina.hashCode();
		result = 31 * result + maxHealth.hashCode();
		result = 31 * result + currentHealth.hashCode();
		result = 31 * result + baseCoolRate.hashCode();
		result = 31 * result + maxHeat.hashCode();
		result = 31 * result + baseHealthRegenRate.hashCode();
		result = 31 * result + outOfCombatHealthRegen.hashCode();
		result = 31 * result + healthRegenRate.hashCode();
		result = 31 * result + currentHeat.hashCode();
		result = 31 * result + baseShieldCharge.hashCode();
		result = 31 * result + shieldStrength.hashCode();
		result = 31 * result + outOfCombatShieldRecovery.hashCode();
		result = 31 * result + shieldRecovery.hashCode();
		result = 31 * result + baseShieldRecoveryRate.hashCode();
		result = 31 * result + timeProvider.hashCode();
		result = 31 * result + baseShieldEfficiency.hashCode();
		result = 31 * result + maxShieldStrength.hashCode();
		result = 31 * result + baseSpeedModifier.hashCode();
		result = 31 * result + baseAttackRating.hashCode();
		result = 31 * result + attackModifier.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).
				add("baseStamina", baseStamina).
				add("maxHealth", maxHealth).
				add("currentHealth", currentHealth).
				add("baseCoolRate", baseCoolRate).
				add("maxHeat", maxHeat).
				add("healthRegenRate", healthRegenRate).
				add("currentHeat", currentHeat).
				add("maxShieldStrength", maxShieldStrength).
				add("shieldStrength", shieldStrength).
				add("shieldRecoveryRate", getShieldRecoveryRate()).
				add("timeProvider", timeProvider).
				add("auraStats", auraStats).
				toString();
	}

	public AuraStats getAuraStatByType(StatType type) {
		AuraStats stats = auraStats.get(type);
		if (stats == null) {
			throw new RuntimeException("Could not find stat of type " + type);
		}
		return stats;
	}

	public SimpleStat getBaseHealthRegenRate() {
		return baseHealthRegenRate;
	}

	public OutOfCombatHealthRegen getOutOfCombatHealthRegen() {
		return outOfCombatHealthRegen;
	}

	public OutOfCombatHealthRegen getOutOfCombatShieldRecovery() {
		return outOfCombatShieldRecovery;
	}

	public AuraStats getBaseShieldRecovery() {
		return getAuraStatByType(StatType.SHIELD_RECOVERY);
	}

	public AuraStats getAttackRating() {
		return getAuraStatByType(StatType.ATTACK_RATING);
	}

	public AttackModifier getAttackModifier() {
		return attackModifier;
	}

	public SimpleStat getBaseAttackRating() {
		return baseAttackRating;
	}
}
