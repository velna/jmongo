package com.velix.jmongo.protocal;

import java.io.IOException;
import java.io.OutputStream;

import com.velix.bson.BSONOutputStream;


public class GetMoreMessage implements OutgoingMessage, MongoMessage {

	private static final long serialVersionUID = -3350216587439425208L;
	private MessageHeader messageHeader;
	private String fullCollectionName;
	private int numberToReturn;
	private long cursorID;

	public GetMoreMessage() {
		messageHeader = new MessageHeader();
	}

	@Override
	public void write(OutputStream output) throws IOException {
		BSONOutputStream out = new BSONOutputStream(output);
		messageHeader.write(out);
		out.writeInteger(0);
		out.writeCString(this.fullCollectionName);
		out.writeInteger(this.numberToReturn);
		out.writeLong(cursorID);
	}

	public MessageHeader getMessageHeader() {
		return messageHeader;
	}

	public void setMessageHeader(MessageHeader messageHeader) {
		this.messageHeader = messageHeader;
	}

	public String getFullCollectionName() {
		return fullCollectionName;
	}

	public void setFullCollectionName(String fullCollectionName) {
		this.fullCollectionName = fullCollectionName;
	}

	public int getNumberToReturn() {
		return numberToReturn;
	}

	public void setNumberToReturn(int numberToReturn) {
		this.numberToReturn = numberToReturn;
	}

	public long getCursorID() {
		return cursorID;
	}

	public void setCursorID(long cursorID) {
		this.cursorID = cursorID;
	}

}
