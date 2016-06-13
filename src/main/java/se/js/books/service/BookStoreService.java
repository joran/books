package se.js.books.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import se.js.books.domain.Book;

@Service
public class BookStoreService {
	List<Book> books = new ArrayList<>();
	
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
	
}
