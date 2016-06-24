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

	private static final Logger LOG = LoggerFactory
			.getLogger(RatingsReadModel.class);
	
	@Inject
	private EventService eventService;
	
	MemorySnapshot<Book> books = new MemorySnapshot<>();

	MemorySnapshot<BookRatingRegistration> bookRatings = new MemorySnapshot<>();

	
	public Stream<BookRatingRegistration> findAllRatings(){
		return bookRatings.findAllNotRemoved();
	}

	public Optional<BookRatingRegistration> findLastRatingByBookId(UUID bookId){
		return bookRatings.findAllNotRemoved().reduce((a,b)->b);
	}

	
	@PostConstruct
	public void reload() {
		books.clear();
		bookRatings.clear();
		eventService.replay(this::handleEvent);
	}
	
	
	private void handleEvent(BookEvent event) {
		if(event == null) {
			return;
		}
		
		Book book = event.getBook();
		LOG.info("handleEvent: " + event );
		switch (event.getType()) {
		case REMOVED:
			bookRatings.remove(book.getId());
			break;
		case RATED:
			int rating = event.getRating();
			bookRatings.add(new BookRatingRegistration(book, rating));
			break;
		case RATING_INC:
			Optional<BookRatingRegistration> _ratings = bookRatings.findSomeById(book.getId());
			int newRating = _ratings.map(r -> r.getRating() + 1).orElse(1);
			bookRatings.add(new BookRatingRegistration(book, newRating));
			break;
		default:
			break;	
		}		
	}
}
