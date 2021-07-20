package com.revature.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.revature.model.User;
import com.revature.model.UserType;

public class UserDAO implements Serializable {
	private static final long serialVersionUID = 7426075925303078799L;

	private static String user_file = "users.dat";
	private static String admin_file = "admins.dat";
	private static String pending_ufile = "pending_users.dat";
	
	public static String user_msg = "Welcome back!";
	public static String admin_msg = null;
	
	public static List<User> pending_users;
	public static HashMap<Integer, User> users;
	public static Integer first_uid = 100;
	public static Integer last_uid = 999;
	public static HashMap<Integer, User> admins;
	public static Integer first_aid = 0;
	public static Integer last_aid = 99;
		
	
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
			users.put(UserDAO.first_uid+1, (new User(UserDAO.first_uid+1, "default", "default@gameshop.com", LocalDate.of(2010, 8, 21), UserType.GAMER)));
			
//			Collection<User> values = users.values();
			
//			u_list = users.values().stream().collect(Collectors.toList());
			System.out.println("First User: " + users.get(UserDAO.first_uid+1));

			ds.writeObjectsToFile(u_list, user_file);
		}
		
		if(a_list != null) {
			admins = populateMap(a_list);
		}
		
		else if(a_list == null) {	// If no admins exist in the admins.dat file create new admin
			admins = new HashMap<Integer, User>();
			admins.put(UserDAO.first_aid+1, (new User(UserDAO.first_aid+1, "xspark", "stephen.gxs@gmail.com", LocalDate.of(1951, 8, 21), UserType.ADMIN)));
			System.out.println("First Admin: " + admins.get(UserDAO.first_aid+1));

			a_list = new ArrayList<User>(admins.values());
			ds.writeObjectsToFile(a_list, admin_file);
		}
	}
	
	public User getUser(Integer id) {
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
	
	public void addPendingUser(User p_user) {
		pending_users.add(p_user);
		writeToFile(pending_users, "pending_users.dat");		
	}
	
	public void addUser() {
		// Get admin input for account details
		
	}
	
	public void addUser(String username, String email, LocalDate birthday, UserType type) {
		
	}
	
	public void addUser(Integer id, String username, String email, LocalDate birthday, UserType type) {
		
	}
	
	public boolean deleteUser(String username) {
		return false;
	}
	
	public void modifyUser(String username) {
		// Use menu to modify certain properties of users
	}
	
	public void modifyUser(Integer id) {
		// Use menu to modify certain properties of users
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
	
	public static void writeToFile(List<User> users, String filename) {
		new DataSerializer<User>().writeObjectsToFile(users, filename);
	}
}
