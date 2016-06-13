package se.js.books.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import se.js.books.domain.Book;

@Service
public class BookStoreService {
	List<Book> books = new ArrayList<>();

}
