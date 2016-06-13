package se.js.books.domain;

import java.time.LocalDate;
import java.util.UUID;

public class Book {
	private final UUID id;
	private final String author;
	private final String titel;
	private final int pages;
	private LocalDate completed;
	
	
	Book(UUID id, String author, String titel, int pages) {
		super();
		this.id = id;
		this.author = author;
		this.titel = titel;
		this.pages = pages;
	}

	public Book(String author, String titel, int pages) {
		this(UUID.randomUUID(), author, titel, pages);
	}
	
	public UUID getId() {
		return id;
	}
	public String getAuthor() {
		return author;
	}
	public String getTitel() {
		return titel;
	}
	public int getPages() {
		return pages;
	}
	public LocalDate getCompleted() {
		return completed;
	}
	void setCompleted(LocalDate completed) {
		this.completed = completed;
	}

	public Book complete() {
		setCompleted(LocalDate.now());
		return this;
	}
}
