package com.velix.jmongo;

import java.util.HashSet;
import java.util.Set;

public class MongoAttachment {

	private Set<String> authenticatedDBs = new HashSet<String>();

	public Set<String> getAuthenticatedDBs() {
		return authenticatedDBs;
	}

	public boolean isDBAuthenticated(String dbname) {
		if (authenticatedDBs.contains("admin")) {
			return true;
		}
		return authenticatedDBs.contains(dbname);
	}

	public void addAuthenticatedDB(String dbname) {
		authenticatedDBs.add(dbname);
	}

	public boolean removeAuthenticatedDB(String dbname) {
		return authenticatedDBs.remove(dbname);
	}
}
