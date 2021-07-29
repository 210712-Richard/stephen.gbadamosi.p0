package com.revature.data;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.revature.model.*;
import com.revature.util.SingletonScanner;

public class GameDAO implements Serializable {
	private static final long serialVersionUID = 7426075925303078778L;

	public static String games_file = "games.dat";
	public static String pending_gfile = "pending_games.dat";
	public static List<Game> pending_games;	
	public static List<Game> games;
	private static List<Game> rentals;
	private static List<Game> sales;
	
	public static String admin_msg_games = "";
	private static Scanner scan = SingletonScanner.getScanner().getScan();


	static {

		DataSerializer<Game> ds = new DataSerializer<>();
		games = new ArrayList<Game>();
		pending_games = new ArrayList<Game>();
		rentals = new ArrayList<Game>();
		sales = new ArrayList<Game>();
		File file;
		
		try {
			file = new File(games_file);
			if(!file.exists()) {
				System.out.println("Games file does not exist in working directory\n"
						+ "Creating new file..");
				writeToFile(games, games_file);
	
			}
			else {
				System.out.println("Games file already exists");
				games = ds.readObjectsFromFile(games_file);
				System.out.println(games == null);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			file = new File(pending_gfile);
			if(!file.exists()) {
				System.out.println("Pending games file does not exist in working directory\n"
						+ "Creating new file..");	
				writeToFile(pending_games, pending_gfile);
			}
			else {
				System.out.println("Pending games file already exists");
				pending_games = ds.readObjectsFromFile(pending_gfile);
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("There are no pending users");
		}
		
		if(games.size() <= 0) {	// Add default titles
			games.add(new Game("Final Fantasy VIII", GameRating.E, LocalDate.of(1999, 2, 11), GameStatus.AVAILABLE));
			games.add(new Game("Final Fantasy X", GameRating.E, LocalDate.of(2001, 7, 19), GameStatus.AVAILABLE));
			games.add(new Game("Devil May Cry 3 - Dante's Awakening", GameRating.MA, LocalDate.of(2005, 2, 17), GameStatus.AVAILABLE));
			games.add(new Game("Shinobi", GameRating.PG13, LocalDate.of(2002, 11, 10), GameStatus.AVAILABLE));
			games.add(new Game("Tomb Raider", GameRating.PG13, LocalDate.of(2013, 3, 13), GameStatus.AVAILABLE));
			
			writeToFile(games, games_file);
		}
		populateList();
		
		if(pending_games == null || pending_games.size() == 0) {	// Verify there are no titles pending approvals, else present to admin			admin_msg = "No tasks pending";
			admin_msg_games = "No games pending admin approval";
			System.out.println(admin_msg_games);
		}
		
		else {
			
			admin_msg_games = "Games queued for approval, please review";
			System.out.println("Pending users list: " + pending_games.toString());
// 			approveUsers():
		}
		
		System.out.println("******* Game inventory *******");
		for(Game g : games) {
			System.out.println(g.toString());
		}
	}
	
	public static Game addNewTitle() {
		String new_title = null;
		while(new_title == null) {	// Check input and requested title for validity
			new_title = getTitle();
			if (new_title != null) {
				if(!checkTitle(new_title)) {
					System.out.println("About to add new game title: " + new_title);
					break;
				}
				System.out.println("Please enter a valid title that isn't already in the game inventory");
				new_title = null;
			}
		}
		
		GameRating rating = getRating();
		LocalDate release = getReleaseDate();
		Game new_game = new Game(new_title, rating, release, GameStatus.AVAILABLE);
		games.add(new_game);
		
		writeToFile(games, games_file);
		return new_game;
	}
	
	public static boolean checkTitle(String title) {
		if(games.size() > 0) {
			for(Game g : games) {
				if(g.title.equalsIgnoreCase(title)) {
					System.out.println("Found title in games inventory");
					return true;
				}
			}
		}
		
		if(pending_games.size() > 0) {
			for(Game g : pending_games) {
				if(g.title.equalsIgnoreCase(title)) {
					System.out.println("Found game in pending approval queue");
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean checkReleaseDate(LocalDate release) {
		if(release.isAfter(LocalDate.now().plusWeeks(4))) {
			System.out.println("Release date is too far in the future");
			return false;
		}
		
		return true;
	}
	
	public static Game getGame(String request) {
		String target = request;
		if (request == null || request.equals("")) {
			System.out.println("Enter the title you wish to remove from inventory: ");
			target = scan.nextLine();
		}
		if(target == null || target.equals("")) {
			System.out.println("Invalid input, try again.");
			return null;
		}
		target.trim();
		
		if(GameDAO.games.size() > 0) {
			for(Game game : GameDAO.games) {
				if(game.title.equalsIgnoreCase(target)) {
					System.out.println("Game found in database");
					return game;
				}
			}
			System.out.println("Could not find game in database. Double check title");
		}
		
		else 
			System.out.println("No games in database");
		
		return null;
	}
	
	public static GameStatus changeStatus(Game game) {
		System.out.println("Enter new status: {Available, Rent, Sell, Unavailable}");
		String status = scan.nextLine();
		if(status == null || status.equals("")) {
			System.out.println("Invalid input, try again.");
			return null;
		}
		status.trim();
		if(status.equalsIgnoreCase("available"))
			return GameStatus.AVAILABLE;
		
		if(status.equalsIgnoreCase("rent") || status.equalsIgnoreCase("rented")) {
			System.out.println("Attempting to rent " + game.title);
			if(game.status != GameStatus.AVAILABLE) {
				System.out.println("Game is " + game.status + " Try again later");
				return game.status;
			}
			
			String username = null;
			User user = null;
			Long use_pts = 0L;
			
			while(username == null || username.equals("")) {
				System.out.println("Enter username to assign title");
				username = scan.nextLine();
				if(username != null && !username.equals("")) {
					username.trim();
					user = UserDAO.getUser(username);
					if(user != null) {
						if(user.getType() == UserType.ADMIN) {
							use_pts = user.getPoints() - 5L;
							if(use_pts >= 0) {
								user.setPoints(use_pts);
								user.inventory.add(game);
								game.rentedBy = user.getUsername();
								game.status = GameStatus.RENTED;
								game.rentDate = LocalDate.now();
								game.returnDate = game.rentDate.plusWeeks(2);
								System.out.println(game.title + " rented to " + user.getUsername() + 
										" for 2 weeks (Due on " + game.getReturnDate().toString() + ")");
								
								UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);
								writeToFile(games, games_file);
								
								return GameStatus.RENTED;
							}
							else 
								System.out.println("Insufficient points for user to rent title");
						}
						
						else {	// User is non-admin
							use_pts = user.getPoints() - 5L;
							if(use_pts >= 0) {
								user.setPoints(use_pts);
								user.inventory.add(game);
								user.setType(UserType.GAMER);
								game.rentedBy = user.getUsername();
								game.status = GameStatus.RENTED;
								game.rentDate = LocalDate.now();
								game.returnDate = game.rentDate.plusWeeks(1);
								System.out.println(game.title + " rented to " + user.getUsername() + 
										" for 1 week (Due on " + game.getReturnDate().toString() + ")");
	
								UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
								writeToFile(games, games_file);
								
								return GameStatus.RENTED;
							}
							
							System.out.println("Not enough points to rent game, buy more points then try again");
						}				
					}
					System.out.println("Invalid username, try again");
					continue;
				}
			}
			
			System.out.println("Rental failed, try again later");
			return GameStatus.AVAILABLE;
		}
		
		if(status.equalsIgnoreCase("unavailable"))
			return GameStatus.UNAVAILABLE;
		
		if(status.equalsIgnoreCase("sell") || status.equalsIgnoreCase("sold")) {
			if(game.status != GameStatus.AVAILABLE) {
				System.out.println("Game is " + game.status + " Try again later");
				return game.status;
			}
			
			String username = null;
			User user = null;
			Long use_pts = 0L;
			
			while(username == null || username.equals("")) {
				System.out.println("Enter username to assign title");
				username = scan.nextLine();
				if(username != null && !username.equals("")) {
					username.trim();
					user = UserDAO.getUser(username);
					if(user != null) {
						use_pts = user.getPoints() - 20L;
						if(user.getType() == UserType.ADMIN || (game.rentedBy != null && game.rentedBy.equals(user.getUsername()))) {
							use_pts = user.getPoints() - 10L;
						}
						if(use_pts >= 0) {
							user.setPoints(use_pts);
							user.inventory.add(game);
							game.ownedBy = user.getUsername();
							game.status = GameStatus.SOLD;
							game.rentedBy = null;
							game.rentDate = null;
							game.returnDate = null;
							System.out.println(game.title + " sold to " + user.getUsername());
							
							UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
							UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);
							writeToFile(games, games_file);
							
							return GameStatus.SOLD;
						}										
			
						System.out.println("Not enough points to buy game, buy more points then try again");
						return GameStatus.AVAILABLE;
					}
					
					System.out.println("Invalid username, try again");
					continue;
				}
			}
		}
		
		System.out.println("Invalid input for game status");
		return null;
	}
	
	public static String getTitle() {
		System.out.println("Enter game title: ");
		String title = scan.nextLine();
		if(title == null || title.equals("")) {
			System.out.println("Invalid input, try again.");
			return null;
		}

		title.trim();		
		System.out.println("Game title chosen: " + title);
		return title;
	}
	
	public static GameRating getRating() {
		boolean validRating = false;
		GameRating g_rating = GameRating.PG13;
		
		while(!validRating) {
			System.out.println("Enter game rating: ");
			System.out.println("\t1. E - Everyone");
			System.out.println("\t2. PG13 - 13 years and older");
			System.out.println("\t3. 17 - 17+");
			String rating = scan.nextLine();
			rating.trim();
			
			if(rating.equalsIgnoreCase("E") || rating.equalsIgnoreCase("Everyone") || rating.equalsIgnoreCase("All")) {
				g_rating = GameRating.E;
				validRating = true;
			}
			if(rating.equalsIgnoreCase("PG13") || rating.equalsIgnoreCase("13")) {
				g_rating = GameRating.PG13;
				validRating = true;
			}
			if(rating.equals("17") || rating.equals("17+") || rating.equalsIgnoreCase("MA") || rating.equalsIgnoreCase("Mature")) {
				g_rating = GameRating.MA;
				validRating = true;
			}
		}
		
		return g_rating;
	}
	
	public static LocalDate getReleaseDate() {
		boolean validDate = false;
		LocalDate release = LocalDate.of(LocalDate.now().getYear(), 1, 1);
		
		while(!validDate) {
			System.out.println("Enter Game release date (YYYY/MM/DD): ");
			List<Integer> date = Stream.of(scan.nextLine().split("/"))
					.map((str)->Integer.parseInt(str)).collect(Collectors.toList());
		
			release = LocalDate.of(date.get(0), date.get(1), date.get(2));
			
			if(checkReleaseDate(release)) {
				System.out.println("Release date entered: " + release.toString());
				validDate = true;
			}
			else {
				System.out.println("Invalid release date. Try again");
			}
		}
		
		return release;
	}
	
	public static LocalDate getReturnDate(int weeks) {
		if(weeks <= 1) {
			System.out.println("Game is due in 1 week (" + LocalDate.now().plusWeeks(1).toString() + ")");
			return LocalDate.now().plusWeeks(1);
		}
		
		System.out.println("Game is due in " + weeks + " weeks (" + LocalDate.now().plusWeeks(weeks).toString() + ")");
		return LocalDate.now().plusWeeks(weeks);
	}
	
	private static void populateList() { 
		if(games.size() <= 0) {
			System.out.println("No games available");
			return;
		}
		
		for(Game g : games) {
			if (g.status == GameStatus.RENTED && g.returnDate.isAfter(LocalDate.now()))	{	// Title due for return, update status
				g.status = GameStatus.AVAILABLE;
				g.rentDate = null;
				g.returnDate = null;
				rentals.remove(g);
			}
			if(g.status == GameStatus.RENTED) {
				rentals.add(g);
			}
			if(g.status == GameStatus.SOLD) {
				sales.add(g);
			}
		}
		System.out.println("Number of titles rented: " + rentals.size());
		System.out.println("Number of titles sold: " + sales.size());
	}
	
	public static void writeToFile(List<Game> games, String filename) {
		new DataSerializer<Game>().writeObjectsToFile(games, filename);
	}
}
