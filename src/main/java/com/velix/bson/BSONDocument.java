package com.velix.bson;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class BSONDocument extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = 8429510414360350473L;

	public BSONDocument() {
		super();
	}

	public BSONDocument(int initialCapacity) {
		super(initialCapacity);
	}

	public BSONDocument(Map<String, Object> m) {
		super(m);
	}

	public List<?> getArray(String name) {
		return (List<?>) this.get(name);
	}

	public Binary getBinary(String name) {
		return (Binary) this.get(name);
	}

	public Boolean getBoolean(String name) {
		return (Boolean) this.get(name);
	}

	public CodeWS getCodeWS(String name) {
		return (CodeWS) this.get(name);
	}

	public Date getDate(String name) {
		return (Date) this.get(name);
	}

	public BSONDocument getDocument(String name) {
		return (BSONDocument) this.get(name);
	}

	public Double getDouble(String name) {
		return (Double) this.get(name);
	}

	public Integer getInt(String name) {
		return (Integer) this.get(name);
	}

	public Long getLong(String name) {
		return (Long) this.get(name);
	}

	public ObjectId getObjectId(String name) {
		return (ObjectId) this.get(name);
	}

	public Pattern getRegularExpression(String name) {
		return (Pattern) this.get(name);
	}

	public String getString(String name) {
		return (String) this.get(name);
	}

	public Symbol getSymbol(String name) {
		return (Symbol) this.get(name);
	}

	public JavascriptCode getJavascriptCode(String name) {
		return (JavascriptCode) this.get(name);
	}

	public Timestamp getTimestamp(String name) {
		return (Timestamp) this.get(name);
	}

	public boolean isMaxKey(String name) {
		return this.get(name) == BSON.MAX_KEY;
	}

	public boolean isMinKey(String name) {
		return this.get(name) == BSON.MIN_KEY;
	}

	public boolean isNull(String name) {
		return this.get(name) == BSON.NULL;
	}

}
