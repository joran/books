package se.js.books.service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.js.books.domain.Book;
import se.js.books.domain.BookRatingRegistration;
import se.js.books.service.events.BookEvent;

@Service
public class RatingsReadModel {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(RatingsReadModel.class);

	@Inject
	private EventService eventService;

	@Inject
	Snapshot<BookRatingRegistration> bookRatings;

	public Stream<BookRatingRegistration> findAllRatings() {
		return bookRatings.findAllNotRemoved();
	}

	public Optional<BookRatingRegistration> findByBookId(UUID bookId) {
		return bookRatings.findById(bookId);
	}

	public void debug() {
		bookRatings.debug();
	}

	@PostConstruct
	private void init() {
		eventService.subscribe(this::handleEvent);
	}

	private void handleEvent(BookEvent event) {
		if (event != null) {
			Book book = event.getBook();
			switch (event.getType()) {
			case REMOVED:
				bookRatings.remove(book.getId());
				break;
			case RATED:
				int rating = event.getRating();
				bookRatings.remove(book.getId());
				bookRatings.add(new BookRatingRegistration(book, rating));
				break;
			case RATING_INC:
				Optional<BookRatingRegistration> _ratings = bookRatings.findById(book.getId());
				int newRating = _ratings.map(r -> r.getRating() + 1).orElse(1);
				bookRatings.remove(book.getId());
				bookRatings.add(new BookRatingRegistration(book, newRating));
				break;
			default:
				break;
			}
		}
	}

}
