package se.fearlessgames.prototyping.model;

public interface Seller {
	Offer requestOffer(int quantity, ItemTemplate itemTemplate, Buyer buyer);

	Order acceptOffer(Offer offer, int payments);

	int getSellPrice();
}
