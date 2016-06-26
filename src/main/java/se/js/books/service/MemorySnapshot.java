package se.js.books.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.js.books.domain.SnapshotEnabled;

public class MemorySnapshot<T extends SnapshotEnabled> implements Snapshot<T> {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(MemorySnapshot.class);

	List<T> items = new ArrayList<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.js.books.service.Snapshot#add(T)
	 */
	@Override
	public void add(T t) {
		items.add(t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.js.books.service.Snapshot#remove(T)
	 */
	@Override
	public void remove(T t) {
		remove(t.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.js.books.service.Snapshot#remove(java.util.UUID)
	 */
	@Override
	public void remove(UUID id) {
		items = filter(item -> !item.getId().equals(id)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.js.books.service.Snapshot#findById(java.util.UUID)
	 */
	@Override
	public Optional<T> findById(UUID id) {
		if (id == null)
			return Optional.empty();
		return filter(i -> i.getId().equals(id)).findFirst();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.js.books.service.Snapshot#findAllNotRemoved()
	 */
	@Override
	public Stream<T> findAllNotRemoved() {
		return findByFilter(i -> i.getRemoved() == null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * se.js.books.service.Snapshot#findByFilter(java.util.function.Predicate)
	 */
	@Override
	public Stream<T> findByFilter(Predicate<T> predicate) {
		return filter(predicate);
	}

	private Stream<T> filter(Predicate<T> predicate) {
		return items.stream().filter(predicate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.js.books.service.Snapshot#clear()
	 */
	@Override
	public void clear() {
		items.clear();
	}

	@Override
	public void debug() {
		LOG.info("===============================================");
		LOG.info("Class: " + this.getClass().getName());
		LOG.info("Id: " + System.identityHashCode(this));
		for (T i : items) {
			LOG.info(i.toString());
		}
		LOG.info("===============================================");
	}

	@Override
	public void save(T t) {
		// Update item in storage (usefull if stored items are persisted)
	}

}
