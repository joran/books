package se.js.books.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import se.js.books.domain.BookRating;
import se.js.books.domain.BookRatingRegistration;
import se.js.books.service.RatingsReadModel;
import se.js.books.service.RatingsWriteModel;

@Controller
@RequestMapping("/api/ratings")
public class RatingsController {

	@Inject
	RatingsReadModel ratingsProvider;

	@Inject
	RatingsWriteModel ratingsWriter;

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public List<BookRatingRegistration> findAll() {
		return ratingsProvider.findAllRatings().collect(Collectors.toList());
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public BookRatingRegistration findRatingByBookId(@PathVariable UUID id, HttpServletResponse response) {
		Optional<BookRatingRegistration> optRating = ratingsProvider.findByBookId(id);

		if (optRating.isPresent()) {
			response.setStatus(HttpServletResponse.SC_OK);
			return optRating.get();
		}
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		return null;
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public BookRatingRegistration ratingBook(@PathVariable UUID id, HttpServletRequest request,
			HttpServletResponse response, @RequestBody BookRating rating) {
		Optional<BookRatingRegistration> optBookRating = ratingsWriter.rateBook(id, rating.getRate());
		if (!optBookRating.isPresent()) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		return optBookRating.get();
	}

}
