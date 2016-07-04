package se.js.books.ui.myevents;

import java.time.LocalDateTime;
import java.util.UUID;

import se.js.books.domain.SnapshotEnabled;
import se.js.books.service.events.BookEvent;
import se.js.books.service.events.BookEvent.Type;
import se.js.books.util.DateUtil;

public class UIEvent implements SnapshotEnabled{
	private String header;
	private String bookTitle;
	private String bookAuthor;
	private String eventOccurred;
	private Type eventType;
	private int rating;

	LocalDateTime removed = null;
	UUID id;
	
	public UIEvent(BookEvent event) {
		this.header = event.getType().toString();
		this.id = event.getBook().getId();
		this.bookTitle = event.getBook().getTitle();
		this.bookAuthor = event.getBook().getAuthor();
		this.eventOccurred = DateUtil.dateTimeAsString(event.getOccurred());
		this.eventType = event.getType();
		this.rating = event.getRating();
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public String getBookAuthor() {
		return bookAuthor;
	}

	public void setBookAuthor(String bookAuthor) {
		this.bookAuthor = bookAuthor;
	}

	public String getEventOccurred() {
		return eventOccurred;
	}

	public void setEventOccurred(String eventOccurred) {
		this.eventOccurred = eventOccurred;
	}


	public Type getEventType() {
		return eventType;
	}


	public void setEventType(Type eventType) {
		this.eventType = eventType;
	}


	public int getRating() {
		return rating;
	}


	public void setRating(int rating) {
		this.rating = rating;
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
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getUiIconClass() {
		String iconClass = null;
		switch (eventType) {
		case CREATED:
			iconClass = "glyphicon-plus";
			break;
		case REMOVED:
			iconClass = "glyphicon-remove";
			break;
		case RATED:
			iconClass = "glyphicon-heart";
			break;
		default:
			iconClass = "glyphicon-info-sign";
			break;
		}
		
		return iconClass;
	}
}
