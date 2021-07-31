package com.revature.controllers;

import java.time.LocalDate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.data.GameDAO;
import com.revature.data.UserDAO;
import com.revature.model.Game;
import com.revature.model.GameStatus;
import com.revature.model.User;
import com.revature.model.UserType;
import com.revature.services.UserService;

import io.javalin.http.Context;

public class UserControllerImpl implements UserController {
	
	private static Logger log = LogManager.getLogger(UserControllerImpl.class);
	private UserService us = new UserService();

	@Override
	public void register(Context ctx) {
		// TODO Auto-generated method stub
		User u = ctx.bodyAsClass(User.class);
		String username = u.getUsername();
		String email = u.getEmail();
		LocalDate birthday = u.getBirthday();
		boolean uname_check, email_check, bday_check;
		uname_check = UserDAO.checkUsername(username);
		email_check = UserDAO.checkEmail(email);
		bday_check = UserDAO.checkBirthday(birthday, UserType.CUSTOMER);
		if(uname_check == false) {
			ctx.status(409);
			ctx.html("Invalid username, try another");
			return;
		}
		if(email_check == false) {
			ctx.status(400);
			ctx.html("Invalid email address provided");
			return;
		}
		if(bday_check == false) {
			ctx.status(400);
			ctx.html("Problem with birthday. You must be 10 years or older to use this website");
			return;			
		}
		
		User newUser = us.register(username, email, birthday);
		if(newUser != null) {
			ctx.status(201);
			ctx.json(newUser);
		}
		else {
			ctx.status(423);
			ctx.html("Unable to create new user, try again later");
		}
		return;
	}

	@Override
	public void login(Context ctx) {
		// TODO Auto-generated method stub
		log.trace("Login method called");
		
		// Attempting to avoid creating a constructor that only accepts username to work around parsing ctx body 
		String input[] = ctx.body().split(":");
		log.debug(input[1]);
		String u = input[1].replace("\"", "");
		u = u.replace("}", "").trim();
		log.debug(u);

		
		// Use the request data to obtain the data requested
		User user = us.login(u);
		log.debug(u);
		
		// Create a session if the login was successful
		if(user != null) {
			// Save the user object as loggedUser in the session
			ctx.sessionAttribute("loggedUser", user);
			
			// Try to use the JSON Marshaller to send a JSON string of this object back to the client
			ctx.json(user);
			ctx.status(200);
			return;
		}
		
		// Send a 401 is the login was not successful
		ctx.status(401);
	}


	@Override
	public void logout(Context ctx) {
		// TODO Auto-generated method stub
		ctx.req.getSession().invalidate();
		ctx.status(204);
	}

	@Override
	public void getPoints(Context ctx) {
		// TODO Auto-generated method stub
		String username = ctx.pathParam("username");
		Long points = Long.parseLong(ctx.pathParam("count"));
		User loggedUser = ctx.sessionAttribute("loggedUser");
		User user = UserDAO.getUser(username);

		if(loggedUser == null) {
			ctx.status(401);
			return;
		}
		
		if(loggedUser.getType() != UserType.ADMIN && points > 20L) {
			ctx.status(401);
			ctx.html("Only admins can add more than 20 points at a time");
			return;
		}
		
		if(!loggedUser.getUsername().equals(username) && loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
			ctx.html("Cannot add points to any account other than your own");
			return;
		}
				
		if(user == null) {
			ctx.status(400);
			ctx.html("Invalid User");
			return;
		}
				
		user.setPoints(user.getPoints() + points);
		ctx.html("Total points for " + user.getUsername() + " is: " + user.getPoints());
		ctx.status(200);
		
		UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
		UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);

		return;
	}
	
	@Override
	public void viewPoints(Context ctx) {
		String username = ctx.pathParam("username");
		User loggedUser = (User) ctx.sessionAttribute("loggedUser");
		
		// if we aren't logged in or our username is different than the logged in username and we're not admin
		if(loggedUser == null) {
			ctx.status(401);
			return;
		}
		
		if(!loggedUser.getUsername().equals(username) && loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
			ctx.html("Cannot view points for any account other than your own");
			return;
		}
		User user = UserDAO.getUser(username);
		
		if(user == null) {
			ctx.status(400);
			ctx.html("Invalid User");
			return;
		}
			
		// otherwise we're golden
		ctx.json(user.getPoints());		
		ctx.status(200);
	}

	@Override
	public void rentTitle(Context ctx) {
		// TODO Auto-generated method stub
		String username = ctx.pathParam("username");
		String title = ctx.pathParam("title");
		Game game = GameDAO.getGame(title);
		User loggedUser = ctx.sessionAttribute("loggedUser");
		User user = UserDAO.getUser(username);

		if(loggedUser == null) {
			ctx.status(401);
			return;
		}
		
		if(!loggedUser.getUsername().equals(username) && loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
			ctx.html("Cannot rent a game for another user");
			return;
		}
		
		if(game == null) {
			ctx.status(404);
			ctx.html("No game with requested title found");
			return;
		}
		
		if(user == null) {
			ctx.status(400);
			ctx.html("Invalid User");
			return;
		}
			
		if(user.getPoints() < 5L) {
			ctx.status(402);
			ctx.html("Insufficient funds on " + user.getUsername() + " account");
			return;
		}
		
		if(game.status != GameStatus.AVAILABLE) {
			ctx.status(409);
			
			if(game.status == GameStatus.RENTED)
				ctx.html("Title currently unavailable for rent. It should be available after " +
			 game.returnDate);
			else
				ctx.html("Title currently unavailable for rent. Check back again later");
			
			return;
		}
		
		if(user.getType() == UserType.CUSTOMER) // Upgrade to gamer account type
			user.setType(UserType.GAMER);
		
		user.setPoints(user.getPoints() - 5L);
		user.inventory.add(game);
		game.setStatus(GameStatus.RENTED);
		game.status = GameStatus.RENTED;
		game.rentDate = LocalDate.now();
		game.returnDate = LocalDate.now().plusWeeks(1);
		game.rentedBy = user.getUsername();
		
		ctx.status(200);
		ctx.html("Game rented for a week. Due Date: " + game.returnDate.toString());
		
		UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
		UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);
		GameDAO.writeToFile(GameDAO.games, GameDAO.games_file);
		return;		
	}

	@Override
	public void returnTitle(Context ctx) {
		// TODO Auto-generated method stub
		String username = ctx.pathParam("username");
		String title = ctx.pathParam("title");
		Game game = GameDAO.getGame(title);
		User loggedUser = ctx.sessionAttribute("loggedUser");

		if(loggedUser == null) {
			ctx.status(401);
			return;
		}
		
		if(!loggedUser.getUsername().equals(username) && loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
			ctx.html("Cannot return a game you didn't rent");
			return;
		}
		
		if(game == null) {
			ctx.status(404);
			ctx.html("No game with requested title found");
			return;
		}
		
		if(game.rentedBy == null || !game.rentedBy.equals(username)) {
			ctx.status(404);
			ctx.html(game.title + " isn't currently rented to you");
			return;
		}
		
		User user = UserDAO.getUser(username);
		
		if(user == null) {
			ctx.status(400);
			ctx.html("Invalid User");
			return;
		}
		user.inventory.remove(game);
		
		game.rentedBy = null;
		game.rentDate = null;
		game.returnDate = null;
		game.status = GameStatus.AVAILABLE;
		
		UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
		UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);
		GameDAO.writeToFile(GameDAO.games, GameDAO.games_file);
		
		ctx.html("Returned game: " + game.title + ". We hope you liked it!");
		ctx.status(200);
	}
	
	@Override
	public void buyTitle(Context ctx) {
		// TODO Auto-generated method stub
		String username = ctx.pathParam("username");
		String title = ctx.pathParam("title");
		Game game = GameDAO.getGame(title);
		User loggedUser = ctx.sessionAttribute("loggedUser");
		Long buy_price = 20L;

		if(loggedUser == null) {
			ctx.status(401);
			return;
		}
		
		if(!loggedUser.getUsername().equals(username) && loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
			return;	
		}
		
		User user = UserDAO.getUser(username);
		if(user == null) {
			ctx.status(400);
			ctx.html("Invalid Username");
			return;			
		}
		
		if(game == null) {
			ctx.status(404);
			ctx.html("No game with requested title found");
			return;
		}
		
		if(game.status != GameStatus.AVAILABLE) {			
			if(game.status == GameStatus.RENTED && game.rentedBy.equalsIgnoreCase(user.getUsername())) {	// Apply discount
					buy_price = 10L;
					if(user.getPoints() < buy_price) {
						ctx.status(402);
						ctx.html("Insufficient funds");
						return;
					}
					user.setPoints(user.getPoints() - buy_price);
					user.inventory.add(game);
					game.setStatus(GameStatus.SOLD);
					game.status = GameStatus.SOLD;
					game.ownedBy = user.getUsername();
					game.rentDate = null;
					game.returnDate = null;
					game.rentedBy = null;
					
					ctx.status(200);
					ctx.html("You now own " + game.title + "! Thank you for your purchase");
					
					UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
					UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);
					GameDAO.writeToFile(GameDAO.games, GameDAO.games_file);
					return;	
				
				}
				else
					ctx.html("Title currently unavailable for purchase. It should be available after " +
					game.returnDate);
		}
			
		else {
			ctx.html("Title currently unavailable for purchase. Check back again later");			
			ctx.status(409);
			return;
		}
		
		if(loggedUser.getType() == UserType.ADMIN)
			buy_price = 10L;
		
		
		if(user.getPoints() < buy_price) {
			ctx.status(402);
			ctx.html("Insufficient funds");
			return;
		}
		
		user.setPoints(user.getPoints() - buy_price);
		user.inventory.add(game);
		game.setStatus(GameStatus.SOLD);
		game.status = GameStatus.SOLD;
		game.ownedBy = user.getUsername();
		game.rentDate = null;
		game.returnDate = null;
		game.rentedBy = null;
		
		ctx.status(200);
		ctx.html("You now own " + game.title + "! Thank you for your purchase");
		
		UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
		UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);
		GameDAO.writeToFile(GameDAO.games, GameDAO.games_file);
		return;	
	}

	@Override
	public void viewInventory(Context ctx) {
		// TODO Auto-generated method stub
		User loggedUser = ctx.sessionAttribute("loggedUser");
		String username = ctx.pathParam("username");
		User user = UserDAO.getUser(username);
		if(loggedUser == null) {
			ctx.status(401);
			return;
		}
		
		if(!loggedUser.getUsername().equals(username) && loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
			ctx.html("Cannot rent a game for another user");
			return;	
		}
		
		if(user == null) {
			ctx.status(400);
			ctx.html("Invalid User");
			return;
		}
		
		if(user.getInventory() == null) {
			ctx.status(204);
			ctx.html(user.getUsername() + "'s inventory is Empty!");
			return;
		}
		// send back the requested user's inventory.
		ctx.html("Displaying inventory for user: " + user.getInventory());
		ctx.json(user.getInventory());
		ctx.status(200);
	}

}
