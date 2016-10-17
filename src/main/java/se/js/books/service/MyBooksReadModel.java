package se.js.books.service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.js.books.domain.Book;
import se.js.books.service.events.BookEvent;
import se.js.books.ui.mybooks.UIBook;

public class MyBooksReadModel {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(MyBooksReadModel.class);

	@Inject
	private EventService eventService;

	@Inject
	Snapshot<UIBook> books;

	public Stream<UIBook> findAllBooks() {
		return books.findAllNotRemoved();
	}

	public Optional<UIBook> findByBookId(UUID bookId) {
		return books.findById(bookId);
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
				LOG.info("Handling event " + event);
				books.add(new UIBook(book));
				break;
			case REMOVED:
				LOG.info("Handling event " + event);
				books.remove(book.getId());
				break;
			case RATED:
				LOG.info("Handling event " + event);
				int rating = event.getRating();
				UIBook uiBook = books.findById(book.getId()).orElse(new UIBook(book));
				uiBook.setRate(rating);
				books.save(uiBook);
				break;
			case RATING_INC: 
				LOG.info("Handling event " + event);
				UIBook uiBook1 = books.findById(book.getId()).orElse(new UIBook(book));
				int rating1 = uiBook1.getRate() + 1;
				uiBook1.setRate(rating1);
				books.save(uiBook1);
				break;
			case SYSTEM_RESET:
				books.clear();
			default:
				break;
			}
		}
	}

}
