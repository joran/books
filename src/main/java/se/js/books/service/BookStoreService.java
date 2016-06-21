package se.js.books.service;

import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.js.books.domain.Book;
import se.js.books.domain.BookRatingRegistration;
import se.js.books.domain.BookReadRegistration;
import se.js.books.service.event.BookEvent;

@Service
public class BookStoreService {
	private static final String FILE_EVENTS_JSON = "events.json";

	private static final Logger LOG = LoggerFactory
			.getLogger(BookStoreService.class);
	
	@Inject
	private ObjectMapper mapper;

	List<Book> books = new ArrayList<>();
	List<BookEvent> events = new ArrayList<>();
	List<BookReadRegistration> booksRead = new ArrayList<>();
	Map<UUID, List<BookRatingRegistration>> bookRatings = new HashMap<>();

	@PostConstruct
	void init() {
		File file = new File(FILE_EVENTS_JSON);
		if(file.exists()) {
			try(BufferedReader in = new BufferedReader(new FileReader(file))){
				events = in.lines()
				.map(this::bookEventFromJsonString)
				.collect(Collectors.toList());
				
				events.stream().forEach(this::handleEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if(events.isEmpty()) {
			Book[] books = new Book[] {
					new Book("Astrid Lindgren", "Pippi Långstrump", 55),
					new Book("J.K. Rawlings", "De vises sten", 385),
					new Book("J.K. Rawlings", "Den flammande bägaren", 463)
			};
			
			concat(stream(books)
					.map(book -> BookEvent.created(book)),
					of(BookEvent.rated(books[0], 1)))
			.forEach(this::handleNewEvent);
		}
	}
	
	private BookEvent bookEventFromJsonString(String jsonString) {
		BookEvent event = null;
		try {
			event = mapper.readValue(jsonString, BookEvent.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return event;
	}
	
	private void handleNewEvent(BookEvent event) {
		withEventCache(withEventPersistence(this::handleEvent)).accept(event);
	}
	
	private Consumer<BookEvent> withEventCache(Consumer<BookEvent> consumer) {
		return (BookEvent event) -> {
			consumer.accept(event);
			events.add(event);
		};
	}
	private Consumer<BookEvent>  withEventPersistence(Consumer<BookEvent> consumer) {
		return (BookEvent event) -> {
			try(BufferedWriter out = new BufferedWriter(new FileWriter(FILE_EVENTS_JSON, true))){
				consumer.accept(event);
				out.write(mapper.writeValueAsString(event));
				out.newLine();
				out.flush();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
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
	
	public void incRatingBook(UUID id) {
		Optional<Book> optBook = findById(id);
		if(optBook.isPresent()) {
			handleNewEvent(BookEvent.ratingIncremented(optBook.get()));
		}
	}
	public Optional<BookRatingRegistration> rateBook(UUID id, int rating) {
		Optional<Book> optBook = findById(id);
		if(optBook.isPresent()) {
			handleNewEvent(BookEvent.rated(optBook.get(), rating));
		}
		return findLastRatingByBookId(id);
	}
	
	public void reload() {
		books.clear();
		booksRead.clear();
		events.stream().forEach(this::handleEvent);
	}
	
	public List<BookEvent> getAllEvents(){
		for (BookEvent bookEvent : events) {
			LOG.info("Event: " + bookEvent);
		}
		return events;
	}
}
