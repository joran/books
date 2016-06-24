package se.js.books.service;

import java.time.LocalDateTime;
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
import se.js.books.service.events.BookEvent;

@Service
public class BooksWriteModel {

	private static final Logger LOG = LoggerFactory
			.getLogger(BooksWriteModel.class);
	
	@Inject
	private EventService eventService;
	
	MemorySnapshot<Book> books = new MemorySnapshot<>();

	List<BookReadRegistration> booksRead = new ArrayList<>();
	Map<UUID, List<BookRatingRegistration>> bookRatings = new HashMap<>();

	
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

	private Optional<Book> findById(UUID id){
		if(id == null) return Optional.empty();
		return books.stream().filter(b -> b.getId().equals(id)).findFirst();
	}
	
	private void handleEvent(BookEvent event) {
		if(event == null) {
			return;
		}
		
		Book book = event.getBook();
		LocalDateTime now = LocalDateTime.now();
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
		default:
			break;	
		}		
	}
}
