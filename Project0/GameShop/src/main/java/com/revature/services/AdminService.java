package com.revature.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.data.GameDAO;
import com.revature.data.UserDAO;
import com.revature.menu.Menu;
import com.revature.model.*;
import com.revature.util.SingletonScanner;

public class AdminService {

	private static final Logger log = LogManager.getLogger(Menu.class);
	private Scanner scan = SingletonScanner.getScanner().getScan();
	public static GameService gs = new GameService();
	public static UserDAO udao = new UserDAO();

	public void approveUsers() {
		// View list of pending users info
		System.out.println("Pending users list is currently: " 
		+ (UserDAO.pending_users.size() > 0? UserDAO.pending_users.toString() : "Empty"));

		if (UserDAO.pending_users != null || UserDAO.pending_users.size() > 0) {
			for(int i = 0; i < UserDAO.pending_users.size()+1; i++) {
				if(i == UserDAO.pending_users.size()) {
					System.out.println("Thanks for reviewing pending users!");
					UserDAO.pending_users.clear();
					System.out.println("Pending users list is now: " + UserDAO.pending_users.toString());
					UserDAO.writeToFile(UserDAO.pending_users, UserDAO.pending_ufile);
					return;
				}
				
				User u = UserDAO.pending_users.get(i);
				System.out.println(u.toString());
				
				log.trace("Review menu loading...");
				switch(reviewMenu()) {
				case 1:	// Registration approved
					int id = 0;
					id = UserDAO.checkID(0, u.getType());
					if(id < 101) {	// Failed to add new user - userbase full
						System.out.println("Unable to add new user, registration denied");
						break;
					}
					u.setId(id);
					if (!UserDAO.checkUsername(u.getUsername())) {
						u.setUsername("default" + u.getId());
						System.out.println("Username already exists.. "
								+ "default username assigned to new user: " + u.getUsername());
					}
					addRegisteredUser(u.getUsername(), u.getEmail(), u.getBirthday());
					break;
				case 2:	// Registration Denied
					System.out.println("Denied registration for user: " + u.getUsername());
					break;
				case 0:	// Quit
					System.out.println("Leaving pending users menu");
					return;
					
				default:
					// invalid selection
					System.out.println("Not a valid selection, please try again.");
				}
			}
		}

		
		// Approve or reject user registration
		
		// Remove user from pending list and add to user list (if approved)
		
		// Write update to files as relevant
		
	}
	
	public void approveTitles() {
		// View list of pending users info
				System.out.println("Pending games queue is currently: " 
				+ (GameDAO.pending_games.size() > 0 ? GameDAO.pending_games.toString() : "Empty"));

				if (GameDAO.pending_games.size() > 0) {
					for(int i = 0; i < GameDAO.pending_games.size()+1; i++) {
						if(i == GameDAO.pending_games.size()) {
							System.out.println("Pending games queue cleared!");
							GameDAO.pending_games.clear();
							System.out.println("Pending Games list is now: " + GameDAO.pending_games.toString());
							GameDAO.writeToFile(GameDAO.pending_games, GameDAO.pending_gfile);
							GameDAO.admin_msg_games = "";
							return;
						}
						
						Game game = GameDAO.pending_games.get(i);
						System.out.println(game.toString());
						
						log.trace("Review menu loading...");
						switch(reviewMenu()) {
						case 1:	// Registration approved
							Game check = GameDAO.getGame(game.title);
							if(check != null) {	// Failed to add new game - already in database
								System.out.println("Unable to add new Game, already exists in database");
								break;
							}
							game.setStatus(GameStatus.AVAILABLE);
							GameDAO.games.add(game);
							
							GameDAO.writeToFile(GameDAO.games, GameDAO.games_file);
							
							break;
						case 2:	// Registration Denied
							System.out.println("Denied request for new title: " + game.title);
							break;
						case 0:	// Quit - clear items reviewed prior to exit
							System.out.println("Clearing titles already reviewed from queue");
						
							for(int j = 0; j < i; j++) {
								System.out.println("Title removed from pending list: " + GameDAO.pending_games.remove(j).title);
							}
								
							System.out.println("Leaving pending games menu");
							return;
							
						default:
							// invalid selection
							System.out.println("Not a valid selection, please try again.");
						}
					}
				}
	}
	
	public void manageUsers(User logged_user) {
		if(logged_user.getType() == UserType.ADMIN) {	// Verify logged in user permissions
			
			
			while(true) {
				switch(userMenu()) {
					case 1: // Add a new user
						UserDAO.addUser();				
						break;
						
					case 2:	// View user
						System.out.println("Enter the ID or username of a user you'd like to view: ");
						String user_id = scan.nextLine();
						if(user_id == null || user_id.equals("")) {
							System.out.println("Invalid input for user search in view user menu. Try again");
							continue;
						}
						
						int uid;
						User user;
						if(user_id.matches("^\\d+")) {	// Is Integer
							uid = Integer.parseInt(user_id);
							user = UserDAO.getUserbyID(uid);
							if(user == null) {
								System.out.println("No userfound with ID provided");
							}
							else {
								Menu.printUser(user);
								break;
							}
						}
						else {
							user = UserDAO.getUser(user_id);
							if(user == null)
								System.out.println("No userfound with ID provided");
	
							else 
								Menu.printUser(user);
						}
						break;
					case 3:	// Modify user
						System.out.println("Enter the username/ID of the user to modify: ");
						String username = scan.nextLine();
						Integer id = 0;
						log.debug(username);

						User target_user = null;
						if(username == null || username.equals("")) {
							System.out.println("Invalid ID format in manage users menu - could not find user to modify");
							break;
						}
						if(username.matches("^\\d+")) {	// Is Integer
							try {
								id = Integer.parseInt(username);
								target_user = UserDAO.getUserbyID(id);
								if(target_user != null) {
//										System.out.println("Updating account info for the following user:\n" + target_user.toString());
									updateUser(target_user);
									return;
//										System.out.println("Account info saved");
								}
							} catch(Exception e) {
//								System.out.println("Invalid ID format in manage users menu");
//								e.printStackTrace();
							} 
						}
						else {
							target_user = UserDAO.getUser(username);
							if(target_user != null) {
				//					System.out.println("Updating account info for the following user:\n" + target_user.toString());
								updateUser(target_user);
								break;
				//					System.out.println("Account info saved");
							}
						}
					
						break;
					case 4:	// Delete user
						UserDAO.deleteUser(logged_user, null);
						break;
					case 0:	// Quit
						System.out.println("Exiting User menu..");
						return;					
				}
			}
		}
		else {
			System.out.println("Inadequate credentials. You need to be an admin to access this menu");
			return;
		}
	}
	
	public void manageTitles(User logged_user) {
		// View game inventory
		// Mark-down game prices
		// Rent or sell games to users
		// Add new titles
		
		if(logged_user.getType() != UserType.ADMIN) {
			System.out.println("Inadequate credentials. You need to be an admin to access this menu");
			return;
		}
		
		 while(true) {
			switch(titleMenu()) {
				case 1: // Add a new title
					Game game = GameDAO.addNewTitle();
					
					if(game != null)
						System.out.println("New Game: " + game.title + "added to inventory");
					else
						System.out.println("Adding new game failed. Try again");
					break;
				case 2:	// Modify title
					System.out.println("Switching to modify menu for games");
					gs.modifyGame(logged_user);
					break;
				case 3:	// Rent or sell title to user
					System.out.println("Would you like to Rent or sell a title?");		
					System.out.println("\t1. Rent Title");
					System.out.println("\t2. Buy Title");
					int selection = Menu.select();
					if(selection == 1) {
						GameService.rentGame(logged_user);
						break;
					}
					
					if(selection == 2) {
						GameService.buyGame(logged_user);
						break;
					}
					
					else {
						System.out.println("Invalid selection in title menu");
						continue;
					}
				case 4: // Delete game
					
				case 0:	// Quit
					System.out.println("Exiting Game title menu..");
					return;
					
			}
		}
	}
	
	public void addRegisteredUser(String uname, String email, LocalDate birthday) {
		// Get user input for account details		
		User u = new User(uname, email, birthday, UserType.CUSTOMER);
		System.out.println("Adding new user:\n" + u.toString());
		
		UserDAO.users.put(u.getId(), u);
		List<User> users_list = new ArrayList<User>(UserDAO.users.values());
		UserDAO.writeToFile(users_list, "users.dat");
	}
	
	public void updateUser(User user) {
		System.out.println("Found user " + user.getUsername() + " in database");
		UserType type = UserDAO.getNewType();
		type = type == null ? user.getType() : type;
		System.out.println("User Type set: " + type);
		System.out.println("Enter new user ID: ");
		String user_id = scan.nextLine();
		if(user_id == null || user_id.equals("") || Integer.parseInt(user_id) == user.getId()) {
			System.out.println("No changes made to user ID per input received");
		}
		
		else {
			int uid = 0;	
			if(user_id.matches("^\\d+")) {	// Is Integer
				try {
					uid = Integer.parseInt(user_id);
					if(uid == user.getId()) 	// User ID is same as new ID
						System.out.println("Requested ID is the same as current. No changes made");
					
					else {
						uid = UserDAO.checkID(uid, user.getType());
						if(uid <= 0)
							System.out.println("Requested ID not available. No changes made");
						
						else {
							user.setId(uid);
							System.out.println("New user ID: " + user.getId());
						}
					}
				} catch(Exception e) {
					System.out.println("Invalid input entered. No changes made");
				}
		//		int uid = UserDAO.getNewID(type);
			}
		}
	
		String username, name, last_name, email;

		username = UserDAO.getNewUsername(user.getUsername());
		name = UserDAO.getFirstName();
		name = (name == null ? user.getName() : name);
		last_name = UserDAO.getLastName();
		last_name = last_name == null ? user.getLastName() : last_name;
		email = UserDAO.getEmail();
		email = email == null ? user.getEmail() : email;
		LocalDate birthday = UserDAO.getNewBirthday(type);
		birthday = birthday == null ? user.getBirthday() : birthday;
		Long points = UserDAO.changePoints();

		username = (username == null || username.equals("")) ? user.getUsername() : username;
		System.out.println("Username to be used in update: " + username);
		user.setUsername(username);
		user.setName(name);
		user.setLastName(last_name);
		user.setEmail(email);
		user.setType(type);
		user.setBirthday(birthday);
		user.setPoints(points <= 0L ? user.getPoints() : points);
		System.out.println("Account details: " + user.toString());
		System.out.println("Account changes saved");
		
		if(type == UserType.ADMIN) {
			List<User> admin_list = UserDAO.getAdmins();
			UserDAO.writeToFile(admin_list, UserDAO.admin_file);
			System.out.println("Updated details for " + username);
		}
		
		if(type == UserType.CUSTOMER || type == UserType.GAMER) {
			List<User> user_list = UserDAO.getUsers();
			UserDAO.writeToFile(user_list, UserDAO.user_file);
			System.out.println("Updated details for " + username);
		}
		
		return;		
	}
	
	private int userMenu() {
		log.trace("called userMenu()");
		
		System.out.println("What would you like to do?");
		System.out.println("\t1. Add New User");
		System.out.println("\t2. View user");
		System.out.println("\t3. Modify user");
		System.out.println("\t4. Delete user");
		System.out.println("\t0. Quit");
		int selection = Menu.select();
		log.trace("Start menu returning selection: " + selection);
		return selection;		
	}	
	
	private int titleMenu() {
		log.trace("called titleMenu()");
		
		System.out.println("What would you like to do?");
		System.out.println("\t1. Add New Title");
		System.out.println("\t2. Modify Title");
		System.out.println("\t3. Rent or buy Title");
		System.out.println("\t0. Quit");
		int selection = Menu.select();
		log.trace("Start menu returning selection: " + selection);
		return selection;		
	}
	
	private int reviewMenu() {
		log.trace("called reviewMenu()");
		System.out.println("Approve or Deny?");
		System.out.println("What would you like to do?");
		System.out.println("\t1. Approve");
		System.out.println("\t2. Deny");
		System.out.println("\t0. Quit");
		int selection = Menu.select();
		log.trace("Start menu returning selection: " + selection);
		return selection;
	}
}
