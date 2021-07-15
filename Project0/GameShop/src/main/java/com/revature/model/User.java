package com.revature.model;

import java.time.LocalDate;
import java.util.List;


public class User {
	private static final long serialVersionUID = -6426075925303078798L;
	
	private Integer id;
	private String username;
	private String email;
	private LocalDate birthday;
	private UserType type;
	private Long currency;
	private LocalDate lastCheckIn;
	private List<Game> inventory;
	
	
}
