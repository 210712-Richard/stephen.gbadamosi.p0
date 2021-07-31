package com.revature.controllers;

import io.javalin.http.Context;

public interface GameController {
	
	void viewTitle(Context ctx);
	
	void viewInventory(Context ctx);
	

}
