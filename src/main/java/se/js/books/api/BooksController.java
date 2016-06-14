package se.js.books.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import se.js.books.domain.Book;
import se.js.books.service.BookStoreService;

@Controller
@RequestMapping("/api/books")
public class BooksController {

	@Inject
	BookStoreService bookstore;
	
    @ResponseBody
	@RequestMapping(method=RequestMethod.GET)
	public List<Book> findAll(){
		return bookstore.findAllAvailableBooks();
	}
	
    @ResponseBody
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public Book findBookByIdk(@PathVariable UUID id, HttpServletResponse response){
		Optional<Book> optBook = bookstore.findById(id);
		Book book = null;
		int status = HttpServletResponse.SC_NOT_FOUND;
		if(optBook.isPresent()) {
			book = optBook.get();
			status = HttpServletResponse.SC_OK;
		}
		response.setStatus(status);
		return book;
	}

	@ResponseBody
	@RequestMapping(method=RequestMethod.POST)
	public Book addNewBook(@RequestBody Book b){
    	return bookstore.addNewBook(b.getAuthor(), b.getTitle(), b.getPages());
//    	public Book addNewBook(String author,String title, int pages){
//		return bookstore.addNewBook(author, title, pages);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public void removeBook(@PathVariable UUID id, HttpServletResponse response){
		bookstore.removeBook(id);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
	
    @ResponseBody
	@RequestMapping(value="/{id}/read", method=RequestMethod.PUT)
	public void finishReadingBook(@PathVariable UUID id, HttpServletResponse response){
		bookstore.finishReadingBook(id);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

    @ResponseBody
	@RequestMapping(value="/{id}/incrating", method=RequestMethod.PUT)
	public void ratingBook(@PathVariable UUID id, HttpServletRequest request, HttpServletResponse response){
		bookstore.incRatingBook(id);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
}
