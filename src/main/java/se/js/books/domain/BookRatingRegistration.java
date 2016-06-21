package se.js.books.domain;

import java.time.LocalDateTime;

public class BookRatingRegistration {
	private LocalDateTime date;
	private Book book;
	private int rating=0;

	public BookRatingRegistration() {
		super();
	}
	
	public BookRatingRegistration(Book book, int rating) {
		super();
		this.book = book;
		this.rating = rating;
		this.date = LocalDateTime.now();
	}

	public LocalDateTime getDate() {
		return date;
	}

	public Book getBook() {
		return book;
	}

	public int getRating() {
		return rating;
	}
	
	
}
