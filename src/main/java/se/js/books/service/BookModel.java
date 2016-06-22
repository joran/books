package se.js.books.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.js.books.domain.Book;
import se.js.books.service.event.BookEvent;

@Service
public class BookModel {

	private static final Logger LOG = LoggerFactory
			.getLogger(BookModel.class);
	
	@Inject
	private EventService eventService;
	
	List<Book> books = new ArrayList<>();
	
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
	
	@PostConstruct
	public void init() {
		books.clear();
		eventService.subscribe((BookEvent event) -> {
				if(event == null) {
					return;
				}
				
				LOG.info("Handle event " + event);
				
				Book book = event.getBook();
				LocalDateTime occurred = event.getOccurred();
				switch (event.getType()) {
				case CREATED:
					books.add(book);
					break;
				case REMOVED:
					books.stream()
					.filter(b -> b.equals(book))
					.forEach(b -> b.setRemoved(occurred.toLocalDate()));
					break;
				default:
					break;	
				}		
		});
	}
}
