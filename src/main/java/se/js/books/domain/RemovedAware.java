package se.js.books.domain;

import java.time.LocalDateTime;

public interface RemovedAware {
	public LocalDateTime getRemoved();
	public void setRemoved(LocalDateTime removed);
}
