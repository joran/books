package se.js.books.service;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.js.books.domain.Book;
import se.js.books.service.events.BookEvent;

public class BooksWriteModel {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(BooksWriteModel.class);

	@Inject
	private EventService eventService;

	@Inject
	Snapshot<Book> books;

	public Book addNewBook(String author, String title, int pages) {
		Book book = new Book(author, title, pages);
		eventService.handleEvent(this::handleEvent).accept(BookEvent.created(book));
		return book;
	}

	public void removeBook(UUID id) {
		Optional<Book> optBook = findById(id);
		if (optBook.isPresent()) {
			eventService.handleEvent(this::handleEvent).accept(BookEvent.removed(optBook.get()));
		}
	}

	private Optional<Book> findById(UUID id) {
		return books.findById(id);
	}

	@PostConstruct
	private void init() {
		eventService.subscribe(this::handleEvent);
	}

	private void handleEvent(BookEvent event) {
		if (event != null) {
			Book book = event.getBook();
			switch (event.getType()) {
			case CREATED:
				books.add(book);
				break;
			case REMOVED:
				books.remove(book);
				break;
			case SYSTEM_RESET:
				books.clear();
			default:
				break;
			}
		}
	}
}
