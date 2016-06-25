package se.js.books.api;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import se.js.books.service.BooksReadModel;
import se.js.books.service.events.BookEvent;

@Controller
@RequestMapping("/api/admin")
public class AdminController {

	@Inject
	BooksReadModel booksProvider;

	@ResponseBody
	@RequestMapping(path = "events", method = RequestMethod.GET)
	public List<BookEvent> findAllEvents(HttpServletResponse response) {
		return booksProvider.getAllEvents();
	}
}
