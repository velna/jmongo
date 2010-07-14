package com.velix.jmongo.protocol;

import java.io.IOException;
import java.io.InputStream;

import com.velix.bson.io.BSONInput;

public interface Readable {
	public void read(InputStream in) throws IOException;

	public void read(BSONInput in) throws IOException;
}
