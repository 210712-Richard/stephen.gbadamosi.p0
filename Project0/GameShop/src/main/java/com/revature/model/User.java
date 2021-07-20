package com.revature.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.revature.data.*;


public class User {
	
	private Integer id;
	private String username;
	private String email;
	private LocalDate birthday;
	private UserType type;
	private Long points;
	private LocalDate lastCheckIn;
	private List<Game> inventory;
	private String message;
	
	public UserDAO udao = new UserDAO();

	// Defaults to admin constructor
	public User() {
		super();
		int id = UserDAO.first_aid+1;
		
		while (udao.getUser(id) != null && id < 100) { // ID already in use
			if (++id == 100) { // All admin spots filled, block account creation
				System.out.println("Unable to create new admin account, try again later");
				return;
			}
		}
		
		this.setId(id);
		this.setUsername("admin" + this.id);
		this.setType(UserType.ADMIN);
		this.setPoints(100L);		
		this.setMessage("New admin account created!");
//		udao.addUser(this);
	}
	
	public User(Integer id, String username, String email, LocalDate birthday, UserType type) {
		if (type == UserType.ADMIN) {
			if (birthday.isAfter((LocalDate.now().minusYears(18)))) { // Admin is less than 18 yrs old, deny request
				System.out.println("Minimum age for admin accounts is 18 years and older\n"
						+ "Account creation failed");
				return;
			}
			if (id < UserDAO.first_aid || id > UserDAO.last_aid) {
				System.out.println("Invalid Admin ID, try again\n"
						+ "Account creation failed");
				return;
			}
			
			int i = id;
			while (udao.getUser(i) != null && i < UserDAO.last_aid) {
				if (++i == UserDAO.last_aid) {
					System.out.println("Could not create admin with ID: " + id);
					return;
				}
			}
			
			this.setId(id == i ? id : i);
			if(id != i)
				System.out.println("ID " + id + "already in use");
			
			System.out.println("Creating Admin with ID: " + this.getId());
			this.setUsername(username);
			this.setEmail(email);
			this.setBirthday(birthday);
			this.setType(UserType.ADMIN);
			this.setPoints(100L);
			this.setMessage("New admin account created!");
//			udao.addUser(this);			
		}
		
		else {	// User account requested
			if (birthday.isAfter((LocalDate.now().minusYears(10)))) { // User is less than 10 yrs old, deny request
				System.out.println("Minimum age for user accounts is 10 years and older\n"
						+ "Account creation failed");
				return;
			} 
			if (id < UserDAO.first_uid || id > UserDAO.last_uid || UserDAO.users.get(id) != null) {
				System.out.println("Invalid User ID, using next available ID to create user account");
				id = UserDAO.first_uid;
				while(udao.getUser(id) != null && id < UserDAO.last_uid) {
					if(++id == UserDAO.last_uid) {
						System.out.println();
					}
				}
			}
			this.setId(id);
			System.out.println("Creating User with ID: " + id);

			this.setUsername(username);
			this.setEmail(email);
			this.setBirthday(birthday);
			this.setType(UserType.CUSTOMER);
			this.setPoints(10L);
			this.setMessage("New account created!");

//			udao.addUser(this);
		}
	}
	
	public User(String username, String email, LocalDate birthday) {
		if (birthday.isAfter((LocalDate.now().minusYears(10)))) { // User is less than 10 yrs old, deny request
			System.out.println("Minimum age for Users: 10 years and older\n"
					+ "Account creation failed");
			return;
		}
	
		int id = UserDAO.first_uid + 1;
		// Verify ID isn't currently in use
		while(udao.getUser(id) != null && id < UserDAO.last_uid) {
			if (++id == UserDAO.last_uid) {
				System.out.println("Max user ID exceeded\nAccount creation failed");
				return;
			}
		}
		
		this.setId(id);
		this.setUsername(username);
		this.setEmail(email);
		this.setBirthday(birthday);
		this.setType(UserType.PENDING);
		this.setPoints(10L);
		this.setMessage("New account pending confirmation");
		
		System.out.println("Thanks for registering! An admin will review and confirm your account within 48 hrs\n"
				+ "Feel free to reach out to admin@gameshop.com with any questions");
//		udao.addPendingUser(this);
	}
	
	public User(String username, String email, LocalDate birthday, UserType type) {
		if(type == UserType.ADMIN) {
			if (birthday.isAfter((LocalDate.now().minusYears(18)))) { // Admin is less than 18 yrs old, deny request
				System.out.println("Minimum age for admin accounts is 18 years and older\n"
						+ "Account creation failed");
				return;
			}
			
			int id = UserDAO.first_aid;
			while (udao.getUser(id) != null && id < UserDAO.last_aid) {
				if (++id == UserDAO.last_aid) {
					System.out.println("Could not create admin with ID: " + id);
					return;
				}
			}
			this.setId(id);
			
			System.out.println("Creating Admin with ID: " + id);
			this.setUsername(username);
			this.setEmail(email);
			this.setBirthday(birthday);
			this.setType(UserType.ADMIN);
			this.setPoints(100L);
			this.setMessage("New admin account created!");
//			udao.addUser(this);
		}
		
		else {	// User account requested
			if (birthday.isAfter((LocalDate.now().minusYears(10)))) { // User is less than 10 yrs old, deny request
				System.out.println("Minimum age for Users: 10 years and older\n"
						+ "Account creation failed");
				return;
			}
		
			int id = UserDAO.first_uid + 1;
			// Verify ID isn't currently in use
			while(udao.getUser(id) != null && id < UserDAO.last_uid) {
				if (++id == UserDAO.last_uid) {
					System.out.println("Max user ID exceeded\nAccount creation failed");
					return;
				}
			}
			this.setId(id);
			System.out.println("Creating User with ID: " + id);

			this.setUsername(username);
			this.setEmail(email);
			this.setBirthday(birthday);
			this.setType(UserType.CUSTOMER);
			this.setPoints(10L);
			this.setMessage("New account created!");

//			udao.addUser(this);						
		}
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		if (id <= 0 || id >= 1000) {
			System.out.println("ID must be a positve number below 1000");
			return;
		}
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		if(username == null || username.length() < 3) {
			System.out.println("Invalid username. Username must be at least 3 characters long");
			return;
		}
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		if(email.indexOf("@") < 0 || email.indexOf(".") < 0) {
			System.out.println("Invalid email address. Try again");
			return;
		}
		this.email = email;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((birthday == null) ? 0 : birthday.hashCode());
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((inventory == null) ? 0 : inventory.hashCode());
		result = prime * result + ((lastCheckIn == null) ? 0 : lastCheckIn.hashCode());
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
		if (points == null) {
			if (other.points != null)
				return false;
		} else if (!points.equals(other.points))
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
		String inv = inventory == null ? null : inventory.toString();
		return "User [id=" + id + ", username=" + username + ", email=" + email + ", birthday=" + birthday + ", type="
				+ type + ", points=" + points + ", lastCheckIn=" + lastCheckIn + "\nInventory: " + inv + "]";
	}
	
	
}
