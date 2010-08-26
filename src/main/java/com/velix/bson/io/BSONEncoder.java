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
package com.velix.bson.io;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.velix.bson.BSON;
import com.velix.bson.BSONDocument;
import com.velix.bson.BSONEntry;
import com.velix.bson.Binary;
import com.velix.bson.CodeWS;
import com.velix.bson.ElementType;
import com.velix.bson.JavascriptCode;
import com.velix.bson.JavascriptCodeWS;
import com.velix.bson.ObjectId;
import com.velix.bson.Symbol;
import com.velix.bson.Timestamp;
import com.velix.bson.util.BSONUtils;
import com.velix.jmongo.MongoDocument;

public class BSONEncoder {
	private static final byte[] EMPTY_DOC = new byte[] { 5, 0 };

	public static void encode(BSONDocument document, BSONOutput out)
			throws IOException {
		if (null == document) {
			out.write(EMPTY_DOC);
		} else {
			int i = out.size();
			out.writeInteger(0);
			for (BSONEntry entry : document) {
				encodeEntry(entry, out);
			}
			out.write(0);
			out.setInteger(i, out.size() - i);
		}
	}

	private static void encodeEntry(BSONEntry entry, BSONOutput out)
			throws IOException {
		String name = entry.getKey();
		if (null == name) {
			// TODO throw an exception ?
			return;
		}

		Object[] typeInfo = getTypeInfo(entry.getValue());
		ElementType elementType = (ElementType) typeInfo[0];
		final Object value = typeInfo[1];

		out.write(elementType.getValue());
		out.writeCString(name);

		switch (elementType) {
		case ARRAY:
		case EMBEDDED_DOCUMENT:
			encode((BSONDocument) value, out);
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
			BSONOutput docOut = new BSONOutput(1024);
			encode(codeWS.getDocument(), docOut);
			byte[] docBytes = docOut.toByteArray();
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
			out.write(((ObjectId) value).getValue());
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
	}

	private static Object[] getTypeInfo(Object element) throws IOException {
		Object[] ret = new Object[2];
		if (null == element) {
			ret[0] = ElementType.NULL;
			ret[1] = BSON.NULL;
		}
		if (element instanceof List<?>) {
			ret[0] = ElementType.ARRAY;
			BSONDocument doc = new MongoDocument();
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
		} else {
			throw new IOException("unkown type [" + element.getClass() + "]");
		}
		return ret;
	}

}
