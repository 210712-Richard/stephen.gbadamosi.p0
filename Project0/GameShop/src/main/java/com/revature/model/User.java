package com.revature.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.revature.data.UserDAO;


public class User implements Serializable {
	private static final long serialVersionUID = 7426075925303078797L;
	
	private String name;
	private String last_name;
	private Integer id;
	private String username;
	private String email;
	private LocalDate birthday;
	private UserType type;
	private Long points;
	private LocalDate lastCheckIn;
	public List<Game> inventory;
	private Integer rent_count;				// MAX 2 for users | 4 for admins
	private String message;
	
//	private UserDAO udao = new UserDAO();
//	private UserService us = new UserService();

	// Defaults to admin constructor
	public User() {
		int id = UserDAO.first_uid;
		id = UserDAO.checkID(id, UserType.CUSTOMER);
		if(id <= UserDAO.first_uid) {
			System.out.println("Failed to create new admin in constructor");
			return;
		}
		
		this.setId(id);
		this.setUsername("user" + this.id);
		this.setType(UserType.CUSTOMER);
		this.setPoints(100L);		
		this.setRentCount(0);
		this.setMessage("New customer account created!");
		this.inventory = new ArrayList<Game>();
//		udao.addUser(this);
	}
	
	public User(String first_name, String last_name, String uname, String email, LocalDate bday, UserType type) {
		int id = type == UserType.ADMIN ? 0 : 100;
		id = UserDAO.checkID(id, type);
		
		if(id == UserDAO.first_aid || id == UserDAO.first_uid) {
			System.out.println("Failed to reserve new ID");
			return;
		}
		
		this.id = id;	// ID check done so by-passing additional check in setter
		this.setName(first_name);
		this.setLastName(last_name);
		this.setUsername(uname);
		this.setEmail(email);
		this.setBirthday(bday);
		this.setType(type);
		this.setRentCount(0);
		this.setMessage("New " + type + " account created!");
		this.inventory = new ArrayList<Game>();

	}
	
	public User(Integer id, String username, String email, LocalDate birthday, UserType type) {
		boolean flag = true;
		long points = 0;
		int i = UserDAO.checkID(id, type);
		System.out.println("ID returned from check: " + i);
		
		if(i <= 0) {
			System.out.println("Failed to create ID in constructor");
			flag = false;
		}
		
		if(!UserDAO.checkBirthday(birthday, type)) {
			System.out.println("Failed to set birthday in constructor");
			flag = false;;
		}
		
		if(!UserDAO.checkUsername(username)) {
			System.out.println("Failed to set username in constructor");
			flag = false;
		}
		if(!UserDAO.checkEmail(email)) {
			System.out.println("Failed to set email in constructor");
			flag = false;
		}
		
		if(flag) {					
			this.id = (i);
			System.out.println("Creating " + type + " account with ID: " + this.getId());
			this.username = username;
			this.email = email;
			this.birthday = birthday;
			this.type = type;
			points = type == UserType.ADMIN ? 100L : 10L;		
			this.points = points;
			this.setRentCount(0);
			this.setMessage("New " + type + " account created!");
			this.inventory = new ArrayList<Game>();
		}
		else {
			System.out.println("Unable to create new account");
			return;
		}
	}
	
	// Defaults to user constructor (Register)
	public User(String username, String email, LocalDate birthday) {
		boolean flag = true;
		int id = 0;
		id = UserDAO.checkID(id, UserType.CUSTOMER);
		
		if(id <= 0); {
			System.out.println("Failed to create ID in constructor");
			flag = false;
		}
	
		if(!UserDAO.checkUsername(username)) {
			System.out.println("Failed to set username in constructor");
			flag = false;
		}

		if(!UserDAO.checkEmail(email)) {
			System.out.println("Failed to set email in constructor");
			flag = false;
		}
		
		if(!UserDAO.checkBirthday(birthday, UserType.CUSTOMER)) {
			System.out.println("Failed to set birthday in constructor");
			flag = false;;
		}
		
		if(flag) {					
			this.id = id;
			System.out.println("Creating User with ID: " + this.getId());
			this.username = username;
			this.email = email;
			this.birthday = birthday;
			this.type = UserType.CUSTOMER;
			this.points = 10L;
			this.setRentCount(0);
			this.setMessage("New account pending confirmation");
			this.inventory = new ArrayList<Game>();
			
			System.out.println("Thanks for registering! An admin will review and confirm your account within 48 hrs\n"
					+ "Feel free to reach out to admin@gameshop.com with any questions");
		}
		
		System.out.println("Unable to create new user account in constructor");
		return;		
	}
	
	public User(String username, String email, LocalDate birthday, UserType type) {
		int id = 0, uid;
		uid = UserDAO.checkID(id, type);
		long points = 0;
		
		if(uid <= 0) {
			System.out.println("Failed to create ID in constructor");
			return;
		}
	
		if(!UserDAO.checkUsername(username)) {
			System.out.println("Failed to set username in constructor");
			return;
		}

		if(!UserDAO.checkEmail(email)) {
			System.out.println("Failed to set email in constructor");
			return;
		}
		
		if(!UserDAO.checkBirthday(birthday, type)) {
			System.out.println("Failed to set birthday in constructor");
			return;
		}
		
		this.id = uid;
		System.out.println("Creating " + type + " with ID: " + this.getId());
		this.username = username;
		this.email = email;
		this.birthday = birthday;
		this.type = type;
		points = type == UserType.ADMIN ? 100L : 10L;
		this.points = points;
		this.setRentCount(0);
		this.setMessage("New " + type + " account created!");
		this.inventory = new ArrayList<Game>();
	
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return last_name;
	}

	public void setLastName(String last_name) {
		this.last_name = last_name;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		if (id <= 0 || id >= 1000) {
			System.out.println("ID must be a number between 101 and 1000");
			return;
		}
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		if(UserDAO.checkUsername(username))
			this.username = username;
		else {
			System.out.println("Didn't update username");
			return;
		}			
	}	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		if(UserDAO.checkEmail(email))
			this.email = email;
		else {
			System.out.println("Didn't update email address");
			return;
		}
	}
	public LocalDate getBirthday() {
		return birthday;
	}
	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}
	public UserType getType() {
		return type;
	}
	public void setType(UserType type) {
		this.type = type;
	}
	public Long getPoints() {
		return this.points;
	}
	public void setPoints(Long points) {
		if (points < 0L) {
			System.out.println("Points cannot be less than 0, resetting to 0");
			this.points = 0L;
		}
		else 
			this.points = points;
	}
	public LocalDate getLastCheckIn() {
		return lastCheckIn;
	}
	public void setLastCheckIn(LocalDate lastCheckIn) {
		this.lastCheckIn = lastCheckIn;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<Game> getInventory() {
		return inventory;
	}
	public void setInventory(List<Game> inventory) {
		this.inventory = inventory;
	}
	public Integer getRentCount() {
		return rent_count;
	}

	public void setRentCount(Integer rent_count) {
		this.rent_count = rent_count;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((birthday == null) ? 0 : birthday.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((inventory == null) ? 0 : inventory.hashCode());
		result = prime * result + ((lastCheckIn == null) ? 0 : lastCheckIn.hashCode());
		result = prime * result + ((last_name == null) ? 0 : last_name.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		result = prime * result + ((rent_count == null) ? 0 : rent_count.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (birthday == null) {
			if (other.birthday != null)
				return false;
		} else if (!birthday.equals(other.birthday))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (inventory == null) {
			if (other.inventory != null)
				return false;
		} else if (!inventory.equals(other.inventory))
			return false;
		if (lastCheckIn == null) {
			if (other.lastCheckIn != null)
				return false;
		} else if (!lastCheckIn.equals(other.lastCheckIn))
			return false;
		if (last_name == null) {
			if (other.last_name != null)
				return false;
		} else if (!last_name.equals(other.last_name))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (points == null) {
			if (other.points != null)
				return false;
		} else if (!points.equals(other.points))
			return false;
		if (rent_count == null) {
			if (other.rent_count != null)
				return false;
		} else if (!rent_count.equals(other.rent_count))
			return false;
		if (type != other.type)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		if(name == null || name.equals(""))
			return "User [username=" + username + "\nid=" + id + ", email=" + email + ", birthday=" + birthday + ", type="	+ type + ", points=" + points + ", last check-in=" + lastCheckIn + 
					", rented titles= " + rent_count + "\nInventory: " + (inventory == null ? "Empty" : inventory.toString() ) + "]";
		else
			return "User [Name: " + name + " " + last_name + "\nid=" + id + ", username=" + username + ", email=" + email + ", birthday=" + birthday + ", type="
			+ type + ", points=" + points + ", last check-in=" + lastCheckIn + ", rented titles= " + rent_count + "\nInventory: " + (inventory == null ? "Empty" : inventory.toString() ) + "]";	
	}
}
