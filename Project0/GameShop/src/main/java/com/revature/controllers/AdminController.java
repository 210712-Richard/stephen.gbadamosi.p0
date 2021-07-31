package com.revature.controllers;

import io.javalin.http.Context;

public interface AdminController {
//	void login(Context ctx);

//	void logout(Context ctx);

	void getPoints(Context ctx);
	
	void viewRegisteredUsers(Context ctx);

	void approveUser(Context ctx);

	void denyUser(Context ctx);

	void viewPoints(Context ctx);

//	void viewRequestedTitles(Context ctx);

//	void approveTitle(Context ctx);

//	void denyTitle(Context ctx);
		
	void rentTitle(Context ctx);

//	void buyTitle(Context ctx);

//	void viewTitles(Context ctx);

	void viewUserInventory(Context ctx);
	
	void viewUsers(Context ctx);
	
	void viewAdmins(Context ctx);

//	void updateUser(Context ctx);

	void removeUser(Context ctx);

}
