package se.js.books.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

import se.js.books.domain.IdAware;
import se.js.books.domain.RemovedAware;

public class MemorySnapshot<T extends RemovedAware & IdAware> {
	List<T> items = new ArrayList<>();

	public void add(T t) {
		items.add(t);
	}
	
	public void remove(T t) {
		LocalDateTime now = LocalDateTime.now();
		filter(o -> o.equals(t))
		.forEach(o -> o.setRemoved(now));
	}
	
	public void remove(UUID id) {
		LocalDateTime now = LocalDateTime.now();
		filter(item -> item.getId().equals(id))
		.forEach(item -> item.setRemoved(now));
	}

	public Optional<T> findSomeById(UUID id){
		if(id == null) return Optional.empty();
		return filter(i ->  i.getId().equals(id)).findFirst();
	}
	
	public Stream<T> findAllNotRemoved(){
		return findByFilter(i -> i.getRemoved() != null);
	}

	public Stream<T> findByFilter(Predicate<T> predicate) {
		return filter(predicate);
	}

	private Stream<T> filter(Predicate<T> predicate) {
		return items.stream().filter(predicate);
	}
	
	public void clear() {
		items.clear();
	}
}
