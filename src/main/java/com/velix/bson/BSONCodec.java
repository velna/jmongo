package com.velix.bson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


import com.velix.bson.Binary.SubType;
import com.velix.bson.util.BSONUtils;

public class BSONCodec {

	public BSONDocument decode(ByteBuffer buffer) throws IOException {
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		BSONDocument ret = new BSONDocument();
		decode0(buffer, ret);
		return ret;
	}

	private void decode0(ByteBuffer buffer, BSONDocument context)
			throws IOException {
		int totalBytes = buffer.getInt();
		if (buffer.remaining() < totalBytes - 4) {
			throw new IOException("expect " + totalBytes + " bytes, but was "
					+ (buffer.remaining() + 4) + " bytes.");
		}
		byte typeValue;
		while ((typeValue = buffer.get()) != 0) {
			ElementType elementType = ElementType.valueOf(typeValue);
			if (null == elementType) {
				throw new IOException("unkown type value[" + typeValue + "]");
			}
			String name = readCString(buffer);
			Object element = null;
			switch (elementType) {
			case ARRAY:
				BSONDocument array = new BSONDocument();
				decode0(buffer, array);
				List<Object> list = new ArrayList<Object>(array.size());
				for (int i = 0;; i++) {
					String key = String.valueOf(i);
					if (array.containsKey(key)) {
						list.add(array.get(key));
					} else {
						break;
					}
				}
				element = list;
				break;
			case BINARY:
				int c = buffer.getInt();
				byte subTypeValue = buffer.get();
				Binary.SubType subType = SubType.valueOf(subTypeValue);
				if (null == elementType) {
					throw new IOException("unkown binay sub type value["
							+ subTypeValue + "]");
				}
				byte[] data = new byte[c];
				buffer.get(data);
				element = new Binary(data, subType);
				break;
			case BOOLEAN:
				element = buffer.get() > 0;
				break;
			case EMBEDDED_DOCUMENT:
				BSONDocument doc = new BSONDocument();
				element = doc;
				decode0(buffer, doc);
				break;
			case FLOATING_POINT:
				element = buffer.getDouble();
				break;
			case INTEGER_32:
				element = buffer.getInt();
				break;
			case INTEGER_64:
				element = buffer.getLong();
				break;
			case JAVASCRIPT_CODE:
				element = new JavascriptCode(readString(buffer));
				break;
			case JAVASCRIPT_CODE_W_SCOPE:
				CodeWS codeWS = new CodeWS();
				buffer.getInt();
				codeWS.setJavascriptCode(readString(buffer));
				// codeWS.setDocument(decode0(in));TODO
				element = new JavascriptCodeWS(codeWS);
				break;
			case MAX_KEY:
				element = Element.MAX_KEY;
				break;
			case MIN_KEY:
				element = Element.MIN_KEY;
				break;
			case NULL:
				element = Element.NULL;
				break;
			case OBJECT_ID:
				byte[] oidValue = new byte[12];
				buffer.get(oidValue);
				element = new ObjectId(oidValue);
				break;
			case REGULAR_EXPRESSION:
				String pattern = this.readCString(buffer);
				String flags = this.readCString(buffer);
				int f = 0;
				if (flags.contains("i")) {
					f |= Pattern.CASE_INSENSITIVE;
				}
				if (flags.contains("m")) {
					f |= Pattern.MULTILINE;
				}
				if (flags.contains("s")) {
					f |= Pattern.DOTALL;
				}
				element = Pattern.compile(pattern, f);
				break;
			case SYMBOL:
				element = new Symbol(this.readString(buffer));
				break;
			case TIMESTAMP:
				element = new Timestamp(buffer.getLong());
				break;
			case UTC_DATETIME:
				element = new Date(buffer.getLong());
				break;
			case UTF8_STRING:
				element = this.readString(buffer);
				break;
			}
			if (null != element) {
				context.put(name, element);
			} else {
				throw new IOException("decode error");
			}
		}
	}

	public void encode(ByteBuffer buffer, BSONDocument doc) throws IOException {
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		encode0(buffer, doc);
	}

	private int encode0(ByteBuffer buffer, BSONDocument context)
			throws IOException {
		final int initPos = buffer.position();
		if (null == context) {
			buffer.putInt(5).put((byte) 0);
			return 5;
		}
		for (Map.Entry<String, Object> entry : context.entrySet()) {
			String name = entry.getKey();
			if (null == name) {
				throw new IOException("name can not be null");
			}
			Object[] typeInfo = getTypeInfo(entry.getValue());
			ElementType type = (ElementType) typeInfo[0];
			Object value = typeInfo[1];
			buffer.put(type.getValue());
			writeCString(buffer, name);
			switch (type) {
			case ARRAY:
			case EMBEDDED_DOCUMENT:
				encode0(buffer, (BSONDocument) value);
				break;
			case BOOLEAN:
				buffer.put((Boolean) value ? (byte) 1 : (byte) 0);
				break;
			case BINARY:
				Binary binary = (Binary) value;
				byte[] binaryData = binary.getValue();
				buffer.putInt(binaryData.length);
				buffer.put(binary.getSubType().getValue());
				buffer.put(binaryData);
				break;
			case FLOATING_POINT:
				buffer.putDouble((Double) value);
				break;
			case INTEGER_32:
				buffer.putInt((Integer) value);
				break;
			case INTEGER_64:
				buffer.putLong((Long) value);
				break;
			case TIMESTAMP:
				buffer.putLong(((Timestamp) value).getValue());
				break;
			case UTC_DATETIME:
				buffer.putLong(((Date) value).getTime());
				break;
			case JAVASCRIPT_CODE:
				writeString(buffer, ((JavascriptCode) value).getValue());
				break;
			case SYMBOL:
				writeString(buffer, ((Symbol) value).getValue());
				break;
			case UTF8_STRING:
				writeString(buffer, (String) value);
				break;
			case JAVASCRIPT_CODE_W_SCOPE:
				CodeWS codeWS = (CodeWS) value;
				String javascriptCode = codeWS.getJavascriptCode();
				int leng = BSONUtils.stringByteLength(javascriptCode);
				// leng += encode0(codeWS.getDocument());TODO
				buffer.putInt(leng);
				writeString(buffer, javascriptCode);
				break;
			case MAX_KEY:
			case MIN_KEY:
			case NULL:
				// empty
				break;
			case OBJECT_ID:
				buffer.put(((ObjectId) value).getValue());
				break;
			case REGULAR_EXPRESSION:
				Pattern pattern = (Pattern) value;
				writeCString(buffer, pattern.pattern());
				int flags = pattern.flags();
				StringBuilder f = new StringBuilder();
				if ((Pattern.CASE_INSENSITIVE & flags) > 0) {
					f.append('i');
				}
				if ((Pattern.MULTILINE & flags) > 0) {
					f.append('m');
				}
				if ((Pattern.DOTALL & flags) > 0) {
					f.append('s');
				}
				writeCString(buffer, f.toString());
				// TODO: other flags ?
				break;
			}
		}
		return buffer.position() - initPos;
	}

	private Object[] getTypeInfo(Object element) throws IOException {
		Object[] ret = new Object[2];
		if (null == element) {
			ret[0] = ElementType.NULL;
			ret[1] = BSON.NULL;
		}
		if (element instanceof List<?>) {
			ret[0] = ElementType.ARRAY;
			BSONDocument doc = new BSONDocument();
			List<?> list = (List<?>) element;
			int i = 0;
			for (Object obj : list) {
				doc.put(String.valueOf(i), obj);
				i++;
			}
			ret[1] = doc;
		} else if (element instanceof BSONDocument) {
			ret[0] = ElementType.EMBEDDED_DOCUMENT;
			ret[1] = element;
		} else if (element instanceof Boolean) {
			ret[0] = ElementType.BOOLEAN;
			ret[1] = element;
		} else if (element instanceof Binary) {
			ret[0] = ElementType.BINARY;
			ret[1] = element;
		} else if (element instanceof Double) {
			ret[0] = ElementType.FLOATING_POINT;
			ret[1] = element;
		} else if (element instanceof Float) {
			ret[0] = ElementType.FLOATING_POINT;
			ret[1] = ((Float) element).doubleValue();
		} else if (element instanceof Long) {
			ret[0] = ElementType.INTEGER_64;
			ret[1] = element;
		} else if (element instanceof Byte) {
			ret[0] = ElementType.INTEGER_32;
			ret[1] = ((Byte) element).intValue();
		} else if (element instanceof Number) {
			ret[0] = ElementType.INTEGER_32;
			ret[1] = ((Number) element).intValue();
		} else if (element instanceof Timestamp) {
			ret[0] = ElementType.TIMESTAMP;
			ret[1] = element;
		} else if (element instanceof Date) {
			ret[0] = ElementType.UTC_DATETIME;
			ret[1] = element;
		} else if (element instanceof JavascriptCode) {
			ret[0] = ElementType.JAVASCRIPT_CODE;
			ret[1] = element;
		} else if (element instanceof Symbol) {
			ret[0] = ElementType.SYMBOL;
			ret[1] = element;
		} else if (element instanceof String) {
			ret[0] = ElementType.UTF8_STRING;
			ret[1] = element;
		} else if (element instanceof JavascriptCodeWS) {
			ret[0] = ElementType.JAVASCRIPT_CODE_W_SCOPE;
			ret[1] = element;
		} else if (element == BSON.MAX_KEY) {
			ret[0] = ElementType.MAX_KEY;
			ret[1] = element;
		} else if (element == BSON.MIN_KEY) {
			ret[0] = ElementType.MIN_KEY;
			ret[1] = element;
		} else if (element == BSON.NULL) {
			ret[0] = ElementType.NULL;
			ret[1] = element;
		} else if (element instanceof ObjectId) {
			ret[0] = ElementType.OBJECT_ID;
			ret[1] = element;
		} else if (element instanceof Pattern) {
			ret[0] = ElementType.REGULAR_EXPRESSION;
			ret[1] = element;
		} else if (element instanceof BSON) {
			// TODO
		} else {
			throw new IOException("unkown type [" + element.getClass() + "]");
		}
		return ret;
	}

	private String readCString(ByteBuffer buffer) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte b;
		while ((b = buffer.get()) >= 0) {
			if (b == 0) {
				return new String(out.toByteArray(), "UTF-8");
			} else {
				out.write(b);
			}
		}
		throw new IOException("can not read cstring from the stream");
	}

	private void writeCString(ByteBuffer buffer, String s) throws IOException {
		buffer.put(s.getBytes("UTF-8"));
		buffer.put((byte) 0);
	}

	private String readString(ByteBuffer buffer) throws IOException {
		int len = buffer.getInt();
		byte[] buf = new byte[len];
		buffer.get(buf);
		return new String(buf, 0, len - 1, "UTF-8");
	}

	private void writeString(ByteBuffer buffer, String s) throws IOException {
		byte[] bs = s.getBytes("UTF-8");
		buffer.putInt(bs.length + 1);
		buffer.put(bs);
		buffer.put((byte) 0);
	}

}
