package se.js.books.domain;

import java.time.LocalDate;

public class BookReadRegistration {
	private LocalDate date;
	private Book book;
	public BookReadRegistration(Book book) {
		super();
		this.book = book;
		this.date = LocalDate.now();
	}
	public LocalDate getDate() {
		return date;
	}
	public Book getBook() {
		return book;
	}
}
