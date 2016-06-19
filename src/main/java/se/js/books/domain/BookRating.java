package se.js.books.domain;

public class BookRating {
	private int rate = 0;

	public BookRating() {
		super();
	}

	public BookRating(int rate) {
		super();
		this.rate = rate;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	@Override
	public String toString() {
		return "BookRating [rate=" + rate + "]";
	}

}
