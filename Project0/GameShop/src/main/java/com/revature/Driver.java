package com.revature;

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
//		app.get("/", (ctx)->ctx.html("Welcome to GameShop Online!\nRegister or login to begin"));
//				
//		// As a user, I can register for a customer account
//		app.put("/users/:username", uc::register);		
//		
//		// As a user, I can log in.
//		app.post("/users", uc::login);
//		
//		// As a user, I can log out.
//		app.delete("/users", uc::logout);
//		
//		// As a user, I can get points.
//		app.put("/users/:username/points/:count", uc::getPoints);
//		
//		// As a user, I can view my points.
//		app.get("/users/:username/points", uc::viewPoints);		
//
//		// As a customer, I can view game inventory
//		app.get("/games/inventory/", gc::viewInventory);		
//	
//		// As a customer, I can search a specific title from inventory
//		app.get("/games/inventory/:title", gc::viewTitle);		
//		
//		// As a customer, I can buy a video game (if available) with sufficient points
//		app.post("/users/:username/inventory/sales/:title", uc::buyTitle);
//		
//		// As a gamer, I can rent a video game (if available) with sufficient points
//		app.post("/users/:username/inventory/rentals/:title", uc::rentTitle);		
//		
//		// As a user, I can view my rented/bought titles
//		app.get("/users/:username/inventory/", uc::viewInventory);
		
		Menu m = new Menu();
		m.start();
	}
}
