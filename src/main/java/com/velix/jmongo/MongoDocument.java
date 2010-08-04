package com.velix.jmongo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.velix.bson.BSONDocument;
import com.velix.bson.BSONEntry;

public class MongoDocument implements BSONDocument {

	private final Map<String, Object> data;

	public MongoDocument() {
		data = new LinkedHashMap<String, Object>();
	}

	public MongoDocument(int initialCapacity) {
		data = new LinkedHashMap<String, Object>(initialCapacity);
	}

	public MongoDocument(BSONDocument doc) {
		if (null == doc) {
			data = new LinkedHashMap<String, Object>();
		} else {
			data = new LinkedHashMap<String, Object>(doc.size());
			this.putAll(doc);
		}
	}

	public MongoDocument(Map<?, ?> docMap) {
		if (null == docMap) {
			data = new LinkedHashMap<String, Object>();
		} else {
			data = new LinkedHashMap<String, Object>(docMap.size());
			this.putAll(docMap);
		}
	}

	@Override
	public void clear() {
		data.clear();
	}

	@Override
	public boolean containsKey(String key) {
		return data.containsKey(key);
	}

	@Override
	public Object get(String key) {
		return data.get(key);
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return data.keySet();
	}

	@Override
	public Object put(String key, Object value) {
		if (null == key) {
			throw new IllegalArgumentException("key can not be null");
		}
		return data.put(key, value);
	}

	@Override
	public void putAll(BSONDocument doc) {
		for (BSONEntry entry : doc) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void putAll(Map<?, ?> docMap) {
		for (Map.Entry<?, ?> entry : docMap.entrySet()) {
			put(entry.getKey().toString(), entry.getValue());
		}

	}

	@Override
	public Object remove(String key) {
		return data.remove(key);
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public Map<String, Object> toMap() {
		return new HashMap<String, Object>(data);
	}

	@Override
	public Iterator<BSONEntry> iterator() {
		return new BSONEntryIterator();
	}

	private class BSONEntryIterator implements Iterator<BSONEntry> {

		private Iterator<Map.Entry<String, Object>> i = data.entrySet()
				.iterator();

		@Override
		public boolean hasNext() {
			return i.hasNext();
		}

		@Override
		public BSONEntry next() {
			return new BSONEntryImpl(i.next());
		}

		@Override
		public void remove() {
			i.remove();
		}

	}

	private static class BSONEntryImpl implements BSONEntry {
		private final Map.Entry<String, Object> mapEntry;

		public BSONEntryImpl(Map.Entry<String, Object> mapEntry) {
			this.mapEntry = mapEntry;
		}

		@Override
		public String getKey() {
			return mapEntry.getKey();
		}

		@Override
		public Object getValue() {
			return mapEntry.getValue();
		}

	}

}
