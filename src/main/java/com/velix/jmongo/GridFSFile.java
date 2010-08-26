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

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import com.velix.bson.BSONDocument;

public class GridFSFile extends MongoDocument implements BSONDocument,
		MongoCollectionAware {
	private static final long serialVersionUID = -4107111593031139847L;

	public static final long DEFAULT_CHUNCK_SIZE = 256 * 1024;

	private Object id;
	private long length;
	private long chunkSize;
	private Date uploadDate;
	private String md5;

	private String filename;
	private String contentType;
	private List<String> aliases;
	private BSONDocument metadata;

	private boolean saved;

	private InputStream inputStream;
	private MongoGridFS gridFS;

	public GridFSFile() {
		saved = true;
	}

	public GridFSFile(InputStream inputStream) {
		saved = false;
		this.inputStream = inputStream;
	}

	public synchronized InputStream getInputStream() {
		if (isSaved()) {
			if (null == inputStream) {
				inputStream = new GridFSFileInputStream(this);
			}
		}
		return inputStream;

	}

	public MongoCollection getMongoCollection() {
		return gridFS;
	}

	public void setMongoCollection(MongoCollection collection) {
		this.gridFS = (MongoGridFS) collection;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object put(String key, Object value) {
		Object obj = super.put(key, value);
		if (key.equals("_id")) {
			id = value;
		} else if (key.equals("filename")) {
			filename = value == null ? null : value.toString();
		} else if (key.equals("contentType")) {
			contentType = (String) value;
		} else if (key.equals("length")) {
			length = (Long) value;
		} else if (key.equals("chunkSize")) {
			chunkSize = (Long) value;
		} else if (key.equals("aliases")) {
			aliases = (List<String>) value;
		} else if (key.equals("uploadDate")) {
			uploadDate = (Date) value;
		} else if (key.equals("md5")) {
			md5 = (String) value;
		} else {
			metadata.put(key, value);
		}
		return obj;
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		put("_id", id);
	}

	public long getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(long chunkSize) {
		put("chunkSize", chunkSize);
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		put("filename", filename);
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		put("contentType", contentType);
	}

	public List<String> getAliases() {
		return aliases;
	}

	public void setAliases(List<String> aliases) {
		put("aliases", aliases);
	}

	public BSONDocument getMetadata() {
		return metadata;
	}

	public void setMetadata(BSONDocument metadata) {
		put("metadata", metadata);
	}

	public long getLength() {
		return length;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public String getMd5() {
		return md5;
	}

	public void setLength(long length) {
		put("length", length);
	}

	public void setUploadDate(Date uploadDate) {
		put("uploadDate", uploadDate);
	}

	public void setMd5(String md5) {
		put("md5", md5);
	}

	public boolean isSaved() {
		return saved;
	}

	void setSaved(boolean saved) {
		this.saved = saved;
	}

}
