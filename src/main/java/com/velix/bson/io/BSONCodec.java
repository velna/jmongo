package com.velix.bson.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.velix.bson.BSON;
import com.velix.bson.BSONDocument;
import com.velix.bson.Binary;
import com.velix.bson.CodeWS;
import com.velix.bson.ElementType;
import com.velix.bson.JavascriptCode;
import com.velix.bson.JavascriptCodeWS;
import com.velix.bson.ObjectId;
import com.velix.bson.Symbol;
import com.velix.bson.Timestamp;
import com.velix.bson.Binary.SubType;
import com.velix.bson.util.BSONUtils;

public class BSONCodec {

	public static BSONDocument decode(byte[] bs) throws IOException {
		if (null == bs) {
			return null;
		}
		return decode0(new BSONInputStream(new ByteArrayInputStream(bs)));
	}

	public static BSONDocument decode(InputStream in) throws IOException {
		if (null == in) {
			return null;
		}
		return decode0(new BSONInputStream(in));
	}

	private static BSONDocument decode0(BSONInputStream in) throws IOException {
		in.readInteger();
		BSONDocument document = new BSONDocument();
		byte typeValue;
		while ((typeValue = (byte) in.read()) != 0) {
			ElementType elementType = ElementType.valueOf(typeValue);
			if (null == elementType) {
				throw new IOException("unkown type value[" + typeValue + "]");
			}
			String name = in.readCString();
			Object element = null;
			switch (elementType) {
			case ARRAY:
				BSONDocument array = decode0(in);
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
				int c = in.readInteger();
				byte subTypeValue = (byte) in.read();
				Binary.SubType subType = SubType.valueOf(subTypeValue);
				if (null == elementType) {
					throw new IOException("unkown binay sub type value["
							+ subTypeValue + "]");
				}
				byte[] data = new byte[c];
				if (in.read(data) != c) {
					throw new IOException("invalid binary data");
				}
				element = new Binary(data, subType);
				break;
			case BOOLEAN:
				element = in.read() > 0;
				break;
			case EMBEDDED_DOCUMENT:
				element = decode0(in);
				break;
			case FLOATING_POINT:
				element = Double.longBitsToDouble(in.readLong());
				break;
			case INTEGER_32:
				element = in.readInteger();
				break;
			case INTEGER_64:
				element = in.readLong();
				break;
			case JAVASCRIPT_CODE:
				element = new JavascriptCode(in.readString());
				break;
			case JAVASCRIPT_CODE_W_SCOPE:
				CodeWS codeWS = new CodeWS();
				in.readInteger();
				codeWS.setJavascriptCode(in.readString());
				codeWS.setDocument(decode0(in));
				element = new JavascriptCodeWS(codeWS);
				break;
			case MAX_KEY:
				element = BSON.MAX_KEY;
				break;
			case MIN_KEY:
				element = BSON.MIN_KEY;
				break;
			case NULL:
				element = BSON.NULL;
				break;
			case OBJECT_ID:
				byte[] oidValue = new byte[12];
				if (in.read(oidValue) != 12) {
					throw new IOException("invalid object id");
				}
				element = new ObjectId(oidValue);
				break;
			case REGULAR_EXPRESSION:
				String pattern = in.readCString();
				String flags = in.readCString();
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
				element = new Symbol(in.readString());
				break;
			case TIMESTAMP:
				element = new Timestamp(in.readLong());
				break;
			case UTC_DATETIME:
				element = new Date(in.readLong());
				break;
			case UTF8_STRING:
				element = in.readString();
				break;
			}
			if (null != element) {
				document.put(name, element);
			} else {
				throw new IOException("decode error");
			}
		}
		return document;
	}

	public static byte[] encode(BSONDocument document) throws IOException {
		if (null == document) {
			return new byte[] { 5, 0 };
		}
		BSONOutputStream out = new BSONOutputStream(1024);
		out.writeInteger(0);
		for (Map.Entry<String, Object> entry : document.entrySet()) {
			byte[] bs = encodeEntry(entry);
			if (null != bs) {
				out.write(bs);
			}
		}
		out.write(0);
		out.set(0, out.size());
		return out.toByteArray();
	}

	public static void encode(BSONDocument document, OutputStream out)
			throws IOException {
		out.write(encode(document));
	}

	private static byte[] encodeEntry(Map.Entry<String, Object> entry)
			throws IOException {
		String name = entry.getKey();
		if (null == name) {
			return null;
		}
		BSONOutputStream out = new BSONOutputStream(1024);

		Object[] typeInfo = getTypeInfo(entry.getValue());
		ElementType elementType = (ElementType) typeInfo[0];
		final Object value = typeInfo[1];

		out.write(elementType.getValue());
		out.writeCString(name);

		switch (elementType) {
		case ARRAY:
		case EMBEDDED_DOCUMENT:
			out.write(encode((BSONDocument) value));
			break;
		case BOOLEAN:
			out.write((Boolean) value ? 1 : 0);
			break;
		case BINARY:
			Binary binary = (Binary) value;
			byte[] binaryData = binary.getValue();
			out.writeInteger(binaryData.length);
			out.write(binary.getSubType().getValue());
			out.write(binaryData);
			break;
		case FLOATING_POINT:
			Double v = (Double) value;
			out.writeLong(Double.doubleToLongBits(v));
			break;
		case INTEGER_32:
			out.writeInteger((Integer) value);
			break;
		case INTEGER_64:
		case TIMESTAMP:
			out.writeLong((Long) value);
			break;
		case UTC_DATETIME:
			out.writeLong(((Date) value).getTime());
			break;
		case JAVASCRIPT_CODE:
		case SYMBOL:
		case UTF8_STRING:
			out.writeString(value.toString());
			break;
		case JAVASCRIPT_CODE_W_SCOPE:
			CodeWS codeWS = (CodeWS) value;
			String javascriptCode = codeWS.getJavascriptCode();
			int leng = BSONUtils.stringByteLength(javascriptCode);
			byte[] docBytes = encode(codeWS.getDocument());
			leng += docBytes.length;
			out.writeInteger(leng);
			out.writeString(javascriptCode);
			break;
		case MAX_KEY:
		case MIN_KEY:
		case NULL:
			// empty
			break;
		case OBJECT_ID:
			out.write((byte[]) value);
			break;
		case REGULAR_EXPRESSION:
			Pattern pattern = (Pattern) value;
			out.writeCString(pattern.pattern());
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
			out.writeCString(f.toString());
			// TODO: other flags ?
			break;
		}
		return out.toByteArray();
	}

	private static Object[] getTypeInfo(Object element) throws IOException {
		Object[] ret = new Object[2];
		if (null == element) {
			ret[0] = ElementType.NULL;
			ret[1] = BSON.NULL;
		}
		if (element instanceof List<?>) {
			ret[0] = ElementType.ARRAY;
			BSONDocument doc = new BSONDocument(new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					int k1 = Integer.parseInt(o1);
					int k2 = Integer.parseInt(o2);
					return k1 - k2;
				}

			});
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

}
