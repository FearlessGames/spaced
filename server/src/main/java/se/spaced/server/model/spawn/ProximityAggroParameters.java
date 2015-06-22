package se.spaced.server.model.spawn;

public class ProximityAggroParameters {
	private final int proximityAggroDistance;
	private final int socialAggroDistance;

	public ProximityAggroParameters(int proximityAggroDistance, int socialAggroDistance) {
		this.proximityAggroDistance = proximityAggroDistance;
		this.socialAggroDistance = socialAggroDistance;
	}

	public int getProximityAggroDistance() {
		return proximityAggroDistance;
	}

	public int getSocialAggroDistance() {
		return socialAggroDistance;
	}
}
