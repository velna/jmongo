package com.velix.jmongo.protocal;

import java.io.Serializable;

public interface MongoMessage extends Serializable {
	public MessageHeader getMessageHeader();
}
