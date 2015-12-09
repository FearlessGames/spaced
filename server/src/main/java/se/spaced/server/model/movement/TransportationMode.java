package se.spaced.server.model.movement;

public enum TransportationMode {
	WALK(true),
	RUN(true),
	CHARGE(true),
	FLY(false);

	private final boolean walkmeshBound;

	TransportationMode(boolean walkmeshBound) {
		this.walkmeshBound = walkmeshBound;
	}

	public boolean isWalkmeshBound() {
		return walkmeshBound;
	}
}
