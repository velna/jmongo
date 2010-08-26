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
package com.velix.jmongo.protocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.velix.bson.BSONDocument;
import com.velix.bson.io.BSONDecoder;
import com.velix.bson.io.BSONInput;

public class ReplyMessage<T extends BSONDocument> implements IncomingMessage, MongoMessage {

	private static final long serialVersionUID = -3350216587439425208L;
	private MessageHeader messageHeader;
	private int responseFlag;
	private long cursorID;
	private int startingFrom;
	private int numberReturned;
	private List<T> documents;
	private Class<T> clazz;

	public ReplyMessage(Class<T> clazz) {
		this.clazz = clazz;
		messageHeader = new MessageHeader(OperationCode.OP_REPLY);
	}

	@Override
	public void read(BSONInput in) throws IOException {
		messageHeader.read(in);
		responseFlag = in.readInteger();
		cursorID = in.readLong();
		startingFrom = in.readInteger();
		numberReturned = in.readInteger();
		documents = new ArrayList<T>(numberReturned);
		for (int i = 0; i < numberReturned; i++) {
			documents.add(BSONDecoder.decode(in, clazz));
		}
	}

	public MessageHeader getMessageHeader() {
		return messageHeader;
	}

	public int getResponseFlag() {
		return responseFlag;
	}

	public void setResponseFlag(int responseFlag) {
		this.responseFlag = responseFlag;
	}

	public long getCursorID() {
		return cursorID;
	}

	public void setCursorID(long cursorID) {
		this.cursorID = cursorID;
	}

	public int getStartingFrom() {
		return startingFrom;
	}

	public void setStartingFrom(int startingFrom) {
		this.startingFrom = startingFrom;
	}

	public int getNumberReturned() {
		return numberReturned;
	}

	public void setNumberReturned(int numberReturned) {
		this.numberReturned = numberReturned;
	}

	public List<T> getDocuments() {
		return documents;
	}

	public void setDocuments(List<T> documents) {
		this.documents = documents;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("messageHeader:").append(messageHeader);
		builder.append(", responseFlag:").append(responseFlag);
		builder.append(", cursorID:").append(cursorID);
		builder.append(", startingFrom:").append(startingFrom);
		builder.append(", numberReturned:").append(numberReturned);
		builder.append(", documents:").append(documents);
		builder.append("}");
		return builder.toString();
	}

}
