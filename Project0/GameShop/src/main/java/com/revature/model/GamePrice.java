package com.revature.model;

import java.io.Serializable;

public enum GamePrice implements Serializable {
	SALE_DISCOUNT(0.10), 
	RENT_PRICE(5.0), 
	BUY_PRICE(20.0);
	
	private final double value;
	
	private GamePrice(double price) {
		this.value = price;
	}

}
