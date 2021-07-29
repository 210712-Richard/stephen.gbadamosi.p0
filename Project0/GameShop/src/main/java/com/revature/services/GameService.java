package com.revature.services;

import java.time.LocalDate;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.revature.util.SingletonScanner;

import com.revature.menu.Menu;
import com.revature.model.*;
import com.revature.data.*;

public class GameService {

	private static final Logger log = LogManager.getLogger(Menu.class);
	private static Scanner scan = SingletonScanner.getScanner().getScan();
	
	
	public void modifyGame(User admin) {
		// Verify user is admin
		if(admin.getType() != UserType.ADMIN) {
			System.out.println("You don't have permission to modify game info");
			return;
		}
		
		if(GameDAO.games.size() <= 0) {
			System.out.println("No games added to inventory. Add at least one game to access this function");
			return;
		}
		
		// List games by title
		GameDAO.games.toString();
		System.out.println("Use the game title from list above to modify game details");
		
		String game_name = GameDAO.getTitle();
		Game game = GameDAO.getGame(null);
		if(game == null) {
			System.out.println("No game found with requested title. Try again");
			return;
		}
		
		System.out.println(game.toString());
		modGameLoop: while(true) { 
			switch(modGameMenu()) {
			case 1:	// Change title
				System.out.println("Enter new title: ");
				game_name = GameDAO.getTitle();
				if(game_name == null) {
					System.out.println("Invalid title. Try again");
					continue modGameLoop;
				}
				else {
					game.setTitle(game_name);
					GameDAO.writeToFile(GameDAO.games, GameDAO.games_file);
					System.out.println("New title saved");
					System.out.println(game.toString());
				}
				break;
			case 2:	// Change rating
				GameRating new_rating = GameDAO.getRating();
				if(new_rating == null) {
					System.out.println("Invalid Rating. Try again");
					continue modGameLoop;
				}
				else {
					game.setRating(new_rating);
					System.out.println("New rating saved");
					System.out.println(game.toString());
				}
				break;
			case 3:	// Change release date
				LocalDate new_release = GameDAO.getReleaseDate();
				game.setRelease_date(new_release);
				System.out.println("New release date saved");
				System.out.println(game.toString());

				break;
			case 4:	// Change status
				GameStatus new_status = GameDAO.changeStatus(game);
				if(new_status == null) {
					System.out.println("Invalid status entry. Try again");
					continue modGameLoop;
				}
				
				game.setStatus(new_status);
				System.out.println("New status saved");
				System.out.println(game.toString());

				break;
			case 0: 
				System.out.println("Exiting menu..");
				return;
				
			default:
				System.out.println("Invalid input for menu selection");
				continue modGameLoop;
			}
		}
		
	
		
		// Print menu for modifiable properties
	}
	
	public static void buyGame(User user) {
		boolean validSelection = false;
		System.out.println("Entering title menu for sales..");

		if(GameDAO.games.size() <= 0) {
			System.out.println("No titles available for purchase. Try again later");
			return;
		}
		
		// List games by title
		System.out.println("Select game title you'd like to buy");
		GameDAO.games.toString();
		Game game = null;
		chooseGameLoop: while(!validSelection) { 
			String game_name = GameDAO.getTitle();
			if(game_name == null) {
				System.out.println("Invalid game title for purchase. Try again");
				continue chooseGameLoop;
			}
			game = GameDAO.getGame(null);
			if(game == null) {
				System.out.println("Invalid game title for purchase. Try again");
				continue chooseGameLoop;				
			}
			if(game.status != GameStatus.AVAILABLE && (game.rentedBy == null || !game.rentedBy.equals(user.getUsername()))) {
				System.out.println("That title is currently unavailable, please try another");
				continue chooseGameLoop;
			}
			
			else
				validSelection = true;
		}
		Long buy_price = 20L;
		// Verify user type
		if(game != null) {
			if(user.getType() == UserType.ADMIN || (game.rentedBy != null && game.rentedBy.equals(user.getUsername()))) {
				// Apply discount for renting title
				buy_price = 10L;
			}
			
			if(user.getPoints() > buy_price) {
				user.setPoints(user.getPoints() - buy_price);
				user.inventory.add(game);
				game.ownedBy = user.getUsername();
				game.status = GameStatus.SOLD;
				game.rentedBy = null;
				game.rentDate = null;
				game.returnDate = null;
				System.out.println(game.title + " sold to " + user.getUsername());
				
				UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
				UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);
				GameDAO.writeToFile(GameDAO.games, GameDAO.games_file);
				
				return;			
			}
			
			System.out.println("Insufficient funds to purchase game. Try again when you have more points");
			return;
		}
	}
	
	public static void rentGame(User user) {
		boolean validSelection = false;
		System.out.println("Entering title menu for rentals..");
		
		if(GameDAO.games.size() <= 0) {
			System.out.println("No titles available for rent. Try again later");
			return;
		}
		
		// List games by title
		GameDAO.games.toString();
		System.out.println("Enter a game title you'd like to rent");
		Game game = null;
		chooseGameLoop: while(!validSelection) { 
			String game_name = GameDAO.getTitle();
			if(game_name == null) {
				System.out.println("Invalid game title for rent. Try again");
				continue chooseGameLoop;
			}
			game = GameDAO.getGame(game_name);
			if(game == null) {
				System.out.println("Invalid game title for rent. Try again");
				continue chooseGameLoop;				
			}
			if(game.status != GameStatus.AVAILABLE) {
				System.out.println("That title is currently unavailable, please try another");
				continue chooseGameLoop;
			}
			
			else
				validSelection = true;
		}
		// Verify user type
		if(game != null) {
			if(user.getType() != UserType.ADMIN) {
				// Default to one week for user rentals
				if(user.getPoints() > 5L) {
					user.setType(UserType.GAMER);
					user.setPoints(user.getPoints() - 5L);
					user.inventory.add(game);
					game.status = GameStatus.RENTED;
					game.rentDate = LocalDate.now();
					game.returnDate = LocalDate.now().plusWeeks(1);
					game.rentedBy = user.getUsername();
					System.out.println("Game rented for a week. Due Date: " + game.returnDate.toString());
					
					UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
					GameDAO.writeToFile(GameDAO.games, GameDAO.games_file);
					return;
				}
				else {
					System.out.println("Not enough points available to rent");
					return;
				}
			}
			else {
				// Give admins 4-week rental option
				int selection = GameService.rentMenu();
				if(selection > 0 && selection <= 4) {
					user.setPoints(user.getPoints() - 5L);
					user.inventory.add(game);
					game.status = GameStatus.RENTED;
					game.rentDate = LocalDate.now();
					game.returnDate = LocalDate.now().plusWeeks(selection);
					game.rentedBy = user.getUsername();
					System.out.println("Game rented for " + selection + " week(s). Due Date: " + game.returnDate.toString());

					UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);
					GameDAO.writeToFile(GameDAO.games, GameDAO.games_file);
					return;
				}
				
				else {
						System.out.println("Invalid rent selection. No change made to title");
						return;
				}
			}
		}
	}
	
	public static Long usePoints(User user, String intent) {
		Long use_pts = -1L;
	
		if(user == null || intent == null) {
			System.out.println("Invalid or Missing input, unable to use points");
		}
		
		if(intent.equalsIgnoreCase("rent")) {
			use_pts =  user.getPoints() - 5L;
		}
		
		if(intent.equalsIgnoreCase("sell") || intent.equalsIgnoreCase("buy")) {
			use_pts = user.getPoints() - 20L;
		}
		
		return use_pts;
	}
	
	private static void removeGame(User admin) {
		if(GameDAO.games.size() > 0) {
			System.out.println("Games List: ");
			System.out.println(GameDAO.games.toString());
		}
		// Verify user is admin
		if(admin.getType() == UserType.ADMIN) {
			// Search game by title
			Game del_title = GameDAO.getGame(null);
			
			// Remove game
			if(del_title != null) {
				if(del_title.status != GameStatus.AVAILABLE) {
					System.out.println("Unable to delete game, check status then try again. Remove failed");
					return;
				}
				System.out.println("Found Game: " + del_title.toString());
				System.out.println("Are you sure you want to delete this title? Y or N to continue");			
				String confirm = scan.nextLine();
				confirm.trim();
				
				if(confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y")) {
					GameDAO.games.remove(del_title);
					System.out.println("Deleted Game: " + del_title.title + " from inventory");
					return;
				}
				if(confirm.equalsIgnoreCase("no") || confirm.equalsIgnoreCase("n")) {
					System.out.println("Delete operation cancelled by admin");
					return;
				}
			}
			System.out.println("Remove failed. Double check title and try again");
		}
		
		else
			System.out.println("You don't have permission for the requested action. Please login as an admin");

		return;
	}
	private static int rentMenu() {
		System.out.println("How long would you like to rent this game for?");
		System.out.println("\t1. 1 week");
		System.out.println("\t2. 2 weeks");
		System.out.println("\t3. 3 weeks");
		System.out.println("\t4. 4 weeks");
		int selection = Menu.select();
		
		log.trace("Start menu returning selection: " + selection);
		return selection;
	}
	
	private int modGameMenu() {
		System.out.println("What would you like to do?");
		System.out.println("\t1. Change Title");
		System.out.println("\t2. Change Rating");
		System.out.println("\t3. Change Release Date");
		System.out.println("\t4. Change Status");
		System.out.println("\t0. Quit");
		int selection = Menu.select();
		
		log.trace("Start menu returning selection: " + selection);
		return selection;
	}

}
