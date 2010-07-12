package com.velix.jmongo;

public interface ConnectionPool {
	Connection getConnection() throws Exception;

	void close() throws Exception;
}
