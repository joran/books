package se.js.books.ui;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import se.js.books.domain.Book;
import se.js.books.service.BookStoreService;

@Controller
@RequestMapping("/ui")
public class IndexController {
	private static final Logger LOG = LoggerFactory
			.getLogger(IndexController.class);


	@Inject
	BookStoreService bookstore;
	
	@ModelAttribute("book")
	private Book newBook(){
		return new Book();
	}

	@ModelAttribute("books")
	private List<Book> findAll(){
		return bookstore.findAllAvailableBooks();
	}

	@ModelAttribute("totalPages")
	private int sum(){
		return bookstore.findAllAvailableBooks().stream().mapToInt(b -> b.getPages()).sum();
	}

	@RequestMapping({"/", "/index.html"})
	public String index(Model model){
		return "index";
	}

	@RequestMapping(value="/index.html", params= {"saveBook"})
	public String save(final Book book, final BindingResult bindingResult, final ModelMap model){
		if(bindingResult.hasErrors()) {
			return "index";
		}
		this.bookstore.addNewBook(book.getAuthor(), book.getTitle(), book.getPages());
		model.clear();
		return "redirect:/ui/index.html";
	}
	@RequestMapping(value="/index.html", params= {"removeBook"})
	public String remove(final Book book, final BindingResult bindingResult, final HttpServletRequest req, final ModelMap model){
		UUID id = UUID.fromString(req.getParameter("removeBook"));
		this.bookstore.removeBook(id);
		model.clear();
		return "redirect:/ui/index.html";
	}
}
