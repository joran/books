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
import se.js.books.domain.BookRating;
import se.js.books.domain.BookRatingRegistration;
import se.js.books.service.BooksReadModel;

@Controller
@RequestMapping("/api/ratings")
public class RatingsController {

	@Inject
	BooksReadModel booksProvider;
	
	
    @ResponseBody
	@RequestMapping(method=RequestMethod.GET)
	public List<Book> findAll(){
		return booksProvider.findAllAvailableBooks();
	}
	
    @ResponseBody
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public Book findRatingByBookId(@PathVariable UUID id, HttpServletResponse response){
		Optional<Book> optBook = booksProvider.findById(id);
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
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public BookRatingRegistration ratingBook(@PathVariable UUID id, HttpServletRequest request, HttpServletResponse response, @RequestBody BookRating rating){
    	Optional<BookRatingRegistration> optBookRating = bookstore.rateBook(id, rating.getRate());
    	if(!optBookRating.isPresent()) {
    		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    		return null;
    	}
    	response.setStatus(HttpServletResponse.SC_OK);
		return optBookRating.get();
    }
    
}
