package se.fearlessgames.prototyping.model;

public interface ItemPriceService {
	int getPriceForItemBasedOnStock(int desiredStock, int currentStock, int basePrice);
}
