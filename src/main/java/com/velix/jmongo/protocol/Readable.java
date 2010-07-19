package com.velix.jmongo.protocol;

import java.io.IOException;

import com.velix.bson.io.BSONInput;

public interface Readable {
	public void read(BSONInput in) throws IOException;
}
