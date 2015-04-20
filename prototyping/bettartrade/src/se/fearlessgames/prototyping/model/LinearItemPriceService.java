package se.fearlessgames.prototyping.model;

public class LinearItemPriceService implements ItemPriceService {

	private final double slope;

	public LinearItemPriceService(double slope) {
		this.slope = slope;
	}

	@Override
	public int getPriceForItemBasedOnStock(int desiredStock, int currentStock, int basePrice) {
		int diffFromDesired = currentStock - desiredStock;
		return (int) Math.max(basePrice + slope * diffFromDesired, 0.0);
	}
}
