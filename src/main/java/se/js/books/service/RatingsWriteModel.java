package se.js.books.service;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.js.books.domain.Book;
import se.js.books.domain.BookRatingRegistration;
import se.js.books.service.events.BookEvent;

public class RatingsWriteModel {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(RatingsWriteModel.class);

	@Inject
	private EventService eventService;

	@Inject
	private BooksReadModel books;

	@Inject
	private Snapshot<BookRatingRegistration> bookRatings;

	private Optional<BookRatingRegistration> findLastRatingByBookId(UUID bookId) {
		return bookRatings.findById(bookId);
	}

	public void incRatingBook(UUID id) {
		Optional<Book> optBook = findBookById(id);
		if (optBook.isPresent()) {
			eventService.withPersistence(this::handleEvent).accept(BookEvent.ratingIncremented(optBook.get()));
		}
	}

	public Optional<BookRatingRegistration> rateBook(UUID id, int rating) {
		Optional<Book> optBook = findBookById(id);
		if (optBook.isPresent()) {
			eventService.withPersistence(this::handleEvent).accept(BookEvent.rated(optBook.get(), rating));
			return findLastRatingByBookId(id);
		}
		return Optional.empty();
	}

	@PostConstruct
	private void init() {
		eventService.subscribe(this::handleEvent);
	}

	private Optional<Book> findBookById(UUID id) {
		return books.findSomeById(id);
	}

	private void handleEvent(BookEvent event) {
		if (event != null) {
			Book book = event.getBook();
			switch (event.getType()) {
			case RATED:
				int rating = event.getRating();
				BookRatingRegistration _rating = bookRatings.findById(book.getId()).orElse(new BookRatingRegistration(book, 0));
				_rating.setRating(rating);
				bookRatings.save(_rating);
				break;
			case RATING_INC:
				BookRatingRegistration _rating1 = bookRatings.findById(book.getId()).orElse(new BookRatingRegistration(book, 0));
				_rating1.incRating();
				bookRatings.save(_rating1);
				break;
			default:
				break;
			}
		}
	}
}
