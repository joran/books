package se.js.books.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import se.js.books.domain.SnapshotEnabled;
import se.js.books.service.BooksReadModel;
import se.js.books.service.BooksWriteModel;
import se.js.books.service.MemorySnapshot;
import se.js.books.service.MyBooksReadModel;
import se.js.books.service.MyEventsReadModel;
import se.js.books.service.RatingsReadModel;
import se.js.books.service.RatingsWriteModel;
import se.js.books.service.Snapshot;

@Configuration
public class ApplicationConfig {

	@Bean
	@Scope("prototype")
	public BooksReadModel booksReadModel() {
		return new BooksReadModel();
	}

	@Bean
	@Scope("prototype")
	public BooksWriteModel booksWriteModel() {
		return new BooksWriteModel();
	}

	@Bean
	@Scope("prototype")
	public RatingsReadModel ratingsReadModel() {
		return new RatingsReadModel();
	}

	@Bean
	@Scope("prototype")
	public RatingsWriteModel ratingsWriteModel() {
		return new RatingsWriteModel();
	}

	@Bean
	@Scope("prototype")
	public MyBooksReadModel myBooksReadModel() {
		return new MyBooksReadModel();
	}

	@Bean
	@Scope("prototype")
	public MyEventsReadModel myEventsReadModel() {
		return new MyEventsReadModel();
	}

	@Bean
	@Scope("prototype")
	public <T extends SnapshotEnabled> Snapshot<T> snapshot() {
		return new MemorySnapshot<>();
	}
}
