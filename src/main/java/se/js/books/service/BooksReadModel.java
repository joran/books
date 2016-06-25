package se.js.books.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.js.books.domain.Book;
import se.js.books.service.events.BookEvent;

public class BooksReadModel {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(BooksReadModel.class);

	@Inject
	private EventService eventService;

	@Inject
	Snapshot<Book> books;

	public Stream<Book> findAllAvailableBooks() {
		return books.findAllNotRemoved();
	}

	public Optional<Book> findSomeById(UUID id) {
		return books.findById(id);
	}

	public List<BookEvent> getAllEvents() {
		List<BookEvent> e = new ArrayList<BookEvent>();
		eventService.replay(evt -> {
			e.add(evt);
		});
		return e;
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
			default:
				break;
			}
		}
	}

}
