package com.revature.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.revature.data.UserDAO;


public class User extends UserDAO {
	
	private Integer id;
	private String username;
	private String email;
	private LocalDate birthday;
	private UserType type;
	private Long points;
	private LocalDate lastCheckIn;
	private List<Game> inventory;
	private String message;

	public User() {
		super();
		while (UserDAO.admins.get(UserDAO.curr_aid) != null) { // ID already in use
			UserDAO.curr_aid++;
		}
		this.id = UserDAO.curr_aid++;
		this.username = "admin" + this.id;
		this.type = UserType.ADMIN;
		this.points = 100L;		
		this.message = "New account created!";
		UserDAO.admins.put(this.id, this);
	}
	
	public User(Integer id, String username, String email, LocalDate birthday, UserType type) {
		if (type == UserType.ADMIN) {
			if (birthday.isAfter((LocalDate.now().minusYears(18)))) { // Admin is less than 18 yrs old, deny request
				System.out.println("Minimum age for admin accounts is 18 years and older");
				return;
			}
			if (id < UserDAO.first_aid || id > UserDAO.last_aid) {
				System.out.println("Invalid Admin ID, try again");
				return;
			}
			
			int i = id;
			while (UserDAO.admins.get(i) != null && i < UserDAO.last_aid) {
				if (++i >= User.last_aid) {
					System.out.println("Could not create user with ID: " + id);
					return;
				}
			}
			
			this.id = id == i ? id : i;
			System.out.println("Creating User with ID: " + this.id);
			this.username = username;
			this.email = email;
			this.birthday = birthday;
			this.type = UserType.ADMIN;
			this.points = 100L;
			this.message = "New account created!";
			
			UserDAO.admins.put(id, this);
		}
		
		else {	// User account requested
			if (birthday.isAfter((LocalDate.now().minusYears(10)))) { // User is less than 18 yrs old, deny request
				System.out.println("Minimum age for user accounts is 10 years and older");
				return;
			} 
			if (id < UserDAO.first_aid || id > UserDAO.last_aid || UserDAO.admins.get(id) != null) {
				System.out.println("Invalid Admin ID, try again");
				return;
			}
			
			this.id = id;
			this.username = username;
			this.email = email;
			this.birthday = birthday;
			this.type = UserType.CUSTOMER;
			this.points = 10L;
			this.message = "New account created!";

			UserDAO.users.put(id, this);
		}
	}
	
	public User(Integer id, String username, String email, LocalDate birthday) {
		if (birthday.isAfter((LocalDate.now().minusYears(18)))) { // Admin is less than 18 yrs old, deny request
			System.out.println("Minimum age for Admins: 18 years and older");
			return;
		} 
		if (id < UserDAO.first_aid || id > UserDAO.last_aid || UserDAO.admins.get(id) != null) {
			System.out.println("Invalid Admin ID, try again");
			return;
		}
		
		this.id = id;
		this.username = username;
		this.email = email;
		this.birthday = birthday;
		this.type = UserType.PENDING;
		this.points = 10L;
		this.message = "New account created!";

		UserDAO.pending_users.add(this);
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
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
		return "User [id=" + id + ", username=" + username + ", email=" + email + ", birthday=" + birthday + ", type="
				+ type + ", points=" + points + ", lastCheckIn=" + lastCheckIn + "\nInventory =" + inventory.toString() + "]";
	}
	
	
}
