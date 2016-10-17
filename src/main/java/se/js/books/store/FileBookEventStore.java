package se.js.books.store;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.js.books.config.ApplicationProperties;
import se.js.books.service.events.BookEvent;

public class FileBookEventStore implements BookEventStore{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(FileBookEventStore.class);

	@Inject
	private ObjectMapper mapper;
	
	@Inject
	private ApplicationProperties props;


	@Override
	public void accept(BookEvent evt) {
		LOG.info("Writing event to file: " + props.getEventsFile() + " event: " + evt);
		try (BufferedWriter out = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(props.getEventsFile(), true), "UTF-8"))) {
			out.write(mapper.writeValueAsString(evt));
			out.newLine();
			out.flush();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see se.js.books.service.BookEventStore#replay()
	 */
	@Override
	public Stream<BookEvent> replay(){
		File file = new File(props.getEventsFile());
		if (file.exists()) {
			LOG.info("Reading events from file: " + file.getAbsolutePath());
			try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(props.getEventsFile()), "UTF-8"))) {
				return in.lines().map(this::bookEventFromJsonString).peek(System.out::println);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return Stream.empty();
	}
	
	private BookEvent bookEventFromJsonString(String jsonString) {
		BookEvent event = null;
		try {
			event = mapper.readValue(jsonString, BookEvent.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return event;
	}

}
