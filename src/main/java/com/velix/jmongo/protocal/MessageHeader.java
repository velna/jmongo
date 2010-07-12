package com.velix.jmongo.protocal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import com.velix.bson.BSONInputStream;
import com.velix.bson.BSONOutputStream;


public final class MessageHeader implements Writable, Readable, Serializable {
	private static final long serialVersionUID = 8505270142383450097L;
	private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

	private int messageLength;
	private int requestID;
	private int responseTo;
	private int opCode;

	public MessageHeader() {
		this.requestID = ID_GENERATOR.getAndIncrement();
	}

	@Override
	public void read(InputStream in) throws IOException {
		BSONInputStream bsonIn = new BSONInputStream(in);
		messageLength = bsonIn.readInteger();
		requestID = bsonIn.readInteger();
		responseTo = bsonIn.readInteger();
		opCode = bsonIn.readInteger();
	}

	@Override
	public void write(OutputStream output) throws IOException {
		BSONOutputStream out = new BSONOutputStream(output);
		out.writeInteger(messageLength);
		out.writeInteger(requestID);
		out.writeInteger(responseTo);
		out.writeInteger(opCode);
	}

	public int size() {
		return 16;
	}

	public int getMessageLength() {
		return messageLength;
	}

	public void setMessageLength(int messageLength) {
		this.messageLength = messageLength;
	}

	public int getRequestID() {
		return requestID;
	}

	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}

	public int getResponseTo() {
		return responseTo;
	}

	public void setResponseTo(int responseTo) {
		this.responseTo = responseTo;
	}

	public int getOpCode() {
		return opCode;
	}

	public void setOpCode(int opCode) {
		this.opCode = opCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("messageLength:").append(messageLength);
		builder.append(", requestID:").append(requestID);
		builder.append(", responseTo:").append(responseTo);
		builder.append(", opCode:").append(opCode);
		builder.append("}");
		return builder.toString();
	}

}
