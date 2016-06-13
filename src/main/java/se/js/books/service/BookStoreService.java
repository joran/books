package se.js.books.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import se.js.books.domain.Book;

@Service
public class BookStoreService {
	List<Book> books = new ArrayList<>();
	
	@PostConstruct
	void init() {
		books.add(new Book("Astrid Lindgren", "Pippi Långstrump", 55));
		books.add(new Book("J.K. Rawlings", "De vises sten", 385));
		books.add(new Book("J.K. Rawlings", "Den flammande bägaren", 463));
	}
	public Book addNewBook(String author, String title, int pages) {
		Book book = new Book(author, title, pages);
		books.add(book);
		return book;
	}
	
	public List<Book> findAllBooks(){
		return new ArrayList<>(books);
	}
	
	public void removeBook(UUID id) {
		books.removeIf(book -> book.getId().equals(id));
	}
	
	public void finishReadingBook(UUID id){
		LocalDate now = LocalDate.now();
		books.stream()
		.filter(book -> book.getId().equals(id))
		.forEach(book -> book.setFinishedReading(now));
	}
}
