/**
 *  JMongo is a mongodb driver writtern in java.
 *  Copyright (C) 2010  Xiaohu Huang
 *
 *  JMongo is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JMongo is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JMongo.  If not, see <http://www.gnu.org/licenses/>.
 */
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
