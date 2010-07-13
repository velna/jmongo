package com.velix.jmongo.protocal;

public enum OperationCode {
	/**
	 * Reply to a client request. responseTo is set
	 */
	OP_REPLY(1),
	/**
	 * generic msg command followed by a string
	 */
	OP_MSG(1000),
	/**
	 * update document
	 */
	OP_UPDATE(2001),
	/**
	 * insert new document
	 */
	OP_INSERT(2002),
	/**
	 * formerly used for OP_GET_BY_OID
	 */
	RESERVED(2003),
	/**
	 * query a collection
	 */
	OP_QUERY(2004),
	/**
	 * Get more data from a query. See Cursors
	 */
	OP_GET_MORE(2005),
	/**
	 * Delete documents
	 */
	OP_DELETE(2006),
	/**
	 * Tell database client is done with a cursor
	 */
	OP_KILL_CURSORS(2007);

	private final int value;

	private OperationCode(int value) {
		this.value = value;
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

}
