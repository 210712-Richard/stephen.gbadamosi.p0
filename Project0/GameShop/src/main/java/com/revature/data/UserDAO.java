package com.revature.data;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.revature.menu.Menu;
import com.revature.model.Game;
import com.revature.model.GameStatus;
import com.revature.model.User;
import com.revature.model.UserType;
import com.revature.util.SingletonScanner;

public class UserDAO implements Serializable {
	private static final long serialVersionUID = 7426075925303078799L;

	public static String user_file = "users.dat";
	public static String admin_file = "admins.dat";
	public static String pending_ufile = "pending_users.dat";
	
	public static String user_msg = "Welcome back";
	public static String admin_msg = "";
	public static Scanner scan = SingletonScanner.getScanner().getScan();
	
	public static List<User> pending_users;
	public static HashMap<Integer, User> users;
	public static final Integer first_uid = 100;
	public static final Integer last_uid = 999;
	public static HashMap<Integer, User> admins;
	public static final Integer first_aid = 0;
	public static final Integer last_aid = 99;		
	
	static {

		DataSerializer<User> ds = new DataSerializer<>();
		List<User> a_list = new ArrayList<User>();
		List<User> u_list = new ArrayList<User>();
		pending_users = new ArrayList<User>();
	
		File file;
		
		try {
			file = new File("admins.dat");
			if(!file.exists()) {
				System.out.println("Admins file does not exist in working directory\n"
						+ "Creating new file..");
				writeToFile(a_list, admin_file);
	
			}
			else {
				System.out.println("Admins file already exists");
				a_list = ds.readObjectsFromFile(admin_file);
				System.out.println(a_list == null);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			file = new File("users.dat");
			if(file.createNewFile()) {
				System.out.println("Users file does not exist in working directory\n"
						+ "Creating new file..");	
				writeToFile(u_list, user_file);
			}
			else {
				System.out.println("Users file already exists");
				u_list = ds.readObjectsFromFile(user_file);
			}
		} catch(Exception e) {
			System.out.println("User file does not exist in working directory\n"
					+ "Creating new file..");
		}
		
		try {
			file = new File("pending_users.dat");
			if(!file.exists()) {
				System.out.println("Pending users file does not exist in working directory\n"
						+ "Creating new file..");	
				writeToFile(pending_users, pending_ufile);
			}
			else {
				System.out.println("Pending users file already exists");
				pending_users = ds.readObjectsFromFile(pending_ufile);
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("There are no pending users");
		}
		
		if(u_list != null && u_list.size() > 0) {
		//	System.out.println(u_list.get(0));
			users = populateMap(u_list);
		}
		else {
			users = new HashMap<Integer, User>();
			User u = new User(first_uid+1, "default", "default@gameshop.com", LocalDate.of(2010, 8, 21), UserType.GAMER);
			if(u != null) {
				users.put(u.getId(), u);
			}
			
			System.out.println("First User: " + users.get(first_uid+1));
			u_list = new ArrayList<User>(users.values());
			ds.writeObjectsToFile(u_list, user_file);
		}
		
		if(a_list != null && a_list.size() > 0) {
			admins = populateMap(a_list);
		}
		
		else {	// no admins exist in the admins.dat file create new admin
			admins = new HashMap<Integer, User>();
			User u = new User(first_aid+1, "xspark", "stephen.gxs@gmail.com", LocalDate.of(1951, 8, 21), UserType.ADMIN);
			if (u != null) {
				admins.put(u.getId(), u);
				System.out.println("First Admin: " + admins.get(first_aid+1));
			}
			
			a_list = new ArrayList<User>(admins.values());
			ds.writeObjectsToFile(a_list, admin_file);
		}

		if(pending_users == null || pending_users.size() == 0) {	// Verify there are no pending account approvals, else present to admin			admin_msg = "No tasks pending";
			admin_msg = "No users pending approval";
			System.out.println(admin_msg);
		}
		
		else {
			
			admin_msg = "Users pending approval, please review";
			System.out.println("Pending users list: " + pending_users.toString());
		}		
	}	
	
	public static int checkID(Integer id, UserType type) {
		// id is 0 if we want it auto-assigned otherwise try to use requested id if available
		int uid = id > 0 ? id : UserDAO.first_aid + 1;
		System.out.println("Checking that UID: " + uid + " is valid...");
		
		if(UserDAO.getUserbyID(uid) != null) { // ID already in use
			System.out.println("ID already in use, searching for available ID");
		}

		if(type == UserType.ADMIN) {
			while (UserDAO.getUserbyID(uid) != null && uid < UserDAO.last_aid) { // ID already in use
				uid++;
				if (uid == 100) { // All admin spots filled, block account creation
					System.out.println("Unable to create new admin account, try again later");
					return 0;
				}
			}
			System.out.println("Admin ID returned from checkID: " + uid);
			return uid;
		}		
		else {	// User ID requested
			uid = UserDAO.first_uid + 1;
			while(UserDAO.getUserbyID(uid) != null && uid < UserDAO.last_uid) {
				uid++;
				if(uid == UserDAO.last_uid) {	// All user spots filled, block account creation
					System.out.println("Unable to create new user account, try again later");
					return 0;
				}
			}
			System.out.println("User ID returned from checkID: " + uid);
			return uid;
		}
	}
	
	// Returns false if there is a conflict with username check otherwise returns true	
	public static boolean checkUsername(String username) {
		System.out.println("Checking " + username + " for validity..");
		boolean status = false;
		
		if(username == null || username.length() < 3) {
			System.out.println("Invalid username. Username must be at least 3 characters long");
			return false;
		}
		
		if(users != null && users.size() > 0) {
			List<User> users_list = new ArrayList<User>(users.values());
			for(User u : users_list) {			
				if(u.getUsername().equalsIgnoreCase(username)) {
					System.out.println("Username in use");		
					return false;
				}
			}
		}

		if(admins != null && admins.size() > 0) {
			List<User> admins_list = new ArrayList<User>(admins.values());		
			for(User a : admins_list) {
				if(a.getUsername().equalsIgnoreCase(username)) {
					System.out.println("Invalid username entered - user already exists");
					return status;
				}
			}
		}
	
		System.out.println("Success! Username is valid");
		status = true;		

		return status;
	}
	
	public static boolean checkEmail(String email) {
		System.out.println("Checking email for validity..");
		boolean status = false;
		status = (email.trim().indexOf("@") > 0 && email.trim().lastIndexOf(".") > email.trim().indexOf("@"));
		System.out.println("Email check returned " + status);
		return status;
	}
	
	// Returns false if birthday fails to satisfy age requirements for account type
	public static boolean checkBirthday(LocalDate birthday, UserType type) {
		LocalDate limit = LocalDate.now();
		System.out.println("Entering Birthday check for " + type + " account type");
		if (type == UserType.ADMIN) {
			limit.minus(Period.of(18, 0, 0));
			limit.plus(Period.of(0, 0, 1));
			if (birthday.isAfter(limit)) { // Admin is less than 18 yrs old, deny request
				System.out.println("Minimum age for admin accounts is 18 years and older\n"
						+ "Account creation failed");
				return false;
			}
		}
		
		else {
			limit.minus(Period.of(10, 0, 0));
			limit.plus(Period.of(0, 0, 1));
			if (birthday.isAfter(limit)) { // User is less than 10 yrs old, deny request
				System.out.println("Minimum age for admin accounts is 18 years and older\n"
						+ "Account creation failed");
				return false;
			}
		}
		System.out.println("Birthday check passed");
		return true;
	}
	
	public static User getUser(String username) {
		String target = username;
		if (username == null || username.equals("")) {
			System.out.println("Enter a username or ID: ");
			target = scan.nextLine();
		}
		if(target == null || target.equals("")) {
			System.out.println("Invalid input, try again.");
			return null;
		}
		target.trim();
		System.out.println("Searching for user " + target);
		
		int uid;
		User user = null;
		if(target.matches("^\\d+")) {	// Is Integer
			uid = Integer.parseInt(target);
			user = getUserbyID(uid);
			if(user == null) {
				System.out.println("No userfound with ID provided");
				return null;
			}
			else {
				System.out.println("Found user with ID: " + uid + " , Username: " + user.getUsername());
				return user;
			}
		}
		
		System.out.println("Target user: " + target);
		List<User> user_list = getUsers();
		List<User> admin_list = getAdmins();		
		for(User u : user_list) {
			System.out.println(u.toString());
			System.out.println("User: " + u.getUsername() + " == " + target + " ? "
					+ u.getUsername().equalsIgnoreCase(target));
			if(u.getUsername().equalsIgnoreCase(target)) {
				user = u;
				System.out.println("Found user with username: " + user.getUsername());
				return user;
			}
		}
		for(User admin : admin_list) {
			System.out.println(admin.toString());
			System.out.println("User: " + admin.getUsername() + " == " + target + " ? "
					+ admin.getUsername().equalsIgnoreCase(target));
			if(admin.getUsername().equalsIgnoreCase(target)) {
				user = admin;
				System.out.println("Found admin with username: " + user.getUsername());
				return user;
			}
		}
		
		System.out.println("Username not registered in database");
		return user;
	}
	
	public static User getUserbyID(Integer id) {
		if(users.size() == 0) {
			return null;
		}
		
		return users.get(id) != null ? users.get(id) : admins.get(id);
	}
	
	public static List<User> getUsers() {
		return new ArrayList<User>(users.values());
	}
	
	public static List<User> getAdmins() {
		return new ArrayList<User>(admins.values());
	}
	
	public static List<User> getPendingUsers() {
		return pending_users;
	}
		
	public static String getFirstName() {
		System.out.println("Enter First Name: ");
		String new_name = scan.nextLine();
		new_name.trim(); // sanitize input
		if((new_name != null || !new_name.equals("")) && alphabetOnly(new_name)) {
			System.out.println("Name updated to: " + new_name);
			return new_name;
		}
		System.out.println("Name not saved due to invalid or missing input");
		return "";
	}
	
	public static String getLastName() {
		System.out.println("Enter Last Name: ");
		String last_name = scan.nextLine();
		last_name.trim(); // sanitize input
		if((last_name != null || !last_name.equals("")) && alphabetOnly(last_name)) {
			System.out.println("Last name updated to: " + last_name);
			return last_name;
		}
		System.out.println("Name not saved due to invalid or missing input");
		return "";	}
	
	public static String getNewUsername(String curr_uname) {
		System.out.println("Choose username: ");
		String new_uname = scan.nextLine();
		new_uname.trim();
		if(new_uname == null || new_uname.equalsIgnoreCase(curr_uname)) {
			return curr_uname;
		}
		
		if(!checkUsername(new_uname)) {
			System.out.println("Invalid username entered in getNew Username");
			return null;
		}
		
		return new_uname;
	}
	
	public static String getEmail() {
		System.out.println("Enter the email address to use: ");
		String email = scan.nextLine();
		if(checkEmail(email)) {
			return email;
		}
		System.out.println("Failed to verify email");
		return "";
	}
	
	public static LocalDate getNewBirthday(UserType type) {
		System.out.println("Enter birthday (YYYY/MM/DD): ");
		String s = scan.nextLine();
		String[] bday = s.split("/");
		if(bday.length < 3) {
			System.out.println("Invalid Birthday entered. No changes saved");
			return null;
		}
		
		LocalDate birthday = LocalDate.of(Integer.parseInt(bday[0]), Integer.parseInt(bday[1]), Integer.parseInt(bday[2]));
		if(!checkBirthday(birthday, type)) {
			System.out.println("Not old enough, could not complete registration for account type"
					+ " due to age restriction.");
			birthday = null;
		}
		if(!checkBirthday(birthday, type)) {
			System.out.println("Not old enough, could not complete registration for account type"
					+ " due to age restriction.");
			birthday = null;
		}
		
		return birthday;
	}
	public static int getNewID(UserType type) {
		System.out.println("Enter a number for new account ID: ");
		int new_id;
		
		while(true) {
			String input = scan.nextLine();
			if(input.equals(""))	// Skip ID update / check
				return -1;
			
			try {
				new_id = Integer.parseInt(input);
				new_id = checkID(new_id, type);
				System.out.println("Attempting to update user ID to: " + new_id);
				if(new_id <= 0) {
					System.out.println("ID already in use, try another");
					continue;
				}
				else
					return new_id;
			} catch(Exception e) {
				System.out.println("Invalid ID input, try again");
				continue;
			}
		}
				
	}
	
	public static UserType getNewType() {
		switch(typeMenu()) {
			case 1:
				System.out.println("Setting user type to Customer");
				return UserType.CUSTOMER;
			case 2:
				System.out.println("Setting user type to Gamer");
				return UserType.GAMER;
			case 3:
				System.out.println("Setting user type to Admin");
				return UserType.ADMIN;
			case 0:
				break;
			default:
				System.out.println("Invalid Selection.. No changes made to account type");
				break;
		}
		return null;
	}
	
	public static Long changePoints() {
		System.out.println("Enter number of points for this account");
		String input = scan.nextLine();
		if(input == null || input.equals("")) {
			System.out.println("Invalid input for points - no changes saved");
			return 0L;
		}
		
		System.out.println("Enter number of points for this account: ");
		Long points = 0L;
		try {
			points = Long.parseLong(input);
			if(points > 400L) {
				System.out.println("Don't be greedy... 200 points assigned instead");
				points = 200L;
			}
			System.out.println("Number of points = " + points);
		} catch(Exception e) {
			System.out.println("Invalid input for points");
			e.printStackTrace();
		}
		return points;
	}
	
	public void addPendingUser(User p_user) {
		if(pending_users == null) {
			pending_users = new ArrayList<User>();
		}
		if(p_user == null) {
			System.out.println("Account creation failed, unable to add pending user");
			return;
		}
		System.out.println("Registration successful. Added new user to pending user queue");
		pending_users.add(p_user);
		writeToFile(pending_users, "pending_users.dat");		
	}
		
	public static void addUser() {
		// Get user input for account details
		System.out.println("Please enter account details for new user: ");
		String name = getFirstName();
		String last_name = getLastName();
		String uname = getNewUsername("");
		String email = getEmail();
		UserType type = getNewType();
		type = (type == null ? UserType.CUSTOMER : type);
		LocalDate birthday = getNewBirthday(type);
		User new_user;
		
		if(birthday == null) {
			System.out.println("Unable to set birthday for user. Adding new user failed, try again");
			return;
		}
		
		new_user = new User(name, last_name, uname, email, birthday, type);
			
		if(type == UserType.ADMIN)
			admins.put(new_user.getId(), new_user);
		else
			users.put(new_user.getId(), new_user);

		writeToFile(getAdmins(), admin_file);
		writeToFile(getUsers(), user_file);

		
		System.out.println("Success! Added new " + type + ": " + new_user.getUsername());
	}
	
	public static User addUser(String username, String email, LocalDate birthday, UserType type) {
		// Get user input for account details
		System.out.println("Entering add user constructor");
		User new_user = new User(username, email, birthday, type);
		
		if(new_user != null && new_user.getUsername() != null) {		
			if(new_user.getType() == UserType.ADMIN) {
				admins.put(new_user.getId(), new_user);
				List<User> admin_list = getAdmins();
				writeToFile(admin_list, admin_file);
				System.out.println("Success! Added new admin: " + new_user.getUsername());
			}
			
			else {	// Add new customer			
				users.put(new_user.getId(), new_user);
				List<User> user_list = getUsers();
				writeToFile(user_list, user_file);
				System.out.println("Success! Added new user: " + new_user.getUsername());
			}
		}
		
		return new_user;
	}
	
	public static boolean deleteUser(User user, String del_username) { // Move to admin service
		System.out.println("Entering delete user menu..");
		
		boolean status = false;
		// Verify user is admin
		if(user.getType() == UserType.ADMIN) {
			// Search user by username / ID
			User del_user = getUser(del_username);
			
			// Remove User
			if(del_user != null) {
				System.out.println("Found User: " + del_user.getUsername());
				System.out.println("Are you sure you want to delete this User: " + del_user.getUsername()
						+ "?\n(Y)es or (N)o to continue");			
				String confirm = scan.nextLine();
				confirm.trim();
				
				if(confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y") || confirm.equals("")) {
					// scan game inventory to reset values
					for(Game g : del_user.inventory) {
						if(g.status == GameStatus.RENTED) {
							g.status = GameStatus.AVAILABLE;
							g.rentDate = null;
							g.returnDate = null;
							g.rentedBy = null;
						}
					}
					
					if(del_user.getType() == UserType.ADMIN) {
						System.out.println("Removing admin: " + del_user.getUsername());
						admins.remove(del_user.getId(), del_user);
						List<User> admin_list = getAdmins();
						writeToFile(admin_list, admin_file);

					}
					else {
						System.out.println("Removing User: " + del_user.getUsername() + " from database");
						users.remove(del_user.getId(), del_user);	
						List<User> user_list = getUsers();
						writeToFile(user_list, user_file);
												
						status = true;
					}
					
					GameDAO.writeToFile(GameDAO.games, GameDAO.games_file);
					return status;
				}
				if(confirm.equalsIgnoreCase("no") || confirm.equalsIgnoreCase("n")) {
					System.out.println("Delete operation cancelled by admin");
					return false;
				}
			}
			System.out.println("Delete failed. Double check username and try again");
		}
		
		else
			System.out.println("You don't have permission for the requested action. Please login as an admin then try again");

		return status;
	}
	
	public static HashMap<Integer, User> populateMap(List<User> list) {
		HashMap<Integer, User> newMap = null;
		if(list != null) {
			newMap = new HashMap<Integer, User>();
			for(int i = 0; i < list.size(); i++) {
				newMap.put(list.get(i).getId(), list.get(i));
			}
		}
		
		return newMap;		
	}
	
	private static int typeMenu() {
		System.out.println("called UserType Menu()");
		
		System.out.println("Please select the new account type");
		System.out.println("\t1. Customer");
		System.out.println("\t2. Gamer");
		System.out.println("\t3. Admin");
		System.out.println("\t0. No change");
		int selection = Menu.select();
		return selection;
	}
	
	public static boolean alphabetOnly(String str)
	{
		return ((!str.equals(""))
				&& (str != null)
				&& (str.matches("^[a-zA-Z]*$")));
	}
	
	public static void writeToFile(List<User> users, String filename) {
		new DataSerializer<User>().writeObjectsToFile(users, filename);
	}
}