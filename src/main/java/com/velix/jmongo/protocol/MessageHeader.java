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
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import com.velix.bson.io.BSONInput;
import com.velix.bson.io.BSONOutput;

public final class MessageHeader implements Writable, Readable, Serializable {
	private static final long serialVersionUID = 8505270142383450097L;
	private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

	private int messageLength;
	private int requestID;
	private int responseTo;
	private OperationCode opCode;

	public MessageHeader(OperationCode opCode) {
		this.opCode = opCode;
		this.requestID = ID_GENERATOR.getAndIncrement();
	}

	@Override
	public void read(BSONInput in) throws IOException {
		messageLength = in.readInteger();
		requestID = in.readInteger();
		responseTo = in.readInteger();
		opCode = OperationCode.valueOf(in.readInteger());
	}

	@Override
	public void write(BSONOutput out) throws IOException {
		out.writeInteger(messageLength);
		out.writeInteger(requestID);
		out.writeInteger(responseTo);
		out.writeInteger(opCode.getValue());
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

	public OperationCode getOpCode() {
		return opCode;
	}

	public void setOpCode(OperationCode opCode) {
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
