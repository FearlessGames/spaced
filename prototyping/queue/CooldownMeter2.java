
public class CooldownMeter2 {

	public boolean queued = false;
	
	private long lastUpdatedTimestamp;
	private long valueAtLastUpdate;
	
	private long maxValue;
	private long cost;
	private long lastConsumedValue;
	
	public CooldownMeter2(long now, long cost, long buffer) {
		long maxValue = cost + buffer;
		this.cost = cost;
		this.maxValue = maxValue;
		reset(now);
	}

	public void reset(long now) {
		lastUpdatedTimestamp = now;
		valueAtLastUpdate = 0;
		lastConsumedValue = 0;
	}
	
	public boolean canCast(long now) {
		long cooldown = getCooldown(now);
		return cooldown >= cost;
	}
	
	public long getCooldown(long now) {
		long cooldown = valueAtLastUpdate + (now - lastUpdatedTimestamp);
		cooldown = Math.min(maxValue, cooldown);
		return cooldown;
	}
	
	public long getMaxValue() {
		return maxValue;
	}
	
	public void consumeCooldown(long now, long cost) {
		if (canCast(now)) {
			long cooldown = getCooldown(now);
			this.lastUpdatedTimestamp = now;
			valueAtLastUpdate = cooldown - cost;
		}
	}

	public void consumeCooldown(long now) {
		consumeCooldown(now, cost);
		lastConsumedValue = getCooldown(now);
	}

	public long getCost() {
		return cost;
	}
	
	public long getLastConsumed() {
		return lastConsumedValue;
	}
}
