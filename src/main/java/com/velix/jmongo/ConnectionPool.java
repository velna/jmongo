package com.velix.jmongo;

import java.io.IOException;
import java.util.NoSuchElementException;

public interface ConnectionPool {
	/**
	 * get a connection from the connection pool, one should call close method
	 * on the connection after do something over the connection
	 * 
	 * @return
	 * @throws IOException
	 * @throws NoSuchElementException
	 * @throws IllegalStateException
	 *             if this pool is already closed
	 */
	Connection getConnection() throws IOException, NoSuchElementException,
			IllegalStateException;

	/**
	 * close this connection pool
	 */
	void close();
}
