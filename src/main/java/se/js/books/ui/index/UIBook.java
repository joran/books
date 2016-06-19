package se.js.books.ui.index;

import java.util.Optional;
import java.util.UUID;

import se.js.books.domain.Book;
import se.js.books.domain.BookRating;
import se.js.books.domain.BookRatingRegistration;

public class UIBook {
	private UUID id;
	private String author;
	private String title;
	private Integer pages;
	private int  rate=0;
	
	public UIBook(Book book, BookRating rating) {
		super();
		this.id = book.getId();
		this.author = book.getAuthor();
		this.title = book.getTitle();
		this.pages = book.getPages();
		this.rate = rating.getRate();
	}

	public UIBook(Book book, Optional<BookRatingRegistration> possibleRating) {
		super();
		this.id = book.getId();
		this.author = book.getAuthor();
		this.title = book.getTitle();
		this.pages = book.getPages();
		this.rate = possibleRating.isPresent() ? possibleRating.get().getRating() : 0;

	}

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
	public String toString() {
		return "UIBook [id=" + id + ", author=" + author + ", title=" + title + ", pages=" + pages + ", rate=" + rate
				+ "]";
	}
}
