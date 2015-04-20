package se.spaced.server.model.items;

public class InventoryOutOfBoundsException extends Exception {
	private final int position;
	private final int startingIndex;
	private final int capacity;

	public InventoryOutOfBoundsException(int position, int startingIndex, int capacity) {
		super(String.format("Index %d out of bounds [%d-%d]", position, startingIndex, capacity));
		this.position = position;
		this.startingIndex = startingIndex;
		this.capacity = capacity;
	}

	public int getPosition() {
		return position;
	}

	public int getStartingIndex() {
		return startingIndex;
	}

	public int getCapacity() {
		return capacity;
	}
}
