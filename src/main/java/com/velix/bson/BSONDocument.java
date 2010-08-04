package com.velix.bson;

import java.util.Map;
import java.util.Set;

public interface BSONDocument extends Iterable<BSONEntry> {

	int size();

	boolean isEmpty();

	boolean containsKey(String key);

	Object get(String key);

	Object put(String key, Object value);

	Object remove(String key);

	void putAll(BSONDocument doc);

	void putAll(Map<?, ?> mapDoc);

	public Map<String, Object> toMap();

	void clear();

	public Set<String> keySet();

}
