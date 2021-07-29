package com.revature.controllers;

import io.javalin.http.Context;

public interface GameController {
	void requestTitle(Context ctx);

	void addTitle(Context ctx);
	
	void viewTitle(Context ctx);
	
	void viewInventory(Context ctx);
	

}
