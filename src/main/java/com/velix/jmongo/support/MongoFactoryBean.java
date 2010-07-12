package com.velix.jmongo.support;

import org.springframework.beans.factory.FactoryBean;

import com.velix.jmongo.Configuration;
import com.velix.jmongo.Mongo;
import com.velix.jmongo.impl.MongoImpl;

public class MongoFactoryBean extends Configuration implements FactoryBean {

	private String host;
	private int port;

	@Override
	public Object getObject() throws Exception {
		Mongo mongo = new MongoImpl(host, port, this);
		return mongo;
	}

	@Override
	public Class<?> getObjectType() {
		return Mongo.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
