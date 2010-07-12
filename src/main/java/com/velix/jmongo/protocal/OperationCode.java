package com.velix.jmongo.protocal;

public enum OperationCode {
	/**
	 * Reply to a client request. responseTo is set
	 */
	OP_REPLY(1, ReplyMessage.class),
	/**
	 * generic msg command followed by a string
	 */
	OP_MSG(1000, null),
	/**
	 * update document
	 */
	OP_UPDATE(2001, UpdateMessage.class),
	/**
	 * insert new document
	 */
	OP_INSERT(2002, InsertMessage.class),
	/**
	 * formerly used for OP_GET_BY_OID
	 */
	RESERVED(2003, null),
	/**
	 * query a collection
	 */
	OP_QUERY(2004, QueryMessage.class),
	/**
	 * Get more data from a query. See Cursors
	 */
	OP_GET_MORE(2005, GetMoreMessage.class),
	/**
	 * Delete documents
	 */
	OP_DELETE(2006, DeleteMessage.class),
	/**
	 * Tell database client is done with a cursor
	 */
	OP_KILL_CURSORS(2007, KillCursorsMessage.class);

	private final int value;

	private final Class<? extends MongoMessage> clazz;

	private OperationCode(int value, Class<? extends MongoMessage> clazz) {
		this.value = value;
		this.clazz = clazz;
	}

	public int getValue() {
		return value;
	}

	public static OperationCode valueOf(int value) {
		for (OperationCode code : OperationCode.values()) {
			if (code.value == value) {
				return code;
			}
		}
		return null;
	}

	public MongoMessage newMessage() {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
