package se.js.books.service.events;

import java.time.LocalDateTime;

import se.js.books.domain.Book;
import se.js.books.util.DateUtil;

public class BookEvent {
	public enum Type {
		CREATED, REMOVED, READ, RATED, REVIEWED, RATING_INC
	};

	public static int RATE_UNRATED = 0;

	private Type type;
	private LocalDateTime occurred;

	private Book book;
	private String review = "";
	private int rating = RATE_UNRATED;

	private BookEvent() {
		super();
	}

	BookEvent(Type type) {
		this();
		this.type = type;
		this.occurred = LocalDateTime.now();
	}

	BookEvent(Type type, Book book) {
		this(type);
		this.book = book;
	}

	public BookEvent(Type type, Book book, int rate) {
		this(type, book);
		this.rating = rate;
	}

	BookEvent(Type type, Book book, String review) {
		this(type, book);
		this.review = review;
	}

	public static BookEvent created(Book book) {
		return new BookEvent(Type.CREATED, book);
	}

	public static BookEvent removed(Book book) {
		return new BookEvent(Type.REMOVED, book);
	}

	public static BookEvent read(Book book) {
		return new BookEvent(Type.READ, book);
	}

	public static BookEvent rated(Book book, int rating) {
		return new BookEvent(Type.RATED, book, rating);
	}

	public static BookEvent ratingIncremented(Book book) {
		return new BookEvent(Type.RATING_INC, book);
	}

	public static BookEvent reviewed(Book book, String review) {
		return new BookEvent(Type.REVIEWED, book, review);
	}

	public Type getType() {
		return type;
	}

	public LocalDateTime getOccurred() {
		return occurred;
	}

	public Book getBook() {
		return book;
	}

	public String getReview() {
		return review;
	}

	public int getRating() {
		return rating;
	}

	@Override
	public String toString() {
		return "BookEvent [type=" + type + ", occurred=" + DateUtil.format(occurred) + ", book=" + book + ", review="
				+ review + ", rating=" + rating + "]";
	}

}
