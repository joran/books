package se.js.books.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import se.js.books.domain.Book;
import se.js.books.service.BooksReadModel;
import se.js.books.service.BooksWriteModel;

@Controller
@RequestMapping("/api/books")
public class BookController {

	@Inject
	BooksWriteModel booksWriter;

	@Inject
	BooksReadModel booksProvider;

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public List<Book> findAll() {
		return booksProvider.findAllAvailableBooks().collect(Collectors.toList());
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Book findBookByIdk(@PathVariable UUID id, HttpServletResponse response) {
		Optional<Book> optBook = booksProvider.findSomeById(id);
		Book book = null;
		int status = HttpServletResponse.SC_NOT_FOUND;
		if (optBook.isPresent()) {
			book = optBook.get();
			status = HttpServletResponse.SC_OK;
		}
		response.setStatus(status);
		return book;
	}

	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	public Book addNewBook(@RequestBody Book b) {
		return booksWriter.addNewBook(b.getAuthor(), b.getTitle(), b.getPages());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void removeBook(@PathVariable UUID id, HttpServletResponse response) {
		booksWriter.removeBook(id);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
}
