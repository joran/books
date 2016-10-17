package se.js.books.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.js.books.service.events.BookEvent;

public class EventsReadModel {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(EventsReadModel.class);

	@Inject
	private EventService eventService;

	private ArrayList<BookEvent> events = new ArrayList<>();
	
	public List<BookEvent> getAllEvents() {
		return new ArrayList<>(events);
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
				events.add(event);
				break;
			case SYSTEM_RESET:
				events.clear();
			default:
				break;
			}
		}
	}

}
