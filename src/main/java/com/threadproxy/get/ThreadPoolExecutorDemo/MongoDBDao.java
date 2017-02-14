package com.threadproxy.get.ThreadPoolExecutorDemo;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

public interface MongoDBDao {
	public boolean isExits(String dbName, String collectionName, ZhihuUserInfo userInfo);

	public boolean insert(String dbName, String collectionName, ZhihuUserInfo userInfo);

	public List<Document> find(String dbName, String collectionName, Bson filter);
}
