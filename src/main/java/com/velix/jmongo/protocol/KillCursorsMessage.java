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
import java.util.List;

import com.velix.bson.io.BSONOutput;

public class KillCursorsMessage implements OutgoingMessage, MongoMessage {

	private static final long serialVersionUID = -3350216587439425208L;
	private MessageHeader messageHeader;
	private int numberOfCursorIDs;
	private List<Long> cursorIDs;

	public KillCursorsMessage() {
		messageHeader = new MessageHeader(OperationCode.OP_KILL_CURSORS);
	}

	public MessageHeader getMessageHeader() {
		return messageHeader;
	}

	@Override
	public void write(BSONOutput out) throws IOException {
		messageHeader.write(out);
		out.writeInteger(0);
		out.writeInteger(numberOfCursorIDs);
		if (null != cursorIDs) {
			for (Long id : cursorIDs) {
				out.writeLong(id);
			}
		}
		out.set(0, out.size());
	}

	public int getNumberOfCursorIDs() {
		return numberOfCursorIDs;
	}

	public void setNumberOfCursorIDs(int numberOfCursorIDs) {
		this.numberOfCursorIDs = numberOfCursorIDs;
	}

	public List<Long> getCursorIDs() {
		return cursorIDs;
	}

	public void setCursorIDs(List<Long> cursorIDs) {
		this.cursorIDs = cursorIDs;
	}

}
