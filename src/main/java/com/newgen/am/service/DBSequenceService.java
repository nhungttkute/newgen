/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.service;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.DBSequence;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 *
 * @author nhungtt
 */
@Service
public class DBSequenceService {

    private String className = "DBSequenceService";

    public long generateSequence(String seqName, long refId) {
        String methodName = "generateSequence";
        long sequenceValue = 0;
        try {
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
            MongoCollection<Document> collection = database.getCollection("database_sequences");
            
            BasicDBObject query = new BasicDBObject();
            query.put("_id", seqName);
            Document sequenceDoc = collection.find(query).first();
            DBSequence dbSeq = new Gson().fromJson(sequenceDoc.toJson(), DBSequence.class);
            sequenceValue = dbSeq.getSeq() + 1;
            
            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("_id", sequenceValue);

            BasicDBObject updateObj = new BasicDBObject();
            updateObj.put("$set", newDocument);
            
            collection.updateOne(query, updateObj);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return sequenceValue;
    }
}