/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.common;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import java.util.Arrays;

/**
 *
 * @author nhungtt
 */
public class MongoDBConnection {

    public static MongoDatabase getMongoDatabase() {
        String username = ConfigLoader.getMainConfig().getString(Constant.MONGODB_USERNAME);
        String password = ConfigLoader.getMainConfig().getString(Constant.MONGODB_PASSWORD);
        String host = ConfigLoader.getMainConfig().getString(Constant.MONGODB_HOST);
        int port = ConfigLoader.getMainConfig().getInt(Constant.MONGODB_PORT);
        String database = ConfigLoader.getMainConfig().getString(Constant.MONGODB_DATABASE);
        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress(host, port),
                Arrays.asList(credential));
        return mongoClient.getDatabase(database);
    }

}
