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

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

public class ObjectId extends AbstractElement<byte[]> {

	private static final long serialVersionUID = 3892785050074060931L;
	private static final AtomicInteger INC = new AtomicInteger(0);
	private static final long MACHINE_PID;
	static {
		try {
			final long machine;
			StringBuilder sb = new StringBuilder();
			Enumeration<NetworkInterface> e = NetworkInterface
					.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface ni = e.nextElement();
				sb.append(ni.toString());
			}
			machine = ((long) sb.toString().hashCode()) << 16;

			final long pid = java.lang.management.ManagementFactory
					.getRuntimeMXBean().getName().hashCode() & 0xFFFF;

			MACHINE_PID = machine | pid;
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
	}

	public ObjectId(byte[] value) {
		if (null == value || value.length != 12) {
			throw new IllegalArgumentException(
					"value must be length of 12 bytes");
		}
		setValue(value);
	}

	public ObjectId() {
		byte[] value = new byte[12];
		int time = (int) (System.currentTimeMillis() / 1000);
		value[0] = (byte) ((time & 0xFF000000) >> 24);
		value[1] = (byte) ((time & 0xFF0000) >> 16);
		value[2] = (byte) ((time & 0xFF00) >> 8);
		value[3] = (byte) (time & 0xFF);

		value[4] = (byte) (MACHINE_PID & 0xFF);
		value[5] = (byte) ((MACHINE_PID & 0xFF00) >> 8);
		value[6] = (byte) ((MACHINE_PID & 0xFF0000) >> 16);
		value[7] = (byte) ((MACHINE_PID & 0xFF000000) >> 24);
		value[8] = (byte) ((MACHINE_PID & 0xFF00000000L) >> 32);

		int inc = INC.incrementAndGet();
		value[9] = (byte) ((inc & 0xFF0000) >> 16);
		value[10] = (byte) ((inc & 0xFF00) >> 8);
		value[11] = (byte) (inc & 0xFF);

		setValue(value);
	}

	@Override
	public ElementType getElementType() {
		return ElementType.OBJECT_ID;
	}

}
