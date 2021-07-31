package com.revature.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Period;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.revature.model.*;
import com.revature.data.*;

public class AdminServiceTest {
	private static AdminService service;
	private static User test_user;
	private static User default_admin;
	
	@BeforeAll // Specifies that this static method will be run before any tests
	public static void setUpClass() {
		default_admin = service.udao.getUserbyID(1);
		test_user = service.udao.addUser("testuser", "testuser@test.com", LocalDate.of(2007, 10, 10), UserType.CUSTOMER);
	}
	
	@BeforeEach // Specifies a method that is to be run before each test
	public void setUpTests() {
		service = new AdminService(); // create a new userService for every test to maximize isolation
		service.gs = Mockito.mock(GameService.class);
		service.udao = Mockito.mock(UserDAO.class);
	}

	@Test
	public void testaddUser() {
		User test = null;
		String username = "test"; 
		String email = "test@test.com"; 
		LocalDate birthday = LocalDate.of(2004, 10, 10);
		UserType type = UserType.CUSTOMER;
		// Conditions for add:
		
		// Username does not exist in database
		// remove user if already in database
		if(!service.udao.checkUsername(username))
			service.udao.deleteUser(default_admin, username);

//		assertTrue(service.udao.checkUsername(username));;
		
		// Birthday is valid for account type (>18yrs for admins and >10yrs for others)
		assertTrue(service.udao.checkBirthday(birthday, type));
		
		test_user = Mockito.verify(service.udao).addUser(username, email, birthday, type);
		
		// User exists in DB now so username conflict should cause check to return false
		assertFalse(service.udao.checkUsername(username)); 
		
	}
	
	@Test
	public void testDeleteUser() {
		// Conditions for delete:
		
		// target username exists in database (conflict)
		assertFalse(service.udao.checkUsername(test_user.getUsername()));
		
		// admin initiates and completes delete operation
		service.udao.deleteUser(default_admin, test_user.getUsername());
		assertTrue(default_admin.getType() == UserType.ADMIN);
		
		// User removed from DB so username check should return true (no conflicts)
		assertTrue(service.udao.checkUsername(test_user.getUsername()));

		return;

	}	
}
