package com.velix.bson;


public class CodeWS implements BSON {

	private static final long serialVersionUID = 2091594189005651613L;

	private String javascriptCode;

	private BSONDocument document;

	public String getJavascriptCode() {
		return javascriptCode;
	}

	public void setJavascriptCode(String javascriptCode) {
		this.javascriptCode = javascriptCode;
	}

	public BSONDocument getDocument() {
		return document;
	}

	public void setDocument(BSONDocument document) {
		this.document = document;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("javascriptCode: ").append(
				this.javascriptCode).append("\n").append("document: ").append(
				document).toString();
	}

}
