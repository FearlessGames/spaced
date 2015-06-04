package se.spaced.shared.model.stats;

public class StatData {
	private double maxStamina;
	private double baseShieldCharge;
	private double currentHealth;
	private double shieldRecovery;
	private double baseShieldEfficiency;
	private double baseCoolRate;
	private double baseAttackRating;

	public StatData() {
	}

	public StatData(
			double stam,
			double baseShieldCharge,
			double shieldRecovery,
			double baseShieldEfficiency,
			double baseCoolRate, double attackRating) {
		maxStamina = stam;
		this.baseShieldCharge = baseShieldCharge;
		this.shieldRecovery = shieldRecovery;
		this.baseShieldEfficiency = baseShieldEfficiency;
		this.baseCoolRate = baseCoolRate;
		this.baseAttackRating = attackRating;
		currentHealth = maxStamina * EntityStats.STAMINA_TO_HEALTH_FACTOR;
	}

	public double getStamina() {
		return maxStamina;
	}

	public double getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(double currentHealth) {
		this.currentHealth = currentHealth;
	}

	public double getBaseShieldCharge() {
		return baseShieldCharge;
	}

	public double getShieldRecovery() {
		return shieldRecovery;
	}

	public void update(EntityStats entityStats) {
		maxStamina = entityStats.getStamina().getValue();
		currentHealth = entityStats.getCurrentHealth().getValue();
		baseShieldCharge = entityStats.getBaseShieldCharge().getValue();
		shieldRecovery = entityStats.getShieldRecoveryRate().getValue();
		baseShieldEfficiency = entityStats.getBaseShieldEfficiency().getValue();
		baseCoolRate = entityStats.getBaseCoolRate().getValue();
		baseAttackRating = entityStats.getBaseAttackRating().getValue();
	}

	public double getBaseShieldEfficiency() {
		return baseShieldEfficiency;
	}

	public double getBaseCoolRate() {
		return baseCoolRate;
	}

	public double getBaseAttackRating() {
		return baseAttackRating;
	}
}
