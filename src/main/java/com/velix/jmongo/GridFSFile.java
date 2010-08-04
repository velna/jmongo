package com.velix.jmongo;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import com.velix.bson.BSONDocument;

public abstract class GridFSFile extends MongoDocument implements BSONDocument {
	private static final long serialVersionUID = -4107111593031139847L;

	private Object id;
	private long length;
	private long chunkSize;
	private Date uploadDate;
	private String md5;

	private String filename;
	private String contentType;
	private List<String> aliases;
	private BSONDocument metadata;

	public abstract InputStream getInputStream();

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
		this.id = id;
	}

	public long getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(long chunkSize) {
		this.chunkSize = chunkSize;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public List<String> getAliases() {
		return aliases;
	}

	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

	public BSONDocument getMetadata() {
		return metadata;
	}

	public void setMetadata(BSONDocument metadata) {
		this.metadata = metadata;
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

}
