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

@Controller
@RequestMapping("/ui/mybooks")
public class MyBooksController {
	private static final Logger LOG = LoggerFactory
			.getLogger(MyBooksController.class);


	@Inject
	private BooksWriteModel bookstore;
	
	@Inject
	private BooksReadModel books;
	
	@ModelAttribute("book")
	private Book newBook(){
		return new Book();
	}

	@ModelAttribute("books")
	private List<UIBook> findAll(){
		List<UIBook> bs = books.findAllAvailableBooks().stream()
				.map(b -> new UIBook(b, bookstore.findLastRatingByBookId(b.getId())))
				.collect(Collectors.toList());
		return bs;
	}

	@ModelAttribute("totalPages")
	private int sum(){
		return books.findAllAvailableBooks().stream().mapToInt(b -> b.getPages()).sum();
	}

	@RequestMapping({"","/"})
	public String _index(){
		return "redirect:/ui/mybooks/index.html";
	}

	@RequestMapping("/index.html")
	public String index(Model model){
		return "index";
	}

	@RequestMapping(value="/index.html", params= {"saveBook"})
	public String save(@Valid final Book book, final BindingResult bindingResult){
		if(bindingResult.hasErrors()) {
			return "index";
		}
		this.bookstore.addNewBook(book.getAuthor(), book.getTitle(), book.getPages());
		return "redirect:/ui/mybooks/index.html";
	}
	
	@RequestMapping(value="/index.html", params= {"removeBook"})
	public String remove(final Book book, final BindingResult bindingResult, final HttpServletRequest req, final ModelMap model){
		UUID id = UUID.fromString(req.getParameter("removeBook"));
		this.bookstore.removeBook(id);
		model.clear();
		return "redirect:/ui/mybooks/index.html";
	}
}
