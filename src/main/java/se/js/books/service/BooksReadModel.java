package se.js.books.service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.js.books.domain.Book;
import se.js.books.service.events.BookEvent;

@Service
public class BooksReadModel {

	private static final Logger LOG = LoggerFactory
			.getLogger(BooksReadModel.class);
	
	@Inject
	private EventService eventService;
	
	MemorySnapshot<Book> books = new MemorySnapshot<>();
	
	public Stream<Book> findAllAvailableBooks(){
		return books.findAllNotRemoved();
	}
	
	public Optional<Book> findUniqueById(UUID id) {
		return books.findSomeById(id);
	}
	
	@PostConstruct
	public void init() {
		books.clear();
		eventService.subscribe((BookEvent event) -> {
				if(event == null) {
					return;
				}
				
				LOG.info("Handle event " + event);
				
				Book book = event.getBook();
				switch (event.getType()) {
				case CREATED:
					books.add(book);
					break;
				case REMOVED:
					books.remove(book);
					break;
				default:
					break;	
				}		
		});
	}
}
