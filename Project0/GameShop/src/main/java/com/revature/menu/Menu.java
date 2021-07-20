package com.revature.menu;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.model.*;
import com.revature.services.*;
import com.revature.util.SingletonScanner;

// Encapsulate the user interface methods
public class Menu {

	private static final Logger log = LogManager.getLogger(Menu.class);
	
	private UserService us = new UserService();
	private User loggedUser = null;
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
				if(username.matches("^\\d+")) {
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
					System.out.println("Welcome back: "+u.getUsername());
					// call our next method (either the Player menu or the Admin menu, depending on user)
					log.info("Successful login for user: "+loggedUser);
					switch(loggedUser.getType()) {
					case PENDING:
						System.out.println("Your account is pending review. You can login to "
								+ "access services once it is confirmed by an admin");
						break;
					case CUSTOMER:
					case GAMER:
						gamer();
						break;
					
					case ADMIN:
						admin();
						break;
					}
				}
				break;
			case 2:
				// register
				break;
			case 3:
				// quit
				System.out.println("Thanks for using GameShop Online - Goodbye!");
				break mainLoop;
			default:
				// invalid selection
				System.out.println("Not a valid selection, please try again.");
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
		System.out.println("\t3. Quit");
		int selection = select();
		log.trace("Start menu returning selection: "+selection);
		return selection;
	}
	
	private void gamer() {
		log.trace("Gamer menu loading...");
		player: while(true) {
			switch(gamerMenu()) {
			case 1:	// View Account Details
				break;
			case 2:	// Rent title
				break;
			case 3:	// Buy title
				break;
			case 4:	// Request title
				break;
			case 5:	// View points
				System.out.println("You currently have " + loggedUser.getPoints() + " points.");
				break;
			case 6:	// Buy points
				break;
			case 0:
				loggedUser = null;
				break player;
			default:
			}
		}
	}
	
	private int gamerMenu() {
		System.out.println("What would you like to do?");
		System.out.println("\t1. View Account Details");
		System.out.println("\t2. Use Points: Rent Title");
		System.out.println("\t3. Use Points: Buy Title");
		System.out.println("\t4. Request new Title");
		System.out.println("\t5. View Points");
		System.out.println("\t6. Buy Points");
		System.out.println("\t0. Logout");
		return select();
	}
	
	private void admin() {
		log.trace("Admin menu loading...");
		admin: while(true) {
			switch(adminMenu()) {
			case 1:
				break;
			case 2:	// Approve pending users
				break;
			case 3:	// Approve pending titles
				break;
			case 4:	// Manage users - sub menu to add, view, modify and delete accounts
				break;
			case 5:	// Manage titles - sub menu to add, view, modify and delete games
				break;
			case 6:	// Rent title
				break;
			case 7:	// Buy title
				break;
			case 8:	// Buy points
				break;
			case 9:	// View points
				System.out.println("You currently have " + loggedUser.getPoints() + " points.");
				break;
			case 0:
				loggedUser = null;
				break admin;
			default:
			}
		}
	}
	
	private int adminMenu() {
		System.out.println("What would you like to do?");
		System.out.println("\t1. View Account Details");
		System.out.println("\t2. Approve Pending Users");
		System.out.println("\t3. Approve Pending Titles");		
		System.out.println("\t4. Manage Users");
		System.out.println("\t5. Add new title");		
		System.out.println("\t6. Use Points: Rent title");
		System.out.println("\t7. Use Points: Buy title");
		System.out.println("\t8. Buy more points");
		System.out.println("\t9. View P`oints");
		System.out.println("\t0. Logout");
		return select();
	}
	
	private int select() {
		int selection;
		try {
			selection = Integer.parseInt(scan.nextLine());
		} catch(Exception e) {
			selection = -1;
			e.printStackTrace();
		}
		
		//log
		return selection;
	}

}
