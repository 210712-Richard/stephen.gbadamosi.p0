package com.revature.controllers;

import io.javalin.http.Context;

import java.time.LocalDate;
import java.util.List;

import com.revature.data.GameDAO;
import com.revature.data.UserDAO;
import com.revature.model.Game;
import com.revature.model.GameStatus;
import com.revature.model.User;
import com.revature.model.UserType;
import com.revature.services.AdminService;

public class AdminControllerImpl extends UserControllerImpl implements AdminController {

//	@Override
//	public void login(Context ctx) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void getPoints(Context ctx) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void logout(Context ctx) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void viewRegisteredUsers(Context ctx) {
		// TODO Auto-generated method stub
		User loggedUser = ctx.sessionAttribute("loggedUser");

		// if we aren't logged in or we're not admin deny access
		if(loggedUser == null)  {
			ctx.status(401);
			return;
		}
		
		if (loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
			return;
		}
		
		ctx.status(200);
		ctx.json(UserDAO.getPendingUsers());	
	}
	

	@Override
	public void approveUser(Context ctx) {
		// TODO Auto-generated method stub
		String username = ctx.pathParam("username");
		User loggedUser = (User) ctx.sessionAttribute("loggedUser");
		
		// if we aren't logged in or we're not admin deny access
		if(loggedUser == null)  {
			ctx.status(401);
			return;
		}
		
		if (loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
			return;
		}
		
		for(int i = 0; i < UserDAO.pending_users.size(); i++) {
			if(UserDAO.pending_users.get(i).getUsername().equalsIgnoreCase(username)) {
				User user = new User(UserDAO.pending_users.get(i).getUsername(), UserDAO.pending_users.get(i).getEmail(), 
						UserDAO.pending_users.get(i).getBirthday());
				UserDAO.users.put(user.getId(), user);
				UserDAO.pending_users.remove(i);
				List<User> user_list = UserDAO.getUsers();
				
				UserDAO.writeToFile(user_list, UserDAO.user_file);
				UserDAO.writeToFile(UserDAO.pending_users, UserDAO.pending_ufile);
				
				ctx.status(200);
				ctx.json("New user added!");	
				return;
			}
		}

		ctx.status(404);
		ctx.json("Pending users not found!");	
		return;
	}

	@Override
	public void denyUser(Context ctx) {
		// TODO Auto-generated method stub
		String username = ctx.pathParam("username");
		User loggedUser = (User) ctx.sessionAttribute("loggedUser");
		
		// if we aren't logged in or our username is different than the logged in username and we're not admin

		if(loggedUser == null)  {
			ctx.status(401);
			return;
		}
		
		if (loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
			return;
		}
		
		for(int i = 0; i < UserDAO.pending_users.size(); i++) {
			if(UserDAO.pending_users.get(i).getUsername().equalsIgnoreCase(username)) {
				UserDAO.pending_users.remove(i);				
				UserDAO.writeToFile(UserDAO.pending_users, UserDAO.pending_ufile);
				
				ctx.status(200);
				ctx.json("Pending user removed!");	
				return;
			}
		}
		
		ctx.status(404);
		ctx.json("Pending user not found!");	
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
		
		if(loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
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
	public void getPoints(Context ctx) {
		// TODO Auto-generated method stub
		String username = ctx.pathParam("username");
		Long points = Long.parseLong(ctx.pathParam("points"));
		User loggedUser = ctx.sessionAttribute("loggedUser");
		User user = UserDAO.getUser(username);

		if(loggedUser == null) {
			ctx.status(401);
			return;
		}
		
		if(loggedUser.getType() != UserType.ADMIN ) {
			ctx.status(403);
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
	
//	@Override
//	public void viewRequestedTitles(Context ctx) {
//		// TODO Auto-generated method stub
//		User loggedUser = ctx.sessionAttribute("loggedUser");
//
//
//		if(loggedUser == null)  {
//			ctx.status(401);
//			return;
//		}
//		
//		if (loggedUser.getType() != UserType.ADMIN) {
//			ctx.status(403);
//			return;
//		}
//		
//		ctx.status(200);
//		ctx.html("Returned games pending approval");
//		ctx.json(GameDAO.pending_games);	
//	}
//
//	@Override
//	public void approveTitle(Context ctx) {
//		// TODO Auto-generated method stub
//		String title = ctx.pathParam("title");
//		User loggedUser = (User) ctx.sessionAttribute("loggedUser");
//		
//		// if we aren't logged in or our username is different than the logged in username and we're not admin
//
//		if(loggedUser == null)  {
//			ctx.status(401);
//			return;
//		}
//		
//		if (loggedUser.getType() != UserType.ADMIN) {
//			ctx.status(403);
//			return;
//		}
//		
//		for(int i = 0; i < GameDAO.pending_games.size(); i++) {
//			if(GameDAO.pending_games.get(i).title.equalsIgnoreCase(title)) {
//				Game game = GameDAO.pending_games.remove(i);
//				GameDAO.games.add(game);
//				
//				GameDAO.writeToFile(GameDAO.games, GameDAO.games_file);
//				GameDAO.writeToFile(GameDAO.pending_games, GameDAO.pending_gfile);
//				
//				ctx.status(200);
//				ctx.json("New game added!");	
//				return;
//			}
//		}
//		
//		ctx.status(404);
//		ctx.json("Pending game by given title not found!");	
//		return;
//	}
//
//	@Override
//	public void denyTitle(Context ctx) {
//		// TODO Auto-generated method stub
//		String title = ctx.pathParam("title");
//		User loggedUser = (User) ctx.sessionAttribute("loggedUser");
//		
//		// if we aren't logged in or our username is different than the logged in username and we're not admin
//
//		if(loggedUser == null)  {
//			ctx.status(401);
//			return;
//		}
//		
//		if (loggedUser.getType() != UserType.ADMIN) {
//			ctx.status(403);
//			return;
//		}
//	
//		for(int i = 0; i < GameDAO.pending_games.size(); i++) {
//			if(GameDAO.pending_games.get(i).title.equalsIgnoreCase(title)) {
//				GameDAO.pending_games.remove(i);				
//				GameDAO.writeToFile(GameDAO.pending_games, GameDAO.pending_gfile);
//				
//				ctx.status(200);
//				ctx.json("Pending game denied!");	
//				return;
//			}
//		}
//		
//		ctx.status(404);
//		ctx.json("Pending game by given title not found!");	
//		return;
//	}

	@Override
	public void rentTitle(Context ctx) {
		// TODO Auto-generated method stub
		String username = ctx.pathParam("username");
		String title = ctx.pathParam("title");
		Game game = GameDAO.getGame(title);
		User loggedUser = ctx.sessionAttribute("loggedUser");
		User user = UserDAO.getUser(username);

		if(loggedUser == null)  {
			ctx.status(401);
			return;
		}
		
		if (loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
			return;
		}
		
		if(game == null) {
			ctx.status(404);
			ctx.html("No game with requested title found");
			return;
		}
		
		if(user == null) {
			ctx.status(404);
			ctx.html("Invalid Username");
			return;
		}
		
		if(user.getPoints() < 5L) {
			ctx.status(402);
			ctx.html("Insufficient funds");
			return;
		}
		
		if(game.status != GameStatus.AVAILABLE) {
			ctx.status(409);
			if(game.status == GameStatus.RENTED) {
				if(game.rentedBy.equalsIgnoreCase(username)) 
					ctx.html("Title already rented by user");
				else
					ctx.html("Title currently unavailable for rent. It should be available after " +
			 game.returnDate);
			}
			else
				ctx.html("Title currently unavailable for rent. Check back again later");
			
			return;
		}
		
		if(user.getType() == UserType.CUSTOMER) // Upgrade to gamer account type
			user.setType(UserType.GAMER);
		
		user.setPoints(user.getPoints() - 5L);
		user.inventory.add(game);
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
		
		if(loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
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
		
		game.rentedBy = null;
		game.rentDate = null;
		game.returnDate = null;
		game.status = GameStatus.AVAILABLE;
		user.inventory.remove(game);
		
		UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
		UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);
		GameDAO.writeToFile(GameDAO.games, GameDAO.games_file);
		
		ctx.html("Returned game: " + game.title + ". We hope you liked it!");
		ctx.status(200);
	}

//	@Override
//	public void buyTitle(Context ctx) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void viewUsers(Context ctx) {
		// TODO Auto-generated method stub
		User loggedUser = ctx.sessionAttribute("loggedUser");

		if(loggedUser == null)  {
			ctx.status(401);
			return;
		}
		
		if (loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
			return;
		}
		
		ctx.status(200);
		ctx.html("Returned user list");
		ctx.json(UserDAO.getUsers());
	}

	@Override
	public void viewAdmins(Context ctx) {
		// TODO Auto-generated method stub
		User loggedUser = ctx.sessionAttribute("loggedUser");

		if(loggedUser == null)  {
			ctx.status(401);
			return;
		}
		
		if (loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
			return;
		}
		
		ctx.status(200);
		ctx.html("Returned admin list");
		ctx.json(UserDAO.getAdmins());
	}

	@Override
	public void viewUserInventory(Context ctx) {
		// TODO Auto-generated method stub
		String username = ctx.pathParam("username");
		User loggedUser = ctx.sessionAttribute("loggedUser");
		User user = UserDAO.getUser(username);

		if(loggedUser == null)  {
			ctx.status(401);
			return;
		}
		
		if (loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
			return;
		}
		
		if(user == null) {
			ctx.status(404);
			ctx.html("No user with requested username found");
			return;
		}
		
		ctx.status(200);
		ctx.html("Returned inventory for " + user.getUsername());
		ctx.json(user.inventory);
	}

	@Override
	public void removeUser(Context ctx) {
		// TODO Auto-generated method stub
		String username = ctx.pathParam("username");
		User loggedUser = ctx.sessionAttribute("loggedUser");
		User user = UserDAO.getUser(username);

		if(loggedUser == null)  {
			ctx.status(401);
			return;
		}
		
		if (loggedUser.getType() != UserType.ADMIN) {
			ctx.status(403);
			return;
		}
		
		if(user == null) {	// user does not exist in database
			ctx.html(username + " not found in database");
			ctx.status(404);
			return;			
		}
		
		if(!UserDAO.users.remove(user.getId(), user)) {
			UserDAO.admins.remove(user.getId(), user);
		}
		
		ctx.html("Removed user: " + username + " from database");
		UserDAO.writeToFile(UserDAO.getUsers(), UserDAO.user_file);
		UserDAO.writeToFile(UserDAO.getAdmins(), UserDAO.admin_file);
		ctx.status(204);
		return;			
	}

}
