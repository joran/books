package se.js.books.ui.mybooks;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import se.js.books.domain.Book;
import se.js.books.domain.BookRating;
import se.js.books.domain.BookRatingRegistration;
import se.js.books.domain.SnapshotEnabled;

public class UIBook implements SnapshotEnabled{
	private UUID id;
	private String author;
	private String title;
	private Integer pages;
	private int rate = 0;
	private LocalDateTime removed;
	
	public UIBook(Book book, BookRating rating) {
		this(book, rating.getRate());
	}

	public UIBook(Book book, Optional<BookRatingRegistration> possibleRating) {
		this(book, possibleRating.isPresent() ? possibleRating.get().getRating() : 0);
	}

	public UIBook(Book book) {
		this(book, 0);
	}

	public UIBook(Book book, int rating) {
		super();
		this.id = book.getId();
		this.author = book.getAuthor();
		this.title = book.getTitle();
		this.pages = book.getPages();
		this.removed = book.getRemoved();
		this.rate = rating;		
	}

	@Override
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	@Override
	public LocalDateTime getRemoved() {
		return removed;
	}

	@Override
	public void setRemoved(LocalDateTime removed) {
		this.removed = removed;
	}

	@Override
	public String toString() {
		return "UIBook [id=" + id + ", author=" + author + ", title=" + title + ", pages=" + pages + ", rate=" + rate
				+ ", removed=" + removed + "]";
	}
}
