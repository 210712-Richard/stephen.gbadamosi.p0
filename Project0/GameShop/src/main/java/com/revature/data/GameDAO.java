package com.revature.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.revature.model.Game;
import com.revature.model.User;

public class GameDAO implements Serializable {
	private static final long serialVersionUID = 7426075925303078798L;

	private static String games_file = "games.dat";
	private static String pending_gfile = "pending_games.dat";
	
	public static List<User> pending_games;	
	private static List<Game> games;
	private static List<Game> rentals;
	private static List<Game> sales;

	
}
