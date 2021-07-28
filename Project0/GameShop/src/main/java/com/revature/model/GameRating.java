package com.revature.model;

public enum GameRating {
	E("Everyone"), 
	PG13("13 and older"),
	MA("Mature - 17+");
/**
 *  E - For everyone
 *  PG13 - For players 13 and up
 *  MA - For players 17 and up
 */
	private final String value;		
	private GameRating(String s) {
		this.value = s;
	}
}
