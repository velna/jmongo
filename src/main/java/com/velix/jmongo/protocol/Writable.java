package com.velix.jmongo.protocol;

import java.io.IOException;

import com.velix.bson.io.BSONOutputStream;

public interface Writable {
	public void write(BSONOutputStream out) throws IOException;
}
