package com.revature.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class Game implements Serializable {
	private static final long serialVersionUID = 7426075925303078777L;
	
	public String title;
	public GameRating rating;
	public GamePrice price;
	public LocalDate releaseDate;
	public GameStatus status;
	public LocalDate rentDate;
	public LocalDate returnDate;
	public String rentedBy;
	public String ownedBy;
	
	public Game() {
		super();
		this.status = GameStatus.AVAILABLE;
		this.price = GamePrice.BUY_PRICE;
		this.releaseDate = LocalDate.of(LocalDate.now().getYear(), 1, 1); // Set release date to current year
		this.rating = GameRating.E;		
	}
	
	public Game(String title, GameRating rating, LocalDate release, GameStatus status) {
		this.title = title;
		this.rating = rating;
		this.price = GamePrice.BUY_PRICE;
		this.releaseDate = release;
		this.status = (status == null ? GameStatus.AVAILABLE : status);
		
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public GameRating getRating() {
		return rating;
	}
	public void setRating(GameRating rating) {
		this.rating = rating;
	}
	public LocalDate getRelease_date() {
		return releaseDate;
	}
	public void setRelease_date(LocalDate release_date) {
		this.releaseDate = release_date;
	}
	public GameStatus getStatus() {
		return status;
	}
	public void setStatus(GameStatus status) {
		this.status = status;
	}
	public GamePrice getPrice() {
		return price;
	}
	public void setPrice(GamePrice price) {
		this.price = price;
	}
	public LocalDate getRentDate() {
		return rentDate;
	}
	public void setRentDate(LocalDate rentDate) {
		this.rentDate = rentDate;
	}
	public LocalDate getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}
	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getRentedBy() {
		return rentedBy;
	}

	public void setRentedBy(String rentedBy) {
		this.rentedBy = rentedBy;
	}

	public String getOwnedBy() {
		return ownedBy;
	}

	public void setOwnedBy(String ownedBy) {
		this.ownedBy = ownedBy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ownedBy == null) ? 0 : ownedBy.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((rating == null) ? 0 : rating.hashCode());
		result = prime * result + ((releaseDate == null) ? 0 : releaseDate.hashCode());
		result = prime * result + ((rentDate == null) ? 0 : rentDate.hashCode());
		result = prime * result + ((rentedBy == null) ? 0 : rentedBy.hashCode());
		result = prime * result + ((returnDate == null) ? 0 : returnDate.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		Game other = (Game) obj;
		if (ownedBy == null) {
			if (other.ownedBy != null)
				return false;
		} else if (!ownedBy.equals(other.ownedBy))
			return false;
		if (price != other.price)
			return false;
		if (rating != other.rating)
			return false;
		if (releaseDate == null) {
			if (other.releaseDate != null)
				return false;
		} else if (!releaseDate.equals(other.releaseDate))
			return false;
		if (rentDate == null) {
			if (other.rentDate != null)
				return false;
		} else if (!rentDate.equals(other.rentDate))
			return false;
		if (rentedBy == null) {
			if (other.rentedBy != null)
				return false;
		} else if (!rentedBy.equals(other.rentedBy))
			return false;
		if (returnDate == null) {
			if (other.returnDate != null)
				return false;
		} else if (!returnDate.equals(other.returnDate))
			return false;
		if (status != other.status)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Game [title=" + title + ", rating=" + rating + ", price=" + price + ", releaseDate=" + releaseDate
				+ ", status=" + status + ", rentDate=" + rentDate + ", returnDate=" + returnDate + ", rentedBy="
				+ rentedBy + ", ownedBy=" + ownedBy + "]";
	}

	
}
