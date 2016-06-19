package se.js.books.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import se.js.books.domain.BookRatingRegistration;
import se.js.books.domain.BookReadRegistration;
import se.js.books.service.BookStoreService;
import se.js.books.service.event.BookEvent;

@Controller
@RequestMapping("/api/admin")
public class AdminController {

	@Inject
	BookStoreService bookstore;
	
    @ResponseBody
	@RequestMapping(path="reload", method=RequestMethod.GET)
	public void reload(HttpServletResponse response){
		bookstore.reload();
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
	
    @ResponseBody
	@RequestMapping(path="events", method=RequestMethod.GET)
	public List<BookEvent> findAllEvents(HttpServletResponse response){
		return bookstore.getAllEvents();
	}
	
    @ResponseBody
	@RequestMapping(path="report", method=RequestMethod.GET)
	public List<BookReadRegistration> report(HttpServletResponse response){
		return bookstore.buildReport();
	}

    @ResponseBody
	@RequestMapping(path="ratings", method=RequestMethod.GET)
	public Map<UUID, List<BookRatingRegistration>> ratings(HttpServletResponse response){
		return bookstore.findAllRatings();
	}
}
