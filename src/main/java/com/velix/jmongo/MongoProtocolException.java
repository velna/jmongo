package com.velix.jmongo;

public class MongoProtocolException extends MongoException {
	private static final long serialVersionUID = -4592172019123806387L;

	public MongoProtocolException() {
		super();
	}

	public MongoProtocolException(String message, Throwable cause) {
		super(message, cause);
	}

	public MongoProtocolException(String message) {
		super(message);
	}

	public MongoProtocolException(Throwable cause) {
		super(cause);
	}

}
