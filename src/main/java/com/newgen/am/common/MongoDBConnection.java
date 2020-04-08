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
import com.mongodb.client.MongoDatabase;
import java.util.Arrays;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.jsr310.LocalDateCodec;
import org.bson.codecs.pojo.PojoCodecProvider;

/**
 *
 * @author nhungtt
 */
public class MongoDBConnection {

    public static MongoDatabase getMongoDatabase() {
//        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
//            CodecRegistries.fromCodecs(new LocalDateCodec()),
//            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));  
//        MongoClientOptions options = MongoClientOptions.builder()
//            .codecRegistry(codecRegistry).build();
        
        String username = ConfigLoader.getMainConfig().getString(Constant.MONGODB_USERNAME);
        String password = ConfigLoader.getMainConfig().getString(Constant.MONGODB_PASSWORD);
        String host = ConfigLoader.getMainConfig().getString(Constant.MONGODB_HOST);
        int port = ConfigLoader.getMainConfig().getInt(Constant.MONGODB_PORT);
        String database = ConfigLoader.getMainConfig().getString(Constant.MONGODB_DATABASE);
        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress(host, port),
                Arrays.asList(credential));
//        MongoClient mongoClient = new MongoClient(new ServerAddress(host, port),
//                Arrays.asList(credential), options);
        return mongoClient.getDatabase(database);
    }

}
