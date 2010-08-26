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

import com.velix.bson.BSONDocument;
import com.velix.bson.io.BSONEncoder;
import com.velix.bson.io.BSONOutput;
import com.velix.bson.util.BSONUtils;
import com.velix.jmongo.MongoDocument;

public class UpdateMessage implements OutgoingMessage, MongoMessage {

	private static final long serialVersionUID = -5278293474222136247L;

	private MessageHeader messageHeader;
	private String fullCollectionName;
	private int flags;
	private BSONDocument selector;
	private BSONDocument update;

	public UpdateMessage() {
		messageHeader = new MessageHeader(OperationCode.OP_UPDATE);
		this.selector = new MongoDocument();
		this.update = new MongoDocument();
	}

	@Override
	public void write(BSONOutput out) throws IOException {
		messageHeader.write(out);
		out.writeInteger(0);
		out.writeCString(this.fullCollectionName);
		out.writeInteger(flags);
		BSONEncoder.encode(selector, out);
		BSONEncoder.encode(update, out);
		out.set(0, out.size());
	}

	public void setUpsert(boolean upsert) {
		this.flags = (int) BSONUtils.bitSet(this.flags, 0, upsert);
	}

	public void setMultiUpdate(boolean multiUpdate) {
		this.flags = (int) BSONUtils.bitSet(this.flags, 1, multiUpdate);
	}

	public MessageHeader getMessageHeader() {
		return messageHeader;
	}

	public String getFullCollectionName() {
		return fullCollectionName;
	}

	public void setFullCollectionName(String fullCollectionName) {
		this.fullCollectionName = fullCollectionName;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public BSONDocument getSelector() {
		return selector;
	}

	public void setSelector(BSONDocument selector) {
		this.selector = selector;
	}

	public BSONDocument getUpdate() {
		return update;
	}

	public void setUpdate(BSONDocument update) {
		this.update = update;
	}
}
