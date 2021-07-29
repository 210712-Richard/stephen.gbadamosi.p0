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

		User newUser = us.register(u.getUsername(), u.getEmail(), u.getBirthday());
		if(newUser != null) {
			ctx.status(201);
			ctx.json(newUser);
		} else {
			ctx.status(409);
			ctx.html("Username already taken.");
		}
	}

	@Override
	public void login(Context ctx) {
		// TODO Auto-generated method stub
		log.trace("Login method called");
		
		String input[] = ctx.body().split(":");
		log.debug(input);
		String u = input[1].trim();
		log.debug(u);
		
		// Use the request data to obtain the data requested
		User user = us.login(u);
		log.debug(u);
		
		// Create a session if the login was successful
		if(u != null) {
			// Save the user object as loggedUser in the session
			ctx.sessionAttribute("loggedUser", user);
			
			// Try to use the JSON Marshaller to send a JSON string of this object back to the client
			ctx.json(user);
			return;
		}
		
		// Send a 401 is the login was not successful
		ctx.status(401);
	}

	@Override
	public void getPoints(Context ctx) {
		// TODO Auto-generated method stub
		String user = ctx.pathParam("username");
		Long points = Long.parseLong(ctx.pathParam("count"));
		User loggedUser = ctx.sessionAttribute("loggedUser");

		if(loggedUser == null || !loggedUser.getUsername().equals(user)) {
			ctx.status(403);
			return;
		}
		
		if(loggedUser.getType() != UserType.ADMIN) {
			if(points > 20L) {
				ctx.status(403);
				ctx.html("Only admins can add more than 20 points at a time");
				return;
			}
		}
		
		loggedUser.setPoints(loggedUser.getPoints() + points);
		ctx.html("Total points for " + loggedUser.getUsername() + " is: " + loggedUser.getPoints());
		ctx.status(200);
		
		UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
		UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);

		return;
	}

	@Override
	public void logout(Context ctx) {
		// TODO Auto-generated method stub
		ctx.req.getSession().invalidate();
		ctx.status(204);
	}

	@Override
	public void rentTitle(Context ctx) {
		// TODO Auto-generated method stub
		String user = ctx.pathParam("username");
		String title = ctx.pathParam("title");
		Game game = GameDAO.getGame(title);
		User loggedUser = ctx.sessionAttribute("loggedUser");

		if(loggedUser == null || !loggedUser.getUsername().equals(user)) {
			ctx.status(403);
			return;
		}
		
		if(loggedUser.getPoints() < 5L) {
			ctx.status(402);
			ctx.html("Insufficient funds");
			return;
		}
		
		if(game == null) {
			ctx.status(404);
			ctx.html("No game with requested title found");
			return;
		}
		
		if(game.status != GameStatus.AVAILABLE) {
			ctx.status(409);
			ctx.html("Title currently unavailable");
			return;
		}
		
		if(loggedUser.getType() == UserType.CUSTOMER) // Upgrade to gamer
			loggedUser.setType(UserType.GAMER);
		
		loggedUser.setPoints(loggedUser.getPoints() - 5L);
		loggedUser.inventory.add(game);
		game.setStatus(GameStatus.RENTED);
		game.status = GameStatus.RENTED;
		game.rentDate = LocalDate.now();
		game.returnDate = LocalDate.now().plusWeeks(1);
		game.rentedBy = loggedUser.getUsername();
		
		ctx.status(200);
		ctx.html("Game rented for a week. Due Date: " + game.returnDate.toString());
		
		UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
		UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);
		GameDAO.writeToFile(GameDAO.games, GameDAO.games_file);
		return;		
	}

	@Override
	public void buyTitle(Context ctx) {
		// TODO Auto-generated method stub
		String user = ctx.pathParam("username");
		String title = ctx.pathParam("title");
		Game game = GameDAO.getGame(title);
		User loggedUser = ctx.sessionAttribute("loggedUser");

		if(loggedUser == null || !loggedUser.getUsername().equals(user)) {
			ctx.status(403);
			return;
		}
		
		if(loggedUser.getPoints() < 20L) {
			ctx.status(402);
			ctx.html("Insufficient funds");
			return;
		}
		
		if(game == null) {
			ctx.status(404);
			ctx.html("No game with requested title found");
			return;
		}
		
		if(game.status != GameStatus.AVAILABLE) {
			ctx.status(409);
			ctx.html("Title currently unavailable");
			return;
		}
		Long buy_price = 20L;
		if((game.rentedBy != null && game.rentedBy.equals(loggedUser.getUsername())) || loggedUser.getType() == UserType.ADMIN)
			buy_price = 10L;
		loggedUser.setPoints(loggedUser.getPoints() - buy_price);
		loggedUser.inventory.add(game);
		game.setStatus(GameStatus.SOLD);
		game.status = GameStatus.SOLD;
		game.ownedBy = loggedUser.getUsername();
		game.rentDate = null;
		game.returnDate = null;
		game.rentedBy = null;
		
		ctx.status(200);
		ctx.html("Game rented for a week. Due Date: " + game.returnDate.toString());
		
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
		if(loggedUser == null || !loggedUser.getUsername().equals(username)) {
			ctx.status(403);
			return;
		}
		
		// send back the loggedin User's inventory.
		ctx.json(loggedUser.getInventory());
	}

	@Override
	public void viewPoints(Context ctx) {
		String username = ctx.pathParam("username");
		User loggedUser = (User) ctx.sessionAttribute("loggedUser");
		
		// if we aren't logged in or our username is different than the logged in username and we're not admin
		if((loggedUser == null || !loggedUser.getUsername().equals(username)) && loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
			return;
		}
		// otherwise we're golden
		ctx.json(loggedUser.getPoints());		
	}

}
