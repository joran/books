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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.js.books.domain.Book;
import se.js.books.service.event.BookEvent;

@Service
public class EventService {
	private static final String FILE_EVENTS_JSON = "events.json";
	List<BookEvent> events = new ArrayList<>();
	
	@Inject
	private ObjectMapper mapper;
	
	@PostConstruct
	void init() {
		File file = new File(FILE_EVENTS_JSON);
		if(file.exists()) {
			try(BufferedReader in = new BufferedReader(new FileReader(file))){
				events = in.lines()
				.map(this::bookEventFromJsonString)
				.collect(Collectors.toList());
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
			
			events = concat(stream(books)
					.map(book -> BookEvent.created(book)),
					of(BookEvent.rated(books[0], 1)))
					.collect(Collectors.toList());
		}
	}

	public Consumer<BookEvent> handleNewEvent(Consumer<BookEvent> consumer) {
		return withEventCache(withEventPersistence(consumer));
	}
	
	public void replay(Consumer<BookEvent> consumer) {
		events.stream().forEach(consumer);
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
}
