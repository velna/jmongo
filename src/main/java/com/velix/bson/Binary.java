package com.velix.bson;


public class Binary extends AbstractElement<byte[]> {

	private static final long serialVersionUID = -475495378136741936L;

	public static enum SubType implements BSON {
		/**
		 * Function
		 */
		FUNCTION((byte) 0x01),
		/**
		 * Binary
		 */
		BINARY((byte) 0x02),
		/**
		 * UUID
		 */
		UUID((byte) 0x03),
		/**
		 * MD5
		 */
		MD5((byte) 0x05),
		/**
		 * User defined
		 */
		USER_DEFINED((byte) 0x80);

		private final byte value;

		private SubType(final byte value) {
			this.value = value;
		}

		public byte getValue() {
			return value;
		}

		public static SubType valueOf(byte value) {
			for (SubType e : SubType.values()) {
				if (e.value == value) {
					return e;
				}
			}
			return null;
		}
	}

	private SubType subType;

	public Binary(byte[] value, SubType subType) {
		setValue(value);
		this.subType = subType;
	}

	public SubType getSubType() {
		return subType;
	}

	@Override
	public ElementType getElementType() {
		return ElementType.BINARY;
	}

}
