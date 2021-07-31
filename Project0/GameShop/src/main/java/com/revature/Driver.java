package com.revature;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.revature.controllers.*;
import com.revature.menu.Menu;
import com.revature.services.*;

import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;

public class Driver {
	public static void main(String[] args) {
//		
//		// Serialize LocalDate from JSON string
//		ObjectMapper jackson = new ObjectMapper();
//		jackson.registerModule(new JavaTimeModule());
//		jackson.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//		JavalinJackson.configure(jackson);
//		
//		Javalin app = Javalin.create().start(8080);
//		
//		UserControllerImpl uc = new UserControllerImpl();
//		AdminControllerImpl ac = new AdminControllerImpl();
//		GameControllerImpl gc = new GameControllerImpl();
//		
//		// app.METHOD("URN", CALLBACK_FUNCTION);
//		// The Javalin CALLBACK_FUNCTION takes an argument ctx which 
//		// represents the request and the response to the request.
//		// ctx.body() - The body of the request
//		// ctx.html() - Sends html as the response
//		// ctx.status() - changes the status of the response
//		
//		app.get("/", (ctx)->ctx.html("Welcome to GameShop Online!\nRegister or login to begin"));
//				
//		// As a [potential] customer, I can register for a customer account
//		app.post("/guests", uc::register);		
//		
//		// As a user, I can log in.
//		app.post("/users", uc::login);
//		
//		// As a user, I can log out.
//		app.delete("/users", uc::logout);
//		
//		// As a customer, I can get points.
//		app.put("/users/:username/points/:count", uc::getPoints);
//		
//		// As a customer, I can view my points.
//		app.get("/users/:username/points", uc::viewPoints);		
//
//		// As a customer, I can view game inventory
//		app.get("/games/inventory/", gc::viewInventory);		
//	
//		// As a customer, I can search a specific title from inventory
//		app.get("/games/inventory/:title", gc::viewTitle);		
//		
//		// As a gamer, I can rent a video game (if available) with sufficient points
//		app.post("/users/:username/inventory/rentals/:title", uc::rentTitle);		
//
//		// As a gamer, I can return a video game (if rented)
//		app.delete("/users/:username/inventory/rentals/:title", uc::returnTitle);		
//	
//		// As a customer, I can buy a video game (if available) with sufficient points
//		app.post("/users/:username/inventory/sales/:title", uc::buyTitle);
//
//		// As a user, I can view my rented/bought titles
//		app.get("/users/:username/inventory/", uc::viewInventory);
//		
//		// As an admin, I can perform all user functions +
//		
//		// I can view registered users
//		app.get("/admins/guests", ac::viewRegisteredUsers);
//
//		// I can approve (or reject) registered user
//		app.post("/admins/guests/:username", ac::approveUser);
//		app.delete("/admins/guests/:username", ac::denyUser);
//
//		// As an admin, I can view/add points for any account***
//		app.get("/admins/:username/points", ac::viewPoints);
//		app.put("/admins/:username/:points", ac::getPoints);
//		
////		// As an admin, I can view requested games
////		app.get("/admins/temp/games/", ac::viewRequestedTitles);
////		
////		// As an admin, I can approve (or reject) requested games
////		app.post("/admins/temp/games/:title", ac::approveTitle);
////		app.delete("/admins/temp/games/:title", ac::denyTitle);		
//		
//		// As an admin, I can return/assign rentals on/to any account
//		app.post("/admins/:username/inventory/rentals/:title", ac::rentTitle);		
//		app.delete("/admins/:username/inventory/rentals/:title", ac::returnTitle);		
//
//		// As an admin, I can buy titles
//		app.post("/admins/:username/inventory/sales/:title", ac::buyTitle);		
//
//		// As an admin, I can view inventory for any account
//		app.get("/admins/:username/inventory", ac::viewUserInventory);
//
//		// As an admin, I can view all users
//		app.get("/admins/users", ac::viewUsers);
//		app.get("/admins/admin/users", ac::viewAdmins);
//		
//		// As an admin, I can delete users
//		app.delete("/admins/users/:username", ac::removeUser);
		
		
		
		
		Menu m = new Menu();
		m.start();
	}
}
