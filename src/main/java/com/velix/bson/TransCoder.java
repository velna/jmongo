package com.velix.bson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface TransCoder {
	byte[] encode(BSONDocument document) throws IOException;

	void encode(BSONDocument document, OutputStream out) throws IOException;

	BSONDocument decode(byte[] bs) throws IOException;

	BSONDocument decode(InputStream in) throws IOException;
}
