package com.newgen.am.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.BrokerDTO;
import com.newgen.am.dto.CollaboratorDTO;
import com.newgen.am.dto.InvestorDTO;
import com.newgen.am.dto.ListElementDTO;
import com.newgen.am.dto.MemberDTO;
import com.newgen.am.dto.NewsUserInfo;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.exception.CustomException;

@Service
public class CommonService {
	private String className = "CommonService";
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private RedisTemplate template;
	
	@Autowired
	private ModelMapper modelMapper;
	
	public List<ListElementDTO> getMemberList(HttpServletRequest request, long refId) {
		String methodName = "getMemberList";
		List<ListElementDTO> memberList = new ArrayList<ListElementDTO>();
		
		try {
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
		} catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
		
		return memberList;
	}
	
	public List<ListElementDTO> getBrokerList(HttpServletRequest request, long refId) {
		String methodName = "getBrokerList";
		List<ListElementDTO> brokerList = new ArrayList<ListElementDTO>();
		try {
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
		} catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
		
		return brokerList;
	}
	
	public List<ListElementDTO> getCollaboratorList(HttpServletRequest request, long refId) {
		String methodName = "getCollaboratorList";
		List<ListElementDTO> collaboratorList = new ArrayList<ListElementDTO>();
		
		try {
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
					query.append("brokerCode", userInfo.getBrokerCode());
					
					if (Utility.isNotNull(userInfo.getCollaboratorCode())) {
						query.append("code", userInfo.getCollaboratorCode());
					}
				}
				
			}
			
			if (query != null) {
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("collaborators");
				
				Document projection = new Document();
				projection.append("_id", 0.0);
				projection.append("memberCode", 1.0);
				projection.append("brokerCode", 1.0);
				projection.append("code", 1.0);
				projection.append("name", 1.0);
				
				MongoCursor<Document> cur = collection.find(query).projection(projection).iterator();
				while (cur.hasNext()) {
					ListElementDTO elemDto = mongoTemplate.getConverter().read(ListElementDTO.class, cur.next());
					if (elemDto != null) collaboratorList.add(elemDto);
				}
			}
		} catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
		
		return collaboratorList;
	}
	
	public List<ListElementDTO> getInvestorList(HttpServletRequest request, long refId) {
		String methodName = "getInvestorList";
		List<ListElementDTO> investorList = new ArrayList<ListElementDTO>();
		
		try {
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
					query.append("brokerCode", userInfo.getBrokerCode());
					
					if (Utility.isNotNull(userInfo.getCollaboratorCode())) {
						query.append("collaboratorCode", userInfo.getCollaboratorCode());
						
						if (Utility.isNotNull(userInfo.getInvestorCode())) {
							query.append("investorCode", userInfo.getInvestorCode());
						}
					}
				}
				
			}
			
			if (query != null) {
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("investors");
				
				Document projection = new Document();
				projection.append("_id", 0.0);
	            projection.append("memberCode", 1.0);
	            projection.append("brokerCode", 1.0);
	            projection.append("collaboratorCode", 1.0);
	            projection.append("investorCode", 1.0);
	            projection.append("investorName", 1.0);
				
				MongoCursor<Document> cur = collection.find(query).projection(projection).iterator();
				while (cur.hasNext()) {
					ListElementDTO elemDto = mongoTemplate.getConverter().read(ListElementDTO.class, cur.next());
					if (elemDto != null) investorList.add(elemDto);
				}
			}
		} catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
		
		return investorList;
	}
	
	public List<NewsUserInfo> getAdminUserList(long refId) {
		String methodName = "getAdminUserList";
		List<NewsUserInfo> userList = new ArrayList<NewsUserInfo>();
		
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");
			
			Document query = new Document();
	        Document projection = new Document();
	        projection.append("_id", 0.0);
	        projection.append("users.username", 1.0);
	        projection.append("users.email", 1.0);
	        projection.append("users.phoneNumber", 1.0);
			
			MongoCursor<Document> cur = collection.find(query).projection(projection).iterator();
			while (cur.hasNext()) {
				MemberDTO memberDto = mongoTemplate.getConverter().read(MemberDTO.class, cur.next());
				if (memberDto != null && memberDto.getUsers() != null) {
					List<NewsUserInfo> users = modelMapper.map(memberDto.getUsers(), new TypeToken<List<NewsUserInfo>>(){}.getType());
					userList.addAll(users);
				}
			}
		} catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
		
		return userList;
	}
	
	public List<NewsUserInfo> getMemberUserList(long refId) {
		String methodName = "getMemberUserList";
		List<NewsUserInfo> userList = new ArrayList<NewsUserInfo>();
		
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");
			
			Document query = new Document();
	        Document projection = new Document();
	        projection.append("_id", 0.0);
	        projection.append("users.username", 1.0);
	        projection.append("users.email", 1.0);
	        projection.append("users.phoneNumber", 1.0);
			
			MongoCursor<Document> cur = collection.find(query).projection(projection).iterator();
			while (cur.hasNext()) {
				MemberDTO memberDto = mongoTemplate.getConverter().read(MemberDTO.class, cur.next());
				if (memberDto != null && memberDto.getUsers() != null) {
					List<NewsUserInfo> users = modelMapper.map(memberDto.getUsers(), new TypeToken<List<NewsUserInfo>>(){}.getType());
					userList.addAll(users);
				}
			}
		} catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
		
		return userList;
	}
	
	public List<NewsUserInfo> getBrokerUserList(long refId) {
		String methodName = "getBrokerUserList";
		List<NewsUserInfo> userList = new ArrayList<NewsUserInfo>();
		
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("brokers");
			
			Document query = new Document();
	        Document projection = new Document();
	        projection.append("_id", 0.0);
	        projection.append("user.username", 1.0);
	        projection.append("user.email", 1.0);
	        projection.append("user.phoneNumber", 1.0);
			
			MongoCursor<Document> cur = collection.find(query).projection(projection).iterator();
			while (cur.hasNext()) {
				BrokerDTO brokerDto = mongoTemplate.getConverter().read(BrokerDTO.class, cur.next());
				if (brokerDto != null && brokerDto.getUser() != null) {
					NewsUserInfo user = modelMapper.map(brokerDto.getUser(), NewsUserInfo.class);
					userList.add(user);
				}
			}
		} catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
		
		return userList;
	}
	
	public List<NewsUserInfo> getCollaboratorUserList(long refId) {
		String methodName = "getCollaboratorList";
		List<NewsUserInfo> userList = new ArrayList<NewsUserInfo>();
		
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("collaborators");
			
			Document query = new Document();
	        Document projection = new Document();
	        projection.append("_id", 0.0);
	        projection.append("user.username", 1.0);
	        projection.append("user.email", 1.0);
	        projection.append("user.phoneNumber", 1.0);
			
			MongoCursor<Document> cur = collection.find(query).projection(projection).iterator();
			while (cur.hasNext()) {
				CollaboratorDTO collaboratorDto = mongoTemplate.getConverter().read(CollaboratorDTO.class, cur.next());
				if (collaboratorDto != null && collaboratorDto.getUser() != null) {
					NewsUserInfo user = modelMapper.map(collaboratorDto.getUser(), NewsUserInfo.class);
					userList.add(user);
				}
			}
		} catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
		
		return userList;
	}
	
	public List<NewsUserInfo> getInvestorUserList(long refId) {
		String methodName = "getInvestorUserList";
		List<NewsUserInfo> userList = new ArrayList<NewsUserInfo>();
		
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");
			
			Document query = new Document();
	        Document projection = new Document();
	        projection.append("_id", 0.0);
	        projection.append("users.username", 1.0);
	        projection.append("users.email", 1.0);
	        projection.append("users.phoneNumber", 1.0);
			
			MongoCursor<Document> cur = collection.find(query).projection(projection).iterator();
			while (cur.hasNext()) {
				InvestorDTO investorDto = mongoTemplate.getConverter().read(InvestorDTO.class, cur.next());
				if (investorDto != null && investorDto.getUsers() != null) {
					List<NewsUserInfo> users = modelMapper.map(investorDto.getUsers(), new TypeToken<List<NewsUserInfo>>(){}.getType());
					userList.addAll(users);
				}
			}
		} catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
		
		return userList;
	}
}
