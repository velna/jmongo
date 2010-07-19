package com.velix.jmongo.protocol;

import java.io.IOException;

import com.velix.bson.io.BSONOutput;

public interface Writable {
	public void write(BSONOutput out) throws IOException;
}
