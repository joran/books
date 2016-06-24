package se.js.books.service;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.js.books.domain.Book;
import se.js.books.domain.BookRatingRegistration;
import se.js.books.service.events.BookEvent;

@Service
public class RatingsWriteModel {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory
			.getLogger(RatingsWriteModel.class);
	
	@Inject
	private EventService eventService;
	
	@Inject
	private BooksReadModel books;

	MemorySnapshot<BookRatingRegistration> bookRatings = new MemorySnapshot<>();

		
	private Optional<BookRatingRegistration> findLastRatingByBookId(UUID bookId){
		return bookRatings.findSomeById(bookId);
	}
	
	public void incRatingBook(UUID id) {
		Optional<Book> optBook = findBookById(id);
		if(optBook.isPresent()) {
			eventService.withPersistence(this::handleEvent).accept(BookEvent.ratingIncremented(optBook.get()));
		}
	}
	public Optional<BookRatingRegistration> rateBook(UUID id, int rating) {
		Optional<Book> optBook = findBookById(id);
		if(optBook.isPresent()) {
			eventService.withPersistence(this::handleEvent).accept(BookEvent.rated(optBook.get(), rating));
			return findLastRatingByBookId(id);
		}
		return Optional.empty();
	}
	
	@PostConstruct
	private void setup() {
		eventService.subscribe(this::handleEvent);
	}
	
	public void reload() {
		eventService.replay(this::handleEvent);
	}
	

	private Optional<Book> findBookById(UUID id){
		return books.findUniqueById(id);
	}
	
	private void handleEvent(BookEvent event) {
		if(event == null) {
			return;
		}
		
		Book book = event.getBook();
		Optional<BookRatingRegistration> optRatingReg = bookRatings.findSomeById(book.getId());
		switch (event.getType()) {
		case RATED:
			int rating = event.getRating();

			if(optRatingReg.isPresent()) {
				bookRatings.remove(optRatingReg.get());
			}
			bookRatings.add(new BookRatingRegistration(book, rating));
			break;
		case RATING_INC:
			if(optRatingReg.isPresent()) {
				optRatingReg.get().incRating();
			} else {
				bookRatings.add(new BookRatingRegistration(book, 1));
			}
			break;
		default:
			break;	
		}		
	}
}
