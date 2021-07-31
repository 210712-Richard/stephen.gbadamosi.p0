package com.revature.menu;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.data.GameDAO;
import com.revature.data.UserDAO;
import com.revature.model.Game;
import com.revature.model.User;
import com.revature.model.UserType;
import com.revature.services.*;
import com.revature.util.SingletonScanner;

// Encapsulate the user interface methods
public class Menu {

	private static final Logger log = LogManager.getLogger(Menu.class);
	
	private UserService us = new UserService();
	private AdminService as = new AdminService();
	public User loggedUser = null;
	private Scanner scan = SingletonScanner.getScanner().getScan();
	
	public void start() {
		log.trace("GameShop online: start()");
		mainLoop: while(true) {
			switch(startMenu()) {
			case 1:
				// login
				System.out.println("Please enter your username or ID to login: ");
				String username = scan.nextLine();
				Integer id = 0;
				log.debug(username);
				// Call the user service to find the user we want.
				User u = null;
				if(username.matches("^\\d+")) {	// Is Integer
					try {
						id = Integer.parseInt(username);
						u = us.login(id);
					} catch(Exception e) {
						System.out.println("Invalid ID format");
						e.printStackTrace();
					} 
				}
				else {
					u = us.login(username);
				}
				
				if(u == null) {
					log.warn("Unsuccessful login attempt: "+ username);
					System.out.println("Please try again.");
				} 
				else {
					loggedUser = u;
					System.out.println("Welcome: " + u.getUsername());
					// call our next method (either the Player menu or the Admin menu, depending on user)
					log.info("Successful login for user: "+loggedUser);
					
					switch(loggedUser.getType()) {
					case PENDING:
						System.out.println("Your account is pending review. You can login to "
								+ "access services once it is confirmed by an admin");
						break;
					case CUSTOMER:
					case GAMER:
						if(loggedUser.getLastCheckIn() != null)
							System.out.println(UserDAO.user_msg + " " + loggedUser.getUsername() + "!");
						
						gamer(loggedUser);
						break;
					
					case ADMIN:
						if(!UserDAO.admin_msg.isEmpty()) {
							System.out.println(loggedUser.getUsername() + ": " + UserDAO.admin_msg);
							System.out.println(loggedUser.getUsername() + ": " + GameDAO.admin_msg_games);
						}
						
						admin(loggedUser);
						break;
					}
				}
				break;
			case 2:
				us.register();
				System.out.println("Thanks for registering with us. "
						+ "An admin will review and confirm your account within 72 hours.\n"
						+ "Reach us at gso@gameshop.com with any questions");
				break;
			case 0:
				// quit
				System.out.println("Thanks for using GameShop Online - Goodbye!");
				break mainLoop;
			default:
				// invalid selection
				System.out.println("Not a valid selection, please try again.");
				continue;
			}
		}
		log.trace("Ending start()");
	}
	
	private int startMenu() {
		log.trace("called startMenu()");
		System.out.println("Welcome to GameShop online!");
		System.out.println("What would you like to do?");
		System.out.println("\t1. Login");
		System.out.println("\t2. Register");
		System.out.println("\t0. Quit");
		try {
		int selection = select();
		log.trace("Start menu returning selection: " + selection);
		return selection;
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public static void printUser(User user) {
		if(user == null) {
			System.out.println("Couldn't find user to show details. Try again");
			return;
		}
		
		System.out.println(user.toString());
		System.out.println("**********   Account Details   **********");
		
		if(user.getName() != null &&  user.getLastName() != null)
			System.out.println("\t** Name: " + user.getName() + " " + user.getLastName());
		
		System.out.println("\t** Username: " + user.getUsername() + " (" + user.getId() + ")");
		System.out.println("\t** Email: " + user.getEmail());
		System.out.println("\t** Birthday: " + user.getBirthday());
		System.out.println("\t** Account Type: " + user.getType());
		System.out.println("\t** GS Points: " + user.getPoints());
//		System.out.println("Last Login: " + user.getLastCheckIn());
		
		if(user.getBirthday().equals(LocalDate.now()))
			System.out.println("Message from GameShop: Happy Birthday!");
		
		System.out.println("Message from GameShop: " + user.getMessage());
		System.out.println("Gaming Inventory: " + (user.getInventory() == null ? "Empty" : user.getInventory()));
		
		return;
	}
	
	private void gamer(User user) {
		log.trace("Gamer menu loading...");
		gamerLoop: while(true) {
			switch(gamerMenu()) {
			case 1:	// View Account Details				
				printUser(user);
				break;
			case 2:	// Update Account Details
				System.out.println(user.toString());
				us.updateAccount(user);
				break;
			case 3:	// Rent title
				GameService.rentGame(user);
				break;
			case 4:	// Buy title
				GameService.buyGame(user);
				break;
			case 5:	// Return title
				GameService.returnGame(user);
				break;
			case 6:	// Request title
				us.requestTitle(user);
				break;
			case 7:	// View and buy points
				System.out.println("You currently have " + loggedUser.getPoints() + " points.");
				pointsMenu();
				break;
			case 0:
				loggedUser = null;
				break gamerLoop;
			default:
				// invalid selection
				System.out.println("Not a valid selection, please try again.");				
			}
		}
	}
	
	private int gamerMenu() {
		System.out.println("What would you like to do?");
		System.out.println("\t1. View Account Details");
		System.out.println("\t2. Update Account Details");
		System.out.println("\t3. Use Points: Rent Title");
		System.out.println("\t4. Use Points: Buy Title");
		System.out.println("\t5. Return Title");
		System.out.println("\t6. Request new Title");
		System.out.println("\t7. Buy Points");
		System.out.println("\t0. Logout");
		return select();
	}
	
	private void pointsMenu() {
		System.out.println("You currently have " + loggedUser.getPoints() + " points.");
		String first_tier = loggedUser.getType() == UserType.ADMIN ? "20 points" : "5 Points";
		String second_tier = loggedUser.getType() == UserType.ADMIN ? "50 points" : "10 Points";
		String third_tier = loggedUser.getType() == UserType.ADMIN ? "70 points" : "15 Points";
		String fourth_tier = loggedUser.getType() == UserType.ADMIN ? "100 points" : "20 Points";
	
			while (true) {
			System.out.println("How many points would you like to add?");
			System.out.println("\t1. " + first_tier);
			System.out.println("\t2. " + second_tier);
			System.out.println("\t3. " + third_tier);
			System.out.println("\t4. " + fourth_tier);
			System.out.println("\t0. Quit");
			int selection = select();
			Long points = 0L;
			
			switch(selection) {
			case 1:	// Buy 5 points for user, 20 points for admin
				points = loggedUser.getType() == UserType.ADMIN ? 20L : 5L;
				loggedUser.setPoints(loggedUser.getPoints() + points);
				UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);
				UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
				System.out.println("Success!\nYou now have " + loggedUser.getPoints() + " points.");
				return;
			case 2:	// Buy 10 points for user, 50 points for admin
				points = loggedUser.getType() == UserType.ADMIN ? 50L : 10L;
				loggedUser.setPoints(loggedUser.getPoints() + points);
				System.out.println("Success!\\nYou now have " + loggedUser.getPoints() + " points.");
				UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);
				UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
				return;
			case 3: // Buy 15 points for user, 70 points for admin
				points = loggedUser.getType() == UserType.ADMIN ? 70L : 15L;
				loggedUser.setPoints(loggedUser.getPoints() + points);
				System.out.println("Success!\\nYou now have " + loggedUser.getPoints() + " points.");
				UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);
				UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
				return;
			case 4:	// Buy 20 points for user, 100 for admin
				points = loggedUser.getType() == UserType.ADMIN ? 100L : 20L;
				loggedUser.setPoints(loggedUser.getPoints() + points);
				UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);
				UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
				System.out.println("Success!\\nYou now have " + loggedUser.getPoints() + " points.");
				return;
			case 0: // Quit
				return;
			default: System.out.println("Invalid input, use numbers to select option");
				continue;
			}
		}		
	}
	
	private void admin(User admin) {
		log.trace("Admin menu loading...");
		while(true) {
			switch(adminMenu()) {
			case 1:		// Add new user
				System.out.println("Loading options to add new user");
				UserDAO.addUser();
				break;
			case 2:		// Approve pending users
				System.out.println("Loading pending users list..");
				as.approveUsers();
				break;
			case 3:		// Approve pending titles
				System.out.println("Loading pending games list..");
				as.approveTitles();
				break;
			case 4:		// Manage users - sub menu to add, view, modify and delete accounts
				as.manageUsers(admin);
				break;
			case 5:		// Manage titles - sub menu to add, view, modify and delete games
				as.manageTitles(admin);
				break;
			case 6:		// Rent title
				GameService.rentGame(admin);
				break;
			case 7:		// Buy title
				GameService.buyGame(admin);
				break;
			case 8:		// Return title
				System.out.println("You currently have " + loggedUser.getPoints() + " points.");
				GameService.returnGame(admin);
				break;
			case 9:		// Add points
				System.out.println("You currently have " + loggedUser.getPoints() + " points.");
				pointsMenu();
				break;
			case 0:
				System.out.println(loggedUser.getUsername() + " signing off..");
				loggedUser = null;
				return;
			default:
				System.out.println("Invalid entry in admin menu. Try again using numbers to select option");
				continue;
			}
		}
	}
	
	private int adminMenu() {
		System.out.println("What would you like to do?");
		System.out.println("\t1. Add User");
		System.out.println("\t2. Approve Pending Users");
		System.out.println("\t3. Approve Pending Titles");		
		System.out.println("\t4. Manage Users");
		System.out.println("\t5. Add new title");		
		System.out.println("\t6. Use Points: Rent title");
		System.out.println("\t7. Use Points: Buy title");
		System.out.println("\t8. Return title");
		System.out.println("\t9. Buy more points");
		System.out.println("\t0. Logout");
		return select();
	}
	
	public static int select() {
		Scanner scan = SingletonScanner.getScanner().getScan();
		int selection;
		try {
			selection = Integer.parseInt(scan.nextLine());
		} catch(NumberFormatException e) {
			selection = -1;
//			e.printStackTrace();
		}

		//log
		return selection;
	}

}
