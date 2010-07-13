package com.velix.bson;

import java.io.Serializable;

public interface BSON extends Serializable {
	public static final BSON MAX_KEY = MyBSON.MAX_KEY;
	public static final BSON MIN_KEY = MyBSON.MIN_KEY;
	public static final BSON NULL = MyBSON.NULL;
}
