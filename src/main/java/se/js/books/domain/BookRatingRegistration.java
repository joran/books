package se.js.books.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class BookRatingRegistration implements SnapshotEnabled {
	private Book book;
	private int rating = 0;
	private LocalDateTime added;
	private LocalDateTime removed;

	public BookRatingRegistration() {
		super();
	}

	public BookRatingRegistration(Book book, int rating) {
		super();
		this.book = book;
		this.rating = rating;
		this.added = LocalDateTime.now();
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

	public LocalDateTime getRemoved() {
		return removed;
	}

	public void setRemoved(LocalDateTime removed) {
		this.removed = removed;
	}

	@Override
	public UUID getId() {
		return book.getId();
	}

	@Override
	public LocalDateTime getAdded() {
		return added;
	}

	@Override
	public String toString() {
		return "BookRatingRegistration [book=" + book + ", rating=" + rating + ", added=" + added + ", removed="
				+ removed + "]";
	}
}
