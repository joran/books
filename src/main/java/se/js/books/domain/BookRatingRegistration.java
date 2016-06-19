package se.js.books.domain;

import java.time.LocalDate;

public class BookRatingRegistration {
	private LocalDate date;
	private Book book;
	private int rating=0;

	public BookRatingRegistration(Book book, int rating) {
		super();
		this.book = book;
		this.rating = rating;
		this.date = LocalDate.now();
	}

	public LocalDate getDate() {
		return date;
	}

	public Book getBook() {
		return book;
	}

	public int getRating() {
		return rating;
	}
	
	
}
