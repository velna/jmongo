package com.velix.jmongo;

public class MongoAuthenticationException extends MongoException {

	private static final long serialVersionUID = -2031458656023301034L;

	public MongoAuthenticationException() {
		super();
	}

	public MongoAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public MongoAuthenticationException(String message) {
		super(message);
	}

	public MongoAuthenticationException(Throwable cause) {
		super(cause);
	}

}
