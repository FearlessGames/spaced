package se.fearlessgames.prototyping.model;

import java.util.Collection;

public interface Buyer {
	void receive(Order order, Collection<Item> items);
}
