package com.revature.model;

import java.io.Serializable;

/**
 * 
 * @author spark
 * PENDING: New user account registered, pending admin approval 
 * CUSTOMER: User account approved by admin. Has not rented any titles, only buys games or points
 * GAMER: User account with at least one game rented. Can participate in shop events and sweepstakes when availaible
 * ADMIN: Shop admin. Can manage user accounts and perform all user functions. Able to approve any pending users or requested titles
 * 
 */
public enum UserType implements Serializable {
	PENDING, CUSTOMER, GAMER, ADMIN
}
