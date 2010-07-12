package com.velix.bson;

public enum ElementType implements BSON {
	/**
	 * Floating point
	 */
	FLOATING_POINT((byte) 0x01),
	/**
	 * UTF-8 string
	 */
	UTF8_STRING((byte) 0x02),
	/**
	 * Embedded document
	 */
	EMBEDDED_DOCUMENT((byte) 0x03),
	/**
	 * Array
	 */
	ARRAY((byte) 0x04),
	/**
	 * Binary data
	 */
	BINARY((byte) 0x05),
	/**
	 * ObjectId
	 */
	OBJECT_ID((byte) 0x07),
	/**
	 * Boolean
	 */
	BOOLEAN((byte) 0x08),
	/**
	 * UTC datetime
	 */
	UTC_DATETIME((byte) 0x09),
	/**
	 * Null value
	 */
	NULL((byte) 0x0A),
	/**
	 * Regular expression
	 */
	REGULAR_EXPRESSION((byte) 0x0B),
	/**
	 * JavaScript code
	 */
	JAVASCRIPT_CODE((byte) 0x0D),
	/**
	 * Symbol
	 */
	SYMBOL((byte) 0x0E),
	/**
	 * JavaScript code w/ scope
	 */
	JAVASCRIPT_CODE_W_SCOPE((byte) 0x0F),
	/**
	 * 32-bit Integer
	 */
	INTEGER_32((byte) 0x10),
	/**
	 * Timestamp
	 */
	TIMESTAMP((byte) 0x11),
	/**
	 * 64-bit Integer
	 */
	INTEGER_64((byte) 0x12),
	/**
	 * Min key
	 */
	MIN_KEY((byte) 0xFF),
	/**
	 * Max key
	 */
	MAX_KEY((byte) 0x7F);

	private final byte value;

	private ElementType(final byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

	public static ElementType valueOf(byte value) {
		for (ElementType e : ElementType.values()) {
			if (e.value == value) {
				return e;
			}
		}
		return null;
	}
}
