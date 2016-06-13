package se.js.books.domain;

import java.time.LocalDate;
import java.util.UUID;

public class Book {
	private UUID id;
	private String author;
	private String title;
	private Integer pages;
	private LocalDate finishedReading;
	
	
	private Book() {
		super();
	}

	Book(UUID id, String author, String title, int pages) {
		super();
		this.id = id;
		this.author = author;
		this.title = title;
		this.pages = pages;
	}

	public Book(String author, String titel, Integer pages) {
		this(UUID.randomUUID(), author, titel, pages);
	}
	
	public UUID getId() {
		return id;
	}
	public String getAuthor() {
		return author;
	}
	public String getTitle() {
		return title;
	}
	public int getPages() {
		return pages;
	}

	public LocalDate getFinishedReading() {
		return finishedReading;
	}

	public void setFinishedReading(LocalDate finishedReading) {
		this.finishedReading = finishedReading;
	}
}
