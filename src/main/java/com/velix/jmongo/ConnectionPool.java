package com.velix.jmongo;

import java.io.IOException;
import java.util.NoSuchElementException;

public interface ConnectionPool {
	Connection getConnection() throws IOException, NoSuchElementException,
			IllegalStateException;

	void close();
}
