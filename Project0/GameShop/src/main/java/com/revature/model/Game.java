package com.revature.model;

import java.time.LocalDate;

import com.revature.data.GameDAO;

public class Game extends GameDAO {
	
	private String title;
	private String rating;
	private LocalDate release_date;
	private GameStatus status;
	private GamePrice rent;
	private GamePrice buy;

}
