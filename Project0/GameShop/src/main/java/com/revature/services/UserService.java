package com.revature.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.revature.data.GameDAO;
import com.revature.data.UserDAO;
import com.revature.model.Game;
import com.revature.model.GameRating;
import com.revature.model.GameStatus;
import com.revature.model.User;
import com.revature.model.UserType;

public class UserService {
	private static UserDAO udao = new UserDAO();

	public User login(String uname) {
		User u = UserDAO.getUser(uname);
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
	
	public void updateAccount(User u) {
		
		u.setName(UserDAO.getFirstName());
		u.setLastName(UserDAO.getLastName());
		u.setUsername(UserDAO.getNewUsername(u.getUsername()));
		u.setEmail(UserDAO.getEmail());
		System.out.println("Account updated");
		
		List<User> user_list = new ArrayList<User>(UserDAO.users.values());
		UserDAO.writeToFile(user_list, UserDAO.user_file);
	}
	
	public boolean requestTitle(User u) {	
		// Titles can be rejected depending on rating or release date (if inappropriate or future release date)
		String title = GameDAO.getTitle();
		GameRating rating = GameDAO.getRating();
		LocalDate release = GameDAO.getReleaseDate();
		LocalDate now = LocalDate.now().plusDays(1);
		if(!GameDAO.checkTitle(title)) {
			if(rating == GameRating.PG13) {
				if(u.getBirthday().isAfter(now.minusYears(13))) {
					System.out.println("Game rating restriction prevented requesting this title - Sorry.. but not old enough");
					return false;
				}
			}
			if(rating == GameRating.MA) {
				if(u.getBirthday().isAfter(now.minusYears(17))) {
					System.out.println("Game rating restriction prevented requesting this title - Sorry.. but not old enough");
					return false;
				}
			}
		}
		
		else {
			System.out.println("Requested title found in Game inventory");
			return false;
		}
		
		Game p_game = new Game(title, rating, release, GameStatus.PENDING);
		GameDAO.pending_games.add(p_game);
		GameDAO.writeToFile(GameDAO.pending_games, GameDAO.pending_gfile);	
		
		return true;
	}
	
	public void buyPoints() {
		
	}
	
	public void register() {
		String name, last_name, username, email;
		LocalDate birthday;
		name = UserDAO.getFirstName();
		last_name = UserDAO.getLastName();
		username = UserDAO.getNewUsername("");
		email = UserDAO.getEmail();
		birthday = UserDAO.getNewBirthday(UserType.PENDING);
		
		User u = new User(name, last_name, username, email, birthday, UserType.PENDING);
		udao.addPendingUser(u);
		
		UserDAO.writeToFile(UserDAO.pending_users, UserDAO.pending_ufile);
	}
	
	public User register(String username, String email, LocalDate birthday) {
		User u = null;
		if(!UserDAO.checkUsername(username)) {
			u = new User(username, email, birthday, UserType.PENDING);
			udao.addPendingUser(u);

			UserDAO.writeToFile(UserDAO.pending_users, UserDAO.pending_ufile);

			return u;
		}
		else
			System.out.println("Registration failded due to invalid username. Try another");
		
		return u;
	}

}
