package se.js.books.api;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
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
		return bookstore.findAllBooks();
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
	@RequestMapping(value="/{id}/done", method=RequestMethod.PUT)
	public void finishReadingBook(@PathVariable UUID id){
		bookstore.finishReadingBook(id);
	}
}
