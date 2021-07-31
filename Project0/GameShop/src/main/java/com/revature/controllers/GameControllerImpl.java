package com.revature.controllers;

import io.javalin.http.Context;

import com.revature.data.GameDAO;
import com.revature.model.Game;
import com.revature.model.User;
import com.revature.services.GameService;

public class GameControllerImpl implements GameController {

	@Override
	public void viewTitle(Context ctx) {
		// TODO Auto-generated method stub
		String title = ctx.pathParam("title");
		Game game = GameDAO.getGame(title);
		User loggedUser = ctx.sessionAttribute("loggedUser");

		if(loggedUser == null) {
			ctx.status(403);
			return;
		}
		
		if(game == null) {
			ctx.status(404);
			ctx.html("No game with requested title found");
			return;
		}		
		
		ctx.json(game);
		ctx.status(200);
	}

	@Override
	public void viewInventory(Context ctx) {
		// TODO Auto-generated method stub
		User loggedUser = ctx.sessionAttribute("loggedUser");

		if(loggedUser == null) {
			ctx.status(403);
			return;
		}
		
		if(GameDAO.games.size() <= 0) {
			ctx.status(404);
			ctx.html("No games added to inventory");
			return;
		}
		
		ctx.json(GameDAO.games);

	}

}
