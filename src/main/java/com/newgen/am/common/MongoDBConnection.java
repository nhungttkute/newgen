/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.common;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.jsr310.LocalDateCodec;
import org.bson.codecs.pojo.PojoCodecProvider;

/**
 *
 * @author nhungtt
 */
public class MongoDBConnection {
	private static MongoClient mongoClient = null;
	
	public static MongoDatabase getMongoDatabase() {
        String username = ConfigLoader.getMainConfig().getString(Constant.MONGODB_USERNAME);
        String password = ConfigLoader.getMainConfig().getString(Constant.MONGODB_PASSWORD);
        String host = ConfigLoader.getMainConfig().getString(Constant.MONGODB_HOST);
        int port = ConfigLoader.getMainConfig().getInt(Constant.MONGODB_PORT);
        int connectionsPerHost = ConfigLoader.getMainConfig().getInt(Constant.MONGODB_CONNECTIONS_PER_HOST);
        int connectionTimeout = ConfigLoader.getMainConfig().getInt(Constant.MONGODB_CONNECTION_TIMEOUT);
        int threadsAllowedToBlock = ConfigLoader.getMainConfig().getInt(Constant.MONGODB_THREADS_ALLOWED_TO_BLOCK);
        int maxWaitTime = ConfigLoader.getMainConfig().getInt(Constant.MONGODB_MAX_TIME_WAIT);
        int socketTimeout = ConfigLoader.getMainConfig().getInt(Constant.MONGODB_SOCKET_TIMEOUT);
        int heartbeatConnectTimeout = ConfigLoader.getMainConfig().getInt(Constant.MONGODB_HEARTBEAT_CONNECT_TIMEOUT);
        
        String database = ConfigLoader.getMainConfig().getString(Constant.MONGODB_DATABASE);
        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
        
        MongoClientOptions options = 
        		MongoClientOptions.builder()
        		            .threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlock)
        		.connectionsPerHost(connectionsPerHost)
        		.connectTimeout(connectionTimeout)
        		.maxWaitTime(maxWaitTime)
        		.socketTimeout(socketTimeout)
        		.heartbeatConnectTimeout(heartbeatConnectTimeout)
        		.writeConcern(WriteConcern.ACKNOWLEDGED).build();
        
        if (mongoClient == null) {
        	mongoClient = new MongoClient(new ServerAddress(host, port), credential, options);
        }
        
        return mongoClient.getDatabase(database);
    }

    public static void closeDatabase() {
    	if (mongoClient != null) {
    		mongoClient.close();
    	}
    }
}
