package com.velix.jmongo;

import java.util.List;

public interface MongoAdmin extends MongoDB {
	List<String> getDBNames() throws MongoException;
}
