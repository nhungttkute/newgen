package com.newgen.am.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.BrokerCSV;
import com.newgen.am.dto.ListElementDTO;
import com.newgen.am.dto.UserInfoDTO;

@Service
public class CommonService {
	private String className = "CommonService";
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private RedisTemplate template;
	
	public List<ListElementDTO> getMemberList(HttpServletRequest request, long refId) {
		List<ListElementDTO> memberList = new ArrayList<ListElementDTO>();
		// get redis user info
		UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
		
		Document query = null;
		
		// check if user is mxv staff
		if (Utility.isNotNull(userInfo.getDeptCode())) {
			query = new Document();
		} else if (Utility.isNotNull(userInfo.getMemberCode())) {
			query = new Document();
			query.append("code", userInfo.getMemberCode());
		}
		
		if (query != null) {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");
			
			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("code", 1.0);
			projection.append("name", 1.0);
			
			MongoCursor<Document> cur = collection.find(query).projection(projection).iterator();
			while (cur.hasNext()) {
				ListElementDTO elemDto = mongoTemplate.getConverter().read(ListElementDTO.class, cur.next());
				if (elemDto != null) memberList.add(elemDto);
			}
		}
		
		return memberList;
	}
	
	public List<ListElementDTO> getBrokerList(HttpServletRequest request, long refId) {
		List<ListElementDTO> brokerList = new ArrayList<ListElementDTO>();
		// get redis user info
		UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
		
		Document query = null;
		
		// check if user is mxv staff
		if (Utility.isNotNull(userInfo.getDeptCode())) {
			query = new Document();
		} else if (Utility.isNotNull(userInfo.getMemberCode())) {
			query = new Document();
			query.append("memberCode", userInfo.getMemberCode());
			
			if (Utility.isNotNull(userInfo.getBrokerCode())) {
				query.append("code", userInfo.getBrokerCode());
			}
			
		}
		
		if (query != null) {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("brokers");
			
			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("memberCode", 1.0);
			projection.append("code", 1.0);
			projection.append("name", 1.0);
			
			MongoCursor<Document> cur = collection.find(query).projection(projection).iterator();
			while (cur.hasNext()) {
				ListElementDTO elemDto = mongoTemplate.getConverter().read(ListElementDTO.class, cur.next());
				if (elemDto != null) brokerList.add(elemDto);
			}
		}
		
		return brokerList;
	}
}
