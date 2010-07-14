package com.velix.jmongo;

public class MongoCommandFailureException extends MongoException {

	private static final long serialVersionUID = -4862885500649105704L;

	public MongoCommandFailureException() {
		super();
	}

	public MongoCommandFailureException(String message, Throwable cause) {
		super(message, cause);
	}

	public MongoCommandFailureException(String message) {
		super(message);
	}

	public MongoCommandFailureException(Throwable cause) {
		super(cause);
	}

}
