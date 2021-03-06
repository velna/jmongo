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
package com.velix.bson;


public class JavascriptCodeWS extends AbstractElement<CodeWS> {

	private static final long serialVersionUID = -352956563968273102L;

	public JavascriptCodeWS() {
		super();
	}

	public JavascriptCodeWS(CodeWS value) {
		setValue(value);
	}

	@Override
	public ElementType getElementType() {
		return ElementType.JAVASCRIPT_CODE_W_SCOPE;
	}

}
