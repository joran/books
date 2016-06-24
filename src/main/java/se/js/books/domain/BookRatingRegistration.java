package se.js.books.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class BookRatingRegistration implements RemovedAware, IdAware{
	private LocalDateTime date;
	private Book book;
	private int rating=0;
	private LocalDate removed;

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

	public int incRating() {
		this.rating = this.rating + 1;
		return this.rating;
	}
	public int getRating() {
		return rating;
	}

	public LocalDate getRemoved() {
		return removed;
	}

	public void setRemoved(LocalDate removed) {
		this.removed = removed;
	}

	@Override
	public UUID getId() {
		return book.getId();
	}

}
