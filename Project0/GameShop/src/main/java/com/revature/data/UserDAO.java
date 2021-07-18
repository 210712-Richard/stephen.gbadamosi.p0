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
	public static Integer curr_uid = 101;
	public static Integer last_uid = 999;
	public static HashMap<Integer, User> admins;
	public static Integer first_aid = 0;
	public static Integer curr_aid = 1;
	public static Integer last_aid = 99;
	
	
	static {
		DataSerializer<User> ds = new DataSerializer<User>();
		List<User> a_list = ds.readObjectsFromFile(admin_file);
		List<User> u_list = ds.readObjectsFromFile(user_file);
		
		
		// If no admins exist in the admins.dat file (first startup) then create new admin
		if(a_list == null) {
			admins = new HashMap<Integer, User>();
			admins.put(UserDAO.curr_aid, (new User(UserDAO.curr_aid++, "xspark", "stephen.gxs@gmail.com", LocalDate.of(1951, 8, 21), UserType.ADMIN)));
			
			a_list = new ArrayList<User>(admins.values());
			ds.writeObjectsToFile(a_list, admin_file);
		}
		
		if (u_list == null) {	// Verify there are no pending account approvals, else present to admin
			pending_users = ds.readObjectsFromFile(pending_ufile);
			if (pending_users == null) {
				admin_msg = "No tasks pending";
			}
			else {
				admin_msg = "Users pending approval";
			}

		}
	}
}
