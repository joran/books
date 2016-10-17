package se.js.books.store;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.js.books.service.events.BookEvent;

public class MemoryBookEventStore implements BookEventStore{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(MemoryBookEventStore.class);

	private ArrayList<BookEvent> events = new ArrayList<>();
	
	@Override
	public void accept(BookEvent evt) {
		LOG.info("Writing event to memory: " + evt);
		events.add(evt);
	}
	
	/* (non-Javadoc)
	 * @see se.js.books.service.BookEventStore#replay()
	 */
	@Override
	public Stream<BookEvent> replay(){
		LOG.info("Reading events from memory");
		return events.stream();
	}
	
}
