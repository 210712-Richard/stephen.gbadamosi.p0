package com.revature.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.revature.data.*;
import com.revature.model.*;

public class UserServiceTest {
	private static UserService service;
	private static User u;
	
	@BeforeAll // Specifies that this static method will be run before any tests
	public static void setUpClass() {
//		u = new User("test", LocalDate.of(2000, 10, 10), UserType.CUSTOMER); // New users start with a date of 1/1/21
	}
	
	@BeforeEach // Specifies a method that is to be run before each test
	public void setUpTests() {
		service = new UserService(); // create a new userService for every test to maximize isolation
		u.setLastCheckIn(LocalDate.of(2021, 1,1));
		u.setPoints(50l);
//		service.udao = Mockito.mock(UserDAO.class);
	}
	
	@Test
	public void testRegister() {
		String username = "test";
		String email = "test@test.test";
		LocalDate date = LocalDate.of(2020, 2, 2);
//		service.register(username, email, date);
		
		// An object that allows us to receive parameters from methods called on a Mockito mock object
		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

		// ud.addUser() was called with our User as an argument.
//		Mockito.verify(service.udao).addUser(captor.capture());

		// ud.writeToFile() was called.
		//Mockito.verify(service.udao).writeToFile();
		
		// A user is created with the given arguments
		// That user is of type Player
		// That user has a starting Currency of 1000
		User u = captor.getValue();
		assertEquals(10l, u.getPoints(), "Asserting starting number of points for user is 10");
		assertEquals(UserType.CUSTOMER, u.getType(), "Asserting user initializes as Customer");
		assertEquals(username, u.getUsername(), "Asserting username is correct");
		assertEquals(email, u.getEmail(), "Asserting email is correct");
		assertEquals(date, u.getBirthday(), "Asserting birthday is correct");
	}
}
