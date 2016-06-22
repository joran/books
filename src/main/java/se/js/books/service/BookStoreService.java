package se.js.books.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.js.books.domain.Book;
import se.js.books.domain.BookRatingRegistration;
import se.js.books.domain.BookReadRegistration;
import se.js.books.service.event.BookEvent;

@Service
public class BookStoreService {

	private static final Logger LOG = LoggerFactory
			.getLogger(BookStoreService.class);
	
	@Inject
	private EventService eventService;
	
	@Inject
	private BookModel bookModel;
	
	List<Book> books = new ArrayList<>();
	List<BookReadRegistration> booksRead = new ArrayList<>();
	Map<UUID, List<BookRatingRegistration>> bookRatings = new HashMap<>();

	
	public List<Book> findAllAvailableBooks(){
		return bookModel.findAllAvailableBooks();
	}
	
	public Optional<Book> findById(UUID id) {
		return bookModel.findById(id);
	}
	
	public List<BookReadRegistration> buildReport(){
		return booksRead;	
	}
	
	public Map<UUID, List<BookRatingRegistration>> findAllRatings(){
		return bookRatings;
	}

	public Optional<BookRatingRegistration> findLastRatingByBookId(UUID bookId){
		List<BookRatingRegistration> ratings = bookRatings.get(bookId);
		if(ratings == null) {
			return Optional.empty();			
		}
		return ratings.stream().reduce((a,b) -> b);
	}

	public Book addNewBook(String author, String title, int pages) {
		Book book = new Book(author, title, pages);
		eventService.withPersistence(this::handleEvent).accept(BookEvent.created(book));
		return book;
	}
	
	public void removeBook(UUID id) {
		Optional<Book> optBook = findById(id);
		if(optBook.isPresent()) {
			eventService.withPersistence(this::handleEvent).accept(BookEvent.removed(optBook.get()));
		}
	}
	
	public void finishReadingBook(UUID id){
		Optional<Book> optBook = findById(id);
		if(optBook.isPresent()) {
			eventService.withPersistence(this::handleEvent).accept(BookEvent.read(optBook.get()));
		}
	}
	
	public void incRatingBook(UUID id) {
		Optional<Book> optBook = findById(id);
		if(optBook.isPresent()) {
			eventService.withPersistence(this::handleEvent).accept(BookEvent.ratingIncremented(optBook.get()));
		}
	}
	public Optional<BookRatingRegistration> rateBook(UUID id, int rating) {
		Optional<Book> optBook = findById(id);
		if(optBook.isPresent()) {
			eventService.withPersistence(this::handleEvent).accept(BookEvent.rated(optBook.get(), rating));
		}
		return findLastRatingByBookId(id);
	}
	
	@PostConstruct
	public void reload() {
		books.clear();
		booksRead.clear();
		eventService.replay(this::handleEvent);
	}
	
	public List<BookEvent> getAllEvents(){
		List<BookEvent> e = new ArrayList<BookEvent>();
		eventService.replay(evt -> {
			e.add(evt);
			LOG.info("Event: " + evt);
		});
		return e;
	}

	private void handleEvent(BookEvent event) {
		if(event == null) {
			return;
		}
		
		Book book = event.getBook();
		LocalDate now = LocalDate.now();
		LOG.info("handleEvent: " + event );
		switch (event.getType()) {
		case CREATED:
			books.add(book);
			break;
		case REMOVED:
			books.stream()
			.filter(b -> b.equals(book))
			.forEach(b -> b.setRemoved(now));
			break;
		case READ:
			booksRead.add(new BookReadRegistration(book));
			break;
		case RATED:
			int rating = event.getRating();

			List<BookRatingRegistration> ratings = bookRatings.get(book.getId());
			if(ratings == null) {
				ratings = new ArrayList<>();
				bookRatings.put(book.getId(), ratings);
			}
			ratings.add(new BookRatingRegistration(book, rating));
			break;
		case RATING_INC:
			List<BookRatingRegistration> _ratings = bookRatings.get(book.getId());
			if(_ratings == null) {
				_ratings = new ArrayList<>();
				bookRatings.put(book.getId(), _ratings);
			}
			_ratings.add(new BookRatingRegistration(book, 1));
			break;
		case REVIEWED:
			break;
		default:
			break;	
		}		
	}
}
