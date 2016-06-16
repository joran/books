package se.js.books.domain;

import static se.js.books.util.DateUtil.format;

import java.time.LocalDate;
import java.util.UUID;

public class Book {
	private UUID id;
	private String author;
	private String title;
	private Integer pages;
	private LocalDate added;
	private LocalDate removed;
	
	public Book() {
		super();
	}

	Book(UUID id, String author, String title, int pages, LocalDate added, LocalDate removed) {
		this();
		this.id = id;
		this.author = author;
		this.title = title;
		this.pages = pages;
		this.added = added;
		this.removed = removed;
	}

	public Book(String author, String titel, Integer pages) {
		this(UUID.randomUUID(), author, titel, pages, LocalDate.now(), null);
	}
	
	public UUID getId() {
		return id;
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

	public LocalDate getAdded() {
		return added;
	}

	public void setAdded(LocalDate added) {
		this.added = added;
	}

	public LocalDate getRemoved() {
		return removed;
	}

	public void setRemoved(LocalDate removed) {
		this.removed = removed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Book [id=" + id + ", author=" + author + ", title=" + title + ", pages=" + pages + ", added=" + format(added)
				+ ", removed=" + format(removed) + "]";
	}
	
}
