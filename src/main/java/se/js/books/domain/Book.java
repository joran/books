package se.js.books.domain;

import static se.js.books.util.DateUtil.formatISO8601;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

public class Book implements SnapshotEnabled {
	private UUID id;

	@Size(min = 1, max = 100)
	private String author;

	@Size(min = 1, max = 100)
	private String title;

	@NotNull
	@Min(1)
	@NumberFormat(style = Style.NUMBER)
	private Integer pages;

	private LocalDateTime added;
	private LocalDateTime removed;

	public Book() {
		super();
	}

	Book(UUID id, String author, String title, int pages, LocalDateTime added, LocalDateTime removed) {
		this();
		this.id = id;
		this.author = author;
		this.title = title;
		this.pages = pages;
		this.added = added;
		this.removed = removed;
	}

	public Book(String author, String titel, Integer pages) {
		this(UUID.randomUUID(), author, titel, pages, LocalDateTime.now(), null);
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

	public LocalDateTime getAdded() {
		return added;
	}

	public void setAdded(LocalDateTime added) {
		this.added = added;
	}

	public LocalDateTime getRemoved() {
		return removed;
	}

	public void setRemoved(LocalDateTime removed) {
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
		return "Book [id=" + id + ", author=" + author + ", title=" + title + ", pages=" + pages + ", added="
				+ formatISO8601(added) + ", removed=" + formatISO8601(removed) + "]";
	}

}
