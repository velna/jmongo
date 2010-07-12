package com.velix.jmongo.protocal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.velix.bson.BSONDocument;
import com.velix.bson.BSONInputStream;
import com.velix.bson.TransCoderFactory;


public class ReplyMessage implements IncomingMessage, MongoMessage {

	private static final long serialVersionUID = -3350216587439425208L;
	private MessageHeader messageHeader;
	private int responseFlag;
	private long cursorID;
	private int startingFrom;
	private int numberReturned;
	private List<BSONDocument> documents;

	public ReplyMessage() {
		messageHeader = new MessageHeader();
	}

	@Override
	public boolean read(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		return false;
	}

	public void read(InputStream in) throws IOException {
		BSONInputStream bsonIn = new BSONInputStream(in);
		messageHeader.read(in);
		responseFlag = bsonIn.readInteger();
		cursorID = bsonIn.readLong();
		startingFrom = bsonIn.readInteger();
		numberReturned = bsonIn.readInteger();
		documents = new ArrayList<BSONDocument>(numberReturned);
		for (int i = 0; i < numberReturned; i++) {
			documents.add(TransCoderFactory.getInstance().decode(in));
		}
	}

	public MessageHeader getMessageHeader() {
		return messageHeader;
	}

	public void setMessageHeader(MessageHeader messageHeader) {
		this.messageHeader = messageHeader;
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

	public List<BSONDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<BSONDocument> documents) {
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
