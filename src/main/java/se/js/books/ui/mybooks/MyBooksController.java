package se.js.books.ui.mybooks;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import se.js.books.domain.Book;
import se.js.books.service.BooksReadModel;
import se.js.books.service.BooksWriteModel;
import se.js.books.service.RatingsReadModel;
import se.js.books.service.RatingsWriteModel;

@Controller
@RequestMapping("/ui/mybooks")
public class MyBooksController {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(MyBooksController.class);

	@Inject
	private BooksWriteModel booksWriter;

	@Inject
	private BooksReadModel booksProvider;

	@Inject
	RatingsReadModel ratingsProvider;

	@Inject
	RatingsWriteModel ratingsWriter;

	@ModelAttribute("book")
	private Book newBook() {
		return new Book();
	}

	@ModelAttribute("books")
	public List<UIBook> findAll() {
		return booksProvider
				.findAllAvailableBooks()
				.map(b -> new UIBook(b, ratingsProvider.findByBookId(b.getId())))
				.collect(Collectors.toList());
	}

	@ModelAttribute("totalPages")
	private int sum() {
		return booksProvider.findAllAvailableBooks().mapToInt(b -> b.getPages()).sum();
	}

	@RequestMapping({ "", "/" })
	public String _index() {
		return "redirect:/ui/mybooks/index.html";
	}

	@RequestMapping("/index.html")
	public String index(Model model) {
		return "index";
	}

	@RequestMapping(value = "/index.html", params = { "saveBook" })
	public String save(@Valid final Book book, final BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "index";
		}
		this.booksWriter.addNewBook(book.getAuthor(), book.getTitle(), book.getPages());
		return "redirect:/ui/mybooks/index.html";
	}

	@RequestMapping(value = "/index.html", params = { "removeBook" })
	public String remove(final Book book, final BindingResult bindingResult, final HttpServletRequest req,
			final ModelMap model) {
		UUID id = UUID.fromString(req.getParameter("removeBook"));
		this.booksWriter.removeBook(id);
		model.clear();
		return "redirect:/ui/mybooks/index.html";
	}
}
