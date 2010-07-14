package com.velix.jmongo;

public class MongoWriteException extends MongoException {

	private static final long serialVersionUID = 1325418903969417013L;

	public MongoWriteException() {
		super();
	}

	public MongoWriteException(String message, Throwable cause) {
		super(message, cause);
	}

	public MongoWriteException(String message) {
		super(message);
	}

	public MongoWriteException(Throwable cause) {
		super(cause);
	}

}
