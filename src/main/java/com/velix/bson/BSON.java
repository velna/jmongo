package com.velix.bson;

import java.io.Serializable;

public interface BSON extends Serializable {
	public static final BSON MAX_KEY = new MyBSON();
	public static final BSON MIN_KEY = new MyBSON();
	public static final BSON NULL = new MyBSON();
}

class MyBSON implements BSON {
	private static final long serialVersionUID = -3105143201968158898L;
}