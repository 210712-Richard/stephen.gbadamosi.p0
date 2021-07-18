package com.revature.model;

import java.io.Serializable;

public enum GamePrice implements Serializable {
	SALE_DISCOUNT(0.10), 
	RENT_PRICE(5L), 
	BUY_PRICE(20L);
	
	private final double value;
	
	private GamePrice(double price) {
		this.value = price;
	}	
	
	private GamePrice(Long price) {
		this.value = price;
	}

}
