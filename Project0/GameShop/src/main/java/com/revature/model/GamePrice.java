package com.revature.model;

public enum GamePrice {
	SALE_DISCOUNT(0.10), 
	RENT_PRICE(5L), 
	BUY_PRICE(20L),
	DAILY_BONUS(1L);
	
	private final double value;
	
	private GamePrice(double price) {
		this.value = price;
	}	
	
	private GamePrice(long price) {
		this.value = price;
	}

}
