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
import java.util.List;

import com.velix.bson.BSONDocument;
import com.velix.bson.Binary;

class GridFSFileInputStream extends InputStream {

	private GridFSFile file;
	private byte[] buf;
	private int index;
	private int chunkCount;
	private int n;
	private MongoCollection chunksCollection;
	private BSONDocument query;

	public GridFSFileInputStream(GridFSFile file) {
		this.file = file;
		chunkCount = (int) (file.getLength() / file.getChunkSize())
				+ (file.getLength() % file.getChunkSize() == 0 ? 0 : 1);
		MongoGridFS gridFS = (MongoGridFS) file.getMongoCollection();
		chunksCollection = gridFS.getChunksCollection();
		query = new MongoDocument();
		query.put("files_id", file.getId());
	}

	@Override
	public int read() throws IOException {
		if (file.getLength() <= 0 || (n >= chunkCount && index >= buf.length)) {
			return -1;
		}
		if (null == buf || index >= buf.length) {
			query.put("n", n);
			List<BSONDocument> chunkList = chunksCollection.find(query).fields(
					new MongoDocument("data", 1)).toList();
			if (chunkList.size() == 1) {
				buf = ((Binary) chunkList.get(0).get("data")).getValue();
				index = 0;
				n++;
			} else if (chunkList.size() > 1) {
				throw new MongoException("chunk " + n + " of file "
						+ file.getId() + " must be unique");
			} else {
				throw new MongoException("can not read chunk " + n
						+ " of file " + file.getId());
			}
		}
		return buf[index++] & 0xFF;
	}
}
