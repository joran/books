package se.js.books.service;

import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.js.books.config.ApplicationProperties;
import se.js.books.domain.Book;
import se.js.books.service.events.BookEvent;
import se.js.books.store.BookEventStore;

@Service
public class EventService {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(EventService.class);

	private List<BookEvent> events = new ArrayList<>();
	private List<Consumer<BookEvent>> subscribers = new ArrayList<>();

	@Inject
	private ApplicationProperties props;

	@Inject
	private BookEventStore eventStore;

	public void subscribe(Consumer<BookEvent> subscriber) {
		subscribers.add(subscriber);
		replay(subscriber);
	}

	public Consumer<BookEvent> handleEvent(Consumer<BookEvent> consumer) {
		return withPublishToSubscriber(withMemoryCache(withEventStore(consumer)));
	}
	
	public void handleEvent(BookEvent evt) {
		handleEvent((Consumer<BookEvent>)null).accept(evt);
	}

	public void replay(Consumer<BookEvent> consumer) {
		events.stream().forEach(consumer);
	}

	@PostConstruct
	void init() {
		events = eventStore.replay().collect(Collectors.toList());

		if (props.isTestDataEnabled() && events.isEmpty()) {
			Book[] books = new Book[] { new Book("Astrid Lindgren", "Pippi Långstrump", 55),
					new Book("J.K. Rawlings", "De vises sten", 385),
					new Book("J.K. Rawlings", "Den flammande bägaren", 463) };

			events = concat(stream(books).map(book -> BookEvent.created(book)), of(BookEvent.rated(books[0], 1)))
					.collect(Collectors.toList());

			events.stream().forEach(withEventStore(evt -> {
				LOG.info("Adding event to file: " + evt);
			}));
		}
	}

	private Consumer<BookEvent> withPublishToSubscriber(Consumer<BookEvent> consumer) {
		return (BookEvent event) -> {
			if(consumer != null) {
				consumer.accept(event);
			}
			subscribers.stream().forEach(subscriber -> subscriber.accept(event));
		};
	}

	private Consumer<BookEvent> withMemoryCache(Consumer<BookEvent> consumer) {
		return (BookEvent event) -> {
			if(consumer != null) {
				consumer.accept(event);
			}
			events.add(event);
		};
	}

	private Consumer<BookEvent> withEventStore(Consumer<BookEvent> consumer) {
		return (BookEvent event) -> {
			if(consumer != null) {
				eventStore.andThen(consumer).accept(event);
			} else {
				eventStore.accept(event);				
			}
		};
	}
}
