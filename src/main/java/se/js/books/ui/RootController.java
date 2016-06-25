package se.js.books.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RootController {

	@RequestMapping({ "", "/", "/ui" })
	public String root() {
		return "redirect:/ui/mybooks/index.html";
	}
}
