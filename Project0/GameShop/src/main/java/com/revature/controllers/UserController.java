package com.revature.controllers;

import io.javalin.http.Context;

public interface UserController {
	void register(Context ctx);

	void login(Context ctx);

	void getPoints(Context ctx);

	void viewPoints(Context ctx);

	void logout(Context ctx);
		
	void rentTitle(Context ctx);

	void buyTitle(Context ctx);

	void viewInventory(Context ctx);
	
}
