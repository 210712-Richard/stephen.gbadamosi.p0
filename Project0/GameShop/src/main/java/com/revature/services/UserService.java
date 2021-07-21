package com.revature.services;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.revature.data.UserDAO;
import com.revature.model.User;
import com.revature.model.UserType;
import com.revature.util.SingletonScanner;

public class UserService {
	private static UserDAO udao = new UserDAO();

	public User login(String uname) {
		User u = null;
		List<User> u_list = new ArrayList<User>(udao.getUsers());
		List<User> a_list = new ArrayList<User>(udao.getAdmins());
		for(int i = 0; i < u_list.size(); i++) {
			if(u_list.get(i).getUsername().equalsIgnoreCase(uname)) {
				u = u_list.get(i);
			}
		}
		for(int j = 0; j < a_list.size(); j++) {
			if(a_list.get(j).getUsername().equalsIgnoreCase(uname)) {
				u = a_list.get(j);
			}
		}
		System.out.println("Username not registered in database");

		return u;
	}
	
	public User login(Integer uid) {
		User u = null; 
		u = UserDAO.getUserbyID(uid);
		if (u == null) {
			System.out.println("No account exists with ID: " + uid + "Try again");
			return u;
		}
		
		System.out.println("Found " + u.getType() + " account with ID: " + uid);
		return u;
	}
	
	public boolean rentTitle(String title) {
		return false;
	}
	
	public boolean buyTitle(String title, String rating, LocalDate release_year) {	
		// Titles can be rejected depending on rating or release date (if inappropriate or future release date)
		
		return false;
	}
	
	public void requestTitle(String title) {
		
	}
	
	public void buyPoints() {
		
	}
	
	public void register(String username, String email, LocalDate birthday) {
		User u = new User(username, email, birthday);
		udao.addPendingUser(u);
		
	}

}
