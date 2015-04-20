package se.fearlessgames.prototyping.trade.model;

import com.google.common.collect.Lists;

import java.util.List;

public class ItemStock {
	private List<Item> stock = Lists.newArrayList();
	private int wantedStock;

	public ItemStock(int wantedStock) {
		this.wantedStock = wantedStock;
	}

	public List<Item> getStock() {
		return stock;
	}

	public int getWantedStock() {
		return wantedStock;
	}
}
