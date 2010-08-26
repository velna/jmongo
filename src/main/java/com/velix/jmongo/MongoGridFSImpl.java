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
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.velix.bson.BSONDocument;
import com.velix.bson.Binary;
import com.velix.bson.ObjectId;
import com.velix.jmongo.util.MongoUtils;

public class MongoGridFSImpl extends MongoCollectionImpl implements MongoGridFS {

	private MongoCollection chunksCollection;

	public MongoGridFSImpl(ConnectionPool pool, String collectionName,
			MongoDB db) {
		super(pool, collectionName, db);
		this.name = collectionName + ".files";
		fullName = new StringBuilder().append(db.getName()).append(".").append(
				name).toString();
		chunksCollection = new MongoCollectionImpl(pool, collectionName
				+ ".chunks", db);
		this.setObjectClass(GridFSFile.class);
	}

	@Override
	public void save(BSONDocument doc) throws MongoWriteException,
			MongoException {
		if (!(doc instanceof GridFSFile)) {
			throw new IllegalArgumentException("doc must be type of "
					+ GridFSFile.class);
		}
		GridFSFile file = (GridFSFile) doc;
		ObjectId fileId = new ObjectId();
		file.setId(fileId);
		long chunkSize = file.getChunkSize();
		if (chunkSize <= 0) {
			chunkSize = GridFSFile.DEFAULT_CHUNCK_SIZE;
		}
		file.setChunkSize(chunkSize);
		byte[] buf = new byte[(int) chunkSize];
		int n = 0;
		try {
			InputStream inputStream = file.getInputStream();
			MessageDigest digest = MessageDigest.getInstance("md5");
			int count;
			int length = 0;
			BSONDocument chunk;
			while ((count = inputStream.read(buf)) >= 0) {
				chunk = new MongoDocument();
				chunk.put("files_id", fileId);
				chunk.put("n", n);
				byte[] data;
				if (count == chunkSize) {
					data = buf;
				} else {
					data = new byte[count];
					System.arraycopy(buf, 0, data, 0, count);
				}
				digest.update(data);
				chunk.put("data", new Binary(data, Binary.SubType.BINARY));
				chunksCollection.save(chunk);
				length += count;
				n++;
			}
			file.setMd5(MongoUtils.encodeHex(digest.digest()));
			file.setLength(length);
			super.save(Arrays.asList((BSONDocument) file));
			file.setMongoCollection(this);
			file.setSaved(true);
		} catch (IOException e) {
			throw new MongoWriteException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void save(List<BSONDocument> docList) throws MongoWriteException,
			MongoException {
		for (BSONDocument doc : docList) {
			save(doc);
		}
	}

	@Override
	public void setObjectClass(Class<? extends BSONDocument> clazz) {
		if (!GridFSFile.class.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException("must be type of "
					+ GridFSFile.class);
		}
		super.setObjectClass(clazz);
	}

	@Override
	public void remove(BSONDocument query, boolean singleRemove)
			throws MongoWriteException, MongoException {
		// find files to remove
		Cursor<GridFSFile> cursor = this.find(query);
		cursor.fields(new MongoDocument("_id", 1));
		if (singleRemove) {
			cursor.limit(1);
		}
		List<GridFSFile> fileList = cursor.toList();
		List<Object> fileIdList = new ArrayList<Object>(fileList.size());
		for (BSONDocument file : fileList) {
			fileIdList.add(file.get("_id"));
		}

		// remove files
		super.remove(query, singleRemove);

		// remove chunks
		BSONDocument chunkQuery = new MongoDocument("files_id",
				new MongoDocument("$in", fileIdList));
		chunksCollection.remove(chunkQuery, false);
	}

	@Override
	public void update(BSONDocument query, BSONDocument data, boolean upsert,
			boolean multiUpdate) throws MongoWriteException, MongoException {
		throw new UnsupportedOperationException(
				"update is not yet supported of GridFS");
	}

	public MongoCollection getChunksCollection() {
		return chunksCollection;
	}

}
