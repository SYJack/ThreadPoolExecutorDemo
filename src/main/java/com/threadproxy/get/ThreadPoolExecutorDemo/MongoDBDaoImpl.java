package com.threadproxy.get.ThreadPoolExecutorDemo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

public class MongoDBDaoImpl implements MongoDBDao {
	private MongoClient mongoClient = null;

	public MongoDBDaoImpl(String host, int port) {
		if (mongoClient == null) {
			MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
			builder.maxWaitTime(1000 * 60 * 2);
			builder.connectTimeout(1000 * 60 * 1); // 与数据库建立连接的timeout设置为1分钟
			builder.socketTimeout(0);// 套接字超时时间，0无限制
			builder.connectionsPerHost(300); // 连接池设置为300个连接,默认为100
			builder.threadsAllowedToBlockForConnectionMultiplier(5000);
			builder.writeConcern(WriteConcern.ACKNOWLEDGED);
			MongoClientOptions options = builder.build();
			try {
				mongoClient = new MongoClient(new ServerAddress(host, port), options);
			} catch (MongoException e) {
				e.printStackTrace();
			}

		}
	}

	private volatile static MongoDBDaoImpl mongoDBDaoImpl = null;

	public synchronized static MongoDBDaoImpl getInstance(String host, int port) {
		if (mongoDBDaoImpl == null) {
			synchronized (MongoDBDaoImpl.class) {
				if (mongoDBDaoImpl == null) {
					mongoDBDaoImpl = new MongoDBDaoImpl(host, port);
				}
			}
		}
		return mongoDBDaoImpl;
	}

	public boolean isExits(String dbName, String collectionName, ZhihuUserInfo userInfo) {
		return false;
	}

	public boolean insert(String dbName, String collectionName, ZhihuUserInfo userInfo) {
		if (userInfo != null) {
			Map<String, Object> insertMap = new HashMap<String, Object>();
			insertMap.put("urlToken", userInfo.getUserToken());
			insertMap.put("username", userInfo.getUserName());
			insertMap.put("gender", userInfo.getGender());
			insertMap.put("business", userInfo.getBusiness());
			insertMap.put("company", userInfo.getCompany());
			insertMap.put("position", userInfo.getPosition());
			insertMap.put("education", userInfo.getEducation());
			insertMap.put("location", userInfo.getLocation());
			insertMap.put("major", userInfo.getMajor());
			insertMap.put("answersNum", userInfo.getAnswersNum());
			insertMap.put("starsNum", userInfo.getStarsNum());
			insertMap.put("thxNum", userInfo.getThxNum());
			insertMap.put("followingNum", userInfo.getFollowingNum());
			insertMap.put("followersNum", userInfo.getFollowersNum());
			insertMap.put("portrait", userInfo.getPortrait());
			insertMap.put("url", userInfo.getUrl());

			mongoClient.getDatabase(dbName).getCollection(collectionName).insertOne(new Document(insertMap));
		}
		return false;
	}

	public List<Document> find(String dbName, String collectionName, Bson filter) {
		return null;
	}

}
