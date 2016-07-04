package se.js.books.ui.myevents;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import se.js.books.service.MyEventsReadModel;

@Controller
@RequestMapping("/ui/myevents")
public class MyEventsController {
	
	@Inject
	private MyEventsReadModel events;
	
	@RequestMapping({ "", "/" })
	public String _index() {
		return "redirect:/ui/myevents/index.html";
	}
	
	@RequestMapping("/index.html")
	public String index(Model model) {
		List<UIEvent> es = events.findAll().collect(Collectors.toList());
		Collections.reverse(es);
		model.addAttribute("events", es);
		return "myevents/index";
	}

}
