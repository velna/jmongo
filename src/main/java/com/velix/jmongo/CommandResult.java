package com.velix.jmongo;

import java.util.List;

import com.velix.bson.BSONDocument;


public class CommandResult {

	private boolean ok;
	private String errorMessage;
	private List<BSONDocument> docs;

	public CommandResult(List<BSONDocument> docs) {
		if (null == docs) {
			throw new IllegalArgumentException("docs can not be null");
		}
		this.docs = docs;
		if (null != docs && docs.size() > 0) {
			BSONDocument firstDoc = docs.get(0);
			Double c = (Double) firstDoc.get("ok");
			ok = null == c || c.longValue() == 1;
			errorMessage = (String) firstDoc.get("$err");
		}
	}

	public boolean isOk() {
		return ok;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public BSONDocument getFirstDoc() {
		if (null == docs || docs.isEmpty()) {
			return null;
		} else {
			return docs.get(0);
		}
	}

	public Object get(String name) {
		if (null == docs || docs.isEmpty()) {
			return null;
		} else {
			return docs.get(0).get(name);
		}
	}

}
