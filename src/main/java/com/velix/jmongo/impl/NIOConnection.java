package com.velix.jmongo.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.apache.log4j.Logger;


import com.velix.jmongo.Configuration;
import com.velix.jmongo.Connection;
import com.velix.jmongo.Protocal;
import com.velix.jmongo.protocal.IncomingMessage;
import com.velix.jmongo.protocal.OutgoingMessage;

public class NIOConnection implements Connection {
	private static final Logger LOG = Logger.getLogger(NIOConnection.class);

	private SocketChannel channel;
	private Selector selector;
	private ByteBuffer buffer;
	private int retryCount;
	private Protocal protocal;
	private InetSocketAddress address;

	public NIOConnection(String host, int port, Protocal protocal) {
		this(new InetSocketAddress(host, port), protocal);
	}

	public NIOConnection(InetSocketAddress address, Protocal protocal) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("new mongo db connection");
		}
		this.address = address;
		buffer = ByteBuffer.allocateDirect(4 << 20);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		setProtocal(protocal);
	}

	@Override
	public void setProtocal(Protocal protocal) {
		this.protocal = protocal;
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
				if (this.retryCount < Configuration.getInstance()
						.getMaxConnectRetry()) {
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

	public IncomingMessage receive() throws IOException {
		checkConnection();
		return protocal.receive(channel, selector);
	}

	@Override
	public void send(OutgoingMessage message) throws IOException {
		checkConnection();
		protocal.send(message, channel, selector);
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
