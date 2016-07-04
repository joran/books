package se.js.books.service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.js.books.service.events.BookEvent;
import se.js.books.ui.myevents.UIEvent;

public class MyEventsReadModel {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(MyEventsReadModel.class);

	@Inject
	private EventService eventService;

	@Inject
	Snapshot<UIEvent> events;

	public Stream<UIEvent> findAll() {
		return events.findAllNotRemoved();
	}

	public Optional<UIEvent> findByBookId(UUID bookId) {
		return events.findById(bookId);
	}

	@PostConstruct
	private void init() {
		eventService.subscribe(this::handleEvent);
	}

	private void handleEvent(BookEvent event) {
		if (event != null) {
			switch (event.getType()) {
			case CREATED:
			case REMOVED:
			case RATED:
			case RATING_INC: 
				LOG.info("Handling event " + event);
				events.add(new UIEvent(event));
				break;
			default:
				break;
			}
		}
	}

}
