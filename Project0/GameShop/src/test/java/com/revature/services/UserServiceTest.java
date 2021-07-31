package com.revature.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.revature.data.UserDAO;
import com.revature.menu.Menu;
import com.revature.model.User;
import com.revature.model.UserType;

public class UserServiceTest {
	private static UserService service;
	private static User test1;
	private static User default_admin;
	
	@BeforeAll // Specifies that this static method will be run before any tests
	public static void setUpClass() {
		default_admin = service.udao.getUserbyID(1);
		test1 = service.udao.addUser("test1", "testuser@test.com", LocalDate.of(2007, 10, 10), UserType.CUSTOMER);
	}
	
	@BeforeEach // Specifies a method that is to be run before each test
	public void setUpTests() {
		service = new UserService(); // create a new userService for every test to maximize isolation
		service.udao = Mockito.mock(UserDAO.class);
	}
	
	@Test
	public void testRegister() {
		if(service.udao.checkUsername(test1.getUsername()))
			assertEquals(test1.getUsername(), service.register("test1", "testuser@test.com", LocalDate.of(2007, 10, 10)));
		
		else
			assertEquals(null, service.register("test1", "testuser@test.com", LocalDate.of(2007, 10, 10)));
//		assertEquals(User.class, service.login("xspark"));
//		assertEquals(User.class, service.login("101"))
		
	}
}