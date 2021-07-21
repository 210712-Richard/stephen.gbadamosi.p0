package com.revature.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.revature.model.User;
import com.revature.model.UserType;
import com.revature.services.UserService;
import com.revature.util.SingletonScanner;

public class UserDAO implements Serializable {
	private static final long serialVersionUID = 7426075925303078799L;

	private static String user_file = "users.dat";
	private static String admin_file = "admins.dat";
	private static String pending_ufile = "pending_users.dat";
	
	public static String user_msg = "Welcome back!";
	public static String admin_msg = null;
	
	public static List<User> pending_users;
	public static HashMap<Integer, User> users;
	public static final Integer first_uid = 100;
	public static final Integer last_uid = 999;
	public static HashMap<Integer, User> admins;
	public static final Integer first_aid = 0;
	public static final Integer last_aid = 99;		
	
	static {

		DataSerializer<User> ds = new DataSerializer<>();
		List<User> a_list = ds.readObjectsFromFile(admin_file);
		List<User> u_list = ds.readObjectsFromFile(user_file);
//		List<User> p_list = ds.readObjectsFromFile(pending_ufile);
				
//		if(p_list == null) {	// Verify there are no pending account approvals, else present to admin
//			admin_msg = "No tasks pending";
//		}
		
		if(u_list != null) {
		//	System.out.println(u_list.get(0));
			users = populateMap(u_list);
		}
		else if(u_list == null) {
			users = new HashMap<Integer, User>();
			User u = new User(first_uid+1, "default", "default@gameshop.com", LocalDate.of(2010, 8, 21), UserType.GAMER);
			if(u != null) {
				users.put(u.getId(), u);
			}
//			Collection<User> values = users.values();
			
//			u_list = users.values().stream().collect(Collectors.toList());
			System.out.println("First User: " + users.get(first_uid+1));
			u_list = new ArrayList<User>(users.values());
			ds.writeObjectsToFile(u_list, user_file);
		}
		
		if(a_list != null) {
			admins = populateMap(a_list);
		}
		
		else if(a_list == null) {	// If no admins exist in the admins.dat file create new admin
			admins = new HashMap<Integer, User>();
			User u = new User(first_aid+1, "xspark", "stephen.gxs@gmail.com", LocalDate.of(1951, 8, 21), UserType.ADMIN);
			if (u != null) {
				admins.put(u.getId(), u);
				System.out.println("First Admin: " + admins.get(first_aid+1));
			}
			
			a_list = new ArrayList<User>(admins.values());
			ds.writeObjectsToFile(a_list, admin_file);
		}
	}
	
	
	public static int checkID(Integer id, UserType type) {
		int uid = id != 0 ? id : UserDAO.first_aid + 1;
		System.out.println("Checking that UID: " + uid + " is valid...");
		if(UserDAO.getUserbyID(uid) != null) { // ID already in use
			System.out.println("ID already in use, searching for available ID");
		}

		if(type == UserType.ADMIN) {
			while (UserDAO.getUserbyID(uid) != null && uid < UserDAO.last_aid) { // ID already in use
				if (++uid == 100) { // All admin spots filled, block account creation
					System.out.println("Unable to create new admin account, try again later");
					return 0;
				}
			}
			return uid;
		}		
		else {	// User ID requested
			uid = UserDAO.first_uid + 1;
			while(UserDAO.getUserbyID(uid) != null && uid < UserDAO.last_uid) {
				if(++uid == UserDAO.last_uid) {	// All user spots filled, block account creation
					System.out.println("Unable to create new user account, try again later");
					return 0;
				}
			}
			return uid;
		}
	}
	
	public static boolean checkUsername(String username) {
		if(username == null || username.length() < 3) {
			System.out.println("Invalid username. Username must be at least 3 characters long");
			return false;
		}
		
		if(UserDAO.users != null) {
			List<User> users = new ArrayList<User>(UserDAO.users.values());
			for(User u : users) {
				if(u.getUsername().equalsIgnoreCase(username)) {
					System.out.println("Invalid Username, try another");
					return false;
				}
			}
		}

		if(UserDAO.admins != null) {
			List<User> admins = new ArrayList<User>(UserDAO.admins.values());		
			for(User u : admins) {
				if(u.getUsername().equalsIgnoreCase(username)) {
					System.out.println("Invalid username, try another");
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean checkEmail(String email) {
		if(email.indexOf("@") <= 0 || email.indexOf(".") <= 0) {
			System.out.println("Invalid email address. Try again");
			return false;
		}
		return true;
	}
	
	public static boolean checkBirthday(LocalDate birthday, UserType type) {
		LocalDate limit = LocalDate.now();
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
		return true;
	}
	
	public static User getUserbyID(Integer id) {
		if(users.size() == 0) {
			return null;
		}
		
		return users.get(id) != null ? users.get(id) : admins.get(id);
	}
	
	public List<User> getUsers() {
		return new ArrayList<User>(users.values());
	}
	
	public List<User> getAdmins() {
		return new ArrayList<User>(admins.values());
	}
	
	public List<User> getPendingUsers() {
		return pending_users;
	}
		
	public static String getFirstName() {
		Scanner scan = SingletonScanner.getScanner().getScan();

		System.out.println("Enter your First Name: ");
		String new_name = scan.nextLine();
		new_name.trim(); // sanitize input
		if((new_name != null || !new_name.equals("")) && alphabetOnly(new_name)) {
			System.out.println("Name updated to " + new_name);
			return new_name;
		}
		System.out.println("Name not changed due to invalid or missing input");
		return "";
	}
	
	public static String getLastName() {
		Scanner scan = SingletonScanner.getScanner().getScan();

		System.out.println("Enter your Last Name: ");
		String last_name = scan.nextLine();
		last_name.trim(); // sanitize input
		if((last_name != null || !last_name.equals("")) && alphabetOnly(last_name)) {
			System.out.println("Last name updated to " + last_name);
			return last_name;
		}
		System.out.println("Name not changed due to invalid or missing input");
		return "";	}
	
	public static String getUsername(String curr_uname) {
		Scanner scan = SingletonScanner.getScanner().getScan();

		System.out.println("Choose your username: ");
		String new_uname = scan.nextLine();
		if(new_uname.equals("") || new_uname.equalsIgnoreCase(curr_uname)) {
			return curr_uname;
		}
		
		if(!checkUsername(new_uname)) {
			System.out.println("Invalid username, please try again.");
			return null;
		}
		
		return new_uname;
	}
	
	public static String getEmail() {
		Scanner scan = SingletonScanner.getScanner().getScan();

		System.out.println("Enter the email address you'd like to use: ");
		String email = scan.nextLine();
		if(checkEmail(email)) {
			return email;
		}
		System.out.println("Failed to verify email");
		return "";
	}
	
	public static LocalDate getBirthday(UserType type) {
		Scanner scan = SingletonScanner.getScanner().getScan();

		System.out.println("Enter your birthday (YYYY/MM/DD): ");
		List<Integer> bday = Stream.of(scan.nextLine().split("/"))
				.map((str)->Integer.parseInt(str)).collect(Collectors.toList());
		
		LocalDate birthday = LocalDate.of(bday.get(0), bday.get(1), bday.get(2));
		if(!checkBirthday(birthday, type)) {
			System.out.println("Not old enough, please try again when you are older.");
		}
		
		return birthday;
	}
	
	public void addPendingUser(User p_user) {
		pending_users.add(p_user);
		writeToFile(pending_users, "pending_users.dat");		
	}
		
	public void addUser() {
		// Get user input for account details
		String name = getFirstName();
		String last_name = getLastName();
		String uname = getUsername("");
		String email = getEmail();
		LocalDate birthday = getBirthday(UserType.CUSTOMER);
		
		User u = new User(name, last_name, uname, email, birthday, UserType.CUSTOMER);
		System.out.println("Adding new user...");
		
		users.put(u.getId(), u);
	}
	
	public static void updateAccount(User u) {
		u.setName(UserDAO.getFirstName());
		u.setLastName(UserDAO.getLastName());
		u.setUsername(UserDAO.getUsername(u.getUsername()));
		u.setEmail(UserDAO.getEmail());
		System.out.println("Account updated");
	}
			
	public boolean deleteUser(String username) { // Move to admin service
		return false;
	}
	
	public void modifyUser(UserType type) {	// Move to admin service
		// Use menu to modify certain properties of users. Allow for users to skip change if existing value available
	}
	
	public void modifyAdmin(UserType type) { // Move to admin service
		// Use menu to modify certain properties of users. Allow for users to skip change if existing value available
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
