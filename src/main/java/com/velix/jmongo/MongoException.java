package com.velix.jmongo;

public class MongoException extends RuntimeException {

	private static final long serialVersionUID = -3369491039136574895L;

	public MongoException() {
		super();
	}

	public MongoException(String message, Throwable cause) {
		super(message, cause);
	}

	public MongoException(String message) {
		super(message);
	}

	public MongoException(Throwable cause) {
		super(cause);
	}

}
