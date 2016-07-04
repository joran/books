package se.js.books.service;

import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.js.books.config.ApplicationProperties;
import se.js.books.domain.Book;
import se.js.books.service.events.BookEvent;

@Service
public class EventService {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(EventService.class);

	private List<BookEvent> events = new ArrayList<>();
	private List<Consumer<BookEvent>> subscribers = new ArrayList<>();

	@Inject
	private ObjectMapper mapper;
	
	@Inject
	private ApplicationProperties props;

	public void subscribe(Consumer<BookEvent> subscriber) {
		subscribers.add(subscriber);
		replay(subscriber);
	}

	public Consumer<BookEvent> withPersistence(Consumer<BookEvent> consumer) {
		return withPublishToSubscriber(withMemoryCache(withFilePersistence(consumer)));
	}

	public void replay(Consumer<BookEvent> consumer) {
		events.stream().forEach(consumer);
	}

	@PostConstruct
	void init() {
		File file = new File(props.getEventsFile());
		if (file.exists()) {
			LOG.info("Reading events from file: " + file.getAbsolutePath());
			try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(props.getEventsFile()), "UTF-8"))) {
				events = in.lines().map(this::bookEventFromJsonString).collect(Collectors.toList());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (props.isTestDataEnabled() && events.isEmpty()) {
			LOG.info("Writing initial events to file: " + file.getAbsolutePath());

			Book[] books = new Book[] { new Book("Astrid Lindgren", "Pippi Långstrump", 55),
					new Book("J.K. Rawlings", "De vises sten", 385),
					new Book("J.K. Rawlings", "Den flammande bägaren", 463) };

			events = concat(stream(books).map(book -> BookEvent.created(book)), of(BookEvent.rated(books[0], 1)))
					.collect(Collectors.toList());

			events.stream().forEach(withFilePersistence(evt -> {
				LOG.info("Adding event to file: " + evt);
			}));
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

	private Consumer<BookEvent> withPublishToSubscriber(Consumer<BookEvent> consumer) {
		return (BookEvent event) -> {
			consumer.accept(event);
			subscribers.stream().forEach(subscriber -> subscriber.accept(event));
		};
	}

	private Consumer<BookEvent> withMemoryCache(Consumer<BookEvent> consumer) {
		return (BookEvent event) -> {
			consumer.accept(event);
			events.add(event);
		};
	}

	private Consumer<BookEvent> withFilePersistence(Consumer<BookEvent> consumer) {
		return (BookEvent event) -> {
			try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(props.getEventsFile(), true), "UTF-8"))) {
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
