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
package com.velix.jmongo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.velix.jmongo.protocol.IncomingMessage;
import com.velix.jmongo.protocol.OutgoingMessage;

public class NIOConnection extends AbstractConnection implements Connection {
	private static final Logger LOG = Logger.getLogger(NIOConnection.class);

	private SocketChannel channel;
	private Selector selector;
	private ByteBuffer buffer;
	private int retryCount;
	private Protocol protocol;
	private InetSocketAddress address;

	public NIOConnection(String host, int port, Protocol protocol) {
		this(new InetSocketAddress(host, port), protocol);
	}

	public NIOConnection(InetSocketAddress address, Protocol protocol) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("new nio connection");
		}
		this.address = address;
		buffer = ByteBuffer.allocateDirect(4 << 20);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		setProtocal(protocol);
	}

	@Override
	public void setProtocal(Protocol protocol) {
		this.protocol = protocol;
	}

	@Override
	public boolean isConnected() {
		if (null == channel) {
			return false;
		}
		return channel.isConnected();
	}

	public void connect() throws IOException {
		while (true) {
			try {
				if (LOG.isDebugEnabled()) {
					LOG.debug("try connect to " + address);
				}
				checkConnection();
				return;
			} catch (IOException e) {
				this.retryCount++;
				if (this.retryCount >= 3) {
					LOG.warn("connect failed, retry for the " + this.retryCount
							+ "th time ...");
				} else {
					throw e;
				}
			}
		}
	}

	private void checkConnection() throws IOException {
		if (null == selector || !selector.isOpen()) {
			selector = Selector.open();
		}
		if (null == channel || !channel.isOpen()) {
			channel = SocketChannel.open();
			channel.configureBlocking(false);
		}
		if (!channel.isRegistered()) {
			channel.register(selector, SelectionKey.OP_CONNECT);
		}
		if (channel.isConnected()) {
			channel.register(selector, SelectionKey.OP_READ);
			return;
		}
		channel.socket().setTcpNoDelay(true);
		channel.connect(address);
		selector.select();
		Iterator<SelectionKey> i = selector.selectedKeys().iterator();
		while (i.hasNext()) {
			SelectionKey key = i.next();
			if (key.isConnectable()) {
				if (channel.isConnectionPending()) {
					if (channel.finishConnect()) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("connected " + address);
						}
						i.remove();
						channel.register(selector, SelectionKey.OP_READ);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public IncomingMessage receive(Class<?> clazz) throws IOException {
		checkConnection();
		return protocol.receive(channel, selector, (Class)clazz);
	}

	@Override
	public void send(OutgoingMessage message) throws IOException {
		checkConnection();
		protocol.send(message, channel, selector);
	}

	@Override
	public void close() throws IOException {
		if (null != selector) {
			selector.close();
		}
		if (null != channel) {
			channel.close();
		}
	}

}
