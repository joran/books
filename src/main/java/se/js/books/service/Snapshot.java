package se.js.books.service;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

import se.js.books.domain.SnapshotEnabled;

public interface Snapshot<T extends SnapshotEnabled> {

	void add(T t);

	void remove(T t);

	void remove(UUID id);
	
	void save(T t);

	Optional<T> findById(UUID id);

	Stream<T> findAllNotRemoved();

	Stream<T> findByFilter(Predicate<T> predicate);

	void clear();

	void debug();

}