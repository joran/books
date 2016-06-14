package se.js.books.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import se.js.books.domain.Book;
import se.js.books.domain.BookRatingRegistration;
import se.js.books.domain.BookReadRegistration;
import se.js.books.service.event.BookEvent;

@Service
public class BookStoreService {
	List<Book> books = new ArrayList<>();
	List<BookEvent> events = new ArrayList<>();
	List<BookReadRegistration> booksRead = new ArrayList<>();
	Map<UUID, List<BookRatingRegistration>> bookRatings = new HashMap<>();
	
	@PostConstruct
	void init() {
		events.add(BookEvent.created(new Book("Astrid Lindgren", "Pippi Långstrump", 55)));
		events.add(BookEvent.created(new Book("J.K. Rawlings", "De vises sten", 385)));
		events.add(BookEvent.created(new Book("J.K. Rawlings", "Den flammande bägaren", 463)));
		replay(events);
	}
	
	void replay(List<BookEvent> events) {
		books.clear();
		booksRead.clear();
		for (BookEvent event : events) {
			handleEvent(event);
		}
	}

	private void handleNewEvent(BookEvent event) {
		handleEvent(event);
		events.add(event);
	}
	
	private void handleEvent(BookEvent event) {
		Book book = event.getBook();
		LocalDate now = LocalDate.now();
		System.out.println("handleEvent: " + event );
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
	
	public List<Book> findAllAvailableBooks(){
		return books.stream()	
				.filter	(book -> book.getRemoved() == null)
				.collect(Collectors.toList());
	}
	
	public Optional<Book> findById(UUID id) {
		return books.stream()
				.filter(book -> book.getId().equals(id))
				.findFirst();
	}
	
	public List<BookReadRegistration> buildReport(){
		return booksRead;
	}
	
	public Map<UUID, List<BookRatingRegistration>> getRatings(){
		return bookRatings;
	}

	public Book addNewBook(String author, String title, int pages) {
		Book book = new Book(author, title, pages);
		handleNewEvent(BookEvent.created(book));
		return book;
	}
	
	public void removeBook(UUID id) {
		Optional<Book> optBook = findById(id);
		if(optBook.isPresent()) {
			handleNewEvent(BookEvent.removed(optBook.get()));
		}
	}
	
	public void finishReadingBook(UUID id){
		Optional<Book> optBook = findById(id);
		if(optBook.isPresent()) {
			handleNewEvent(BookEvent.read(optBook.get()));
		}
	}
	
	public void ratingBook(UUID id, int rating) {
		Optional<Book> optBook = findById(id);
		if(optBook.isPresent()) {
			handleNewEvent(BookEvent.rated(optBook.get(), rating));
		}
	}
	public void incRatingBook(UUID id) {
		Optional<Book> optBook = findById(id);
		if(optBook.isPresent()) {
			handleNewEvent(BookEvent.ratingIncremented(optBook.get()));
		}
	}
	public void reload() {
		replay(events);
	}
}
