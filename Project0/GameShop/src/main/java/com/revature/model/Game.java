package com.revature.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Game implements Serializable {
	
	private String title;
	private LocalDate release_date;
	private GameStatus status;
	private GamePrice rent;
	private GamePrice buy;

}
