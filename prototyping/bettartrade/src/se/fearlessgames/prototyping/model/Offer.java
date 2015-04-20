package se.fearlessgames.prototyping.model;

public interface Offer {
	int getTotalPrice();

	Buyer getBuyer();

	int getQuantity();
}

class EmptyOffer implements Offer {
	private final Buyer buyer;

	EmptyOffer(Buyer buyer) {
		this.buyer = buyer;
	}

	@Override
	public int getTotalPrice() {
		return 0;
	}

	@Override
	public Buyer getBuyer() {
		return buyer;
	}

	@Override
	public int getQuantity() {
		return 0;
	}
}
