package se.js.books.store;

import java.util.function.Consumer;
import java.util.stream.Stream;

import se.js.books.service.events.BookEvent;

public interface BookEventStore extends Consumer<BookEvent>{

	Stream<BookEvent> replay();

}