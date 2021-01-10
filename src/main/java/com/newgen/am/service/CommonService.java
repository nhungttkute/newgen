package com.newgen.am.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.bson.conversions.Bson;
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
import com.newgen.am.common.RequestParamsParser;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.BrokerDTO;
import com.newgen.am.dto.CollaboratorDTO;
import com.newgen.am.dto.DepartmentDTO;
import com.newgen.am.dto.InvestorDTO;
import com.newgen.am.dto.ListElementDTO;
import com.newgen.am.dto.MemberDTO;
import com.newgen.am.dto.UserBaseInfo;
import com.newgen.am.dto.UserDTO;
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

	@Autowired
	private RequestParamsParser rqParamsParser;

	public List<ListElementDTO> getDeptList(long refId) {
		String methodName = "getDeptList";
		List<ListElementDTO> deptList = new ArrayList<ListElementDTO>();

		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");

			Document query = new Document();

			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("code", 1.0);
			projection.append("name", 1.0);

			MongoCursor<Document> cur = collection.find(query).projection(projection).iterator();
			while (cur.hasNext()) {
				ListElementDTO elemDto = mongoTemplate.getConverter().read(ListElementDTO.class, cur.next());
				if (elemDto != null)
					deptList.add(elemDto);
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return deptList;
	}

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
					if (elemDto != null)
						memberList.add(elemDto);
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
					if (elemDto != null)
						brokerList.add(elemDto);
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
					if (elemDto != null)
						collaboratorList.add(elemDto);
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
					if (elemDto != null)
						investorList.add(elemDto);
				}
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return investorList;
	}

	public List<UserBaseInfo> getAdminUserList(HttpServletRequest request, long refId) {
		String methodName = "getAdminUserList";
		List<UserBaseInfo> userList = new ArrayList<UserBaseInfo>();

		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");

			List<? extends Bson> pipeline = Arrays.asList(
					new Document().append("$unwind", new Document().append("path", "$users")),
					new Document().append("$match", searchCriteria.getQuery()),
					new Document().append("$project", new Document().append("_id", 0.0).append("users", 1.0)),
					new Document().append("$replaceRoot", new Document().append("newRoot", "$users")));

//			System.out.println("pipeline: " + Utility.getGson().toJson(pipeline));
			MongoCursor<Document> cur = collection.aggregate(pipeline).cursor();
			while (cur.hasNext()) {
				UserDTO userDTO = mongoTemplate.getConverter().read(UserDTO.class, cur.next());
				userList.add(modelMapper.map(userDTO, UserBaseInfo.class));
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return userList;
	}

	public List<UserBaseInfo> getMemberUserList(HttpServletRequest request, long refId) {
		String methodName = "getMemberUserList";
		List<UserBaseInfo> userList = new ArrayList<UserBaseInfo>();

		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");

			List<? extends Bson> pipeline = Arrays.asList(
					new Document().append("$unwind", new Document().append("path", "$users")),
					new Document().append("$match", searchCriteria.getQuery()),
					new Document().append("$project", new Document().append("_id", 0.0).append("users", 1.0)),
					new Document().append("$replaceRoot", new Document().append("newRoot", "$users")));

			MongoCursor<Document> cur = collection.aggregate(pipeline).cursor();
			while (cur.hasNext()) {
				UserDTO userDTO = mongoTemplate.getConverter().read(UserDTO.class, cur.next());
				userList.add(modelMapper.map(userDTO, UserBaseInfo.class));
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return userList;
	}

	public List<UserBaseInfo> getBrokerUserList(HttpServletRequest request, long refId) {
		String methodName = "getBrokerUserList";
		List<UserBaseInfo> userList = new ArrayList<UserBaseInfo>();

		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("brokers");

			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("user.username", 1.0);
			projection.append("user.email", 1.0);
			projection.append("user.phoneNumber", 1.0);

			MongoCursor<Document> cur = collection.find(searchCriteria.getQuery()).projection(projection).iterator();
			while (cur.hasNext()) {
				BrokerDTO brokerDto = mongoTemplate.getConverter().read(BrokerDTO.class, cur.next());
				if (brokerDto != null && brokerDto.getUser() != null) {
					UserBaseInfo user = modelMapper.map(brokerDto.getUser(), UserBaseInfo.class);
					userList.add(user);
				}
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return userList;
	}

	public List<UserBaseInfo> getCollaboratorUserList(HttpServletRequest request, long refId) {
		String methodName = "getCollaboratorList";
		List<UserBaseInfo> userList = new ArrayList<UserBaseInfo>();

		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("collaborators");

			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("user.username", 1.0);
			projection.append("user.email", 1.0);
			projection.append("user.phoneNumber", 1.0);

			MongoCursor<Document> cur = collection.find(searchCriteria.getQuery()).projection(projection).iterator();
			while (cur.hasNext()) {
				CollaboratorDTO collaboratorDto = mongoTemplate.getConverter().read(CollaboratorDTO.class, cur.next());
				if (collaboratorDto != null && collaboratorDto.getUser() != null) {
					UserBaseInfo user = modelMapper.map(collaboratorDto.getUser(), UserBaseInfo.class);
					userList.add(user);
				}
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return userList;
	}

	public List<UserBaseInfo> getInvestorUserList(HttpServletRequest request, long refId) {
		String methodName = "getInvestorUserList";
		List<UserBaseInfo> userList = new ArrayList<UserBaseInfo>();

		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");

			List<? extends Bson> pipeline = Arrays.asList(
					new Document().append("$unwind", new Document().append("path", "$users")),
					new Document().append("$match", searchCriteria.getQuery()),
					new Document().append("$project", new Document().append("_id", 0.0).append("users", 1.0)),
					new Document().append("$replaceRoot", new Document().append("newRoot", "$users")));

			MongoCursor<Document> cur = collection.aggregate(pipeline).cursor();
			while (cur.hasNext()) {
				UserDTO userDTO = mongoTemplate.getConverter().read(UserDTO.class, cur.next());
				userList.add(modelMapper.map(userDTO, UserBaseInfo.class));
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return userList;
	}

	public List<UserBaseInfo> getUsersByDeptCode(String deptCode, long refId) {
		String methodName = "getUsersByDeptCode";
		List<UserBaseInfo> userList = new ArrayList<UserBaseInfo>();

		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");

			Document query = new Document();
			query.append("code", deptCode);

			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("users.username", 1.0);
			projection.append("users.fullName", 1.0);
			projection.append("users.email", 1.0);
			projection.append("users.phoneNumber", 1.0);

			Document result = collection.find(query).projection(projection).first();
			DepartmentDTO deptDto = mongoTemplate.getConverter().read(DepartmentDTO.class, result);
			if (deptDto != null && deptDto.getUsers() != null) {
				userList = modelMapper.map(deptDto.getUsers(), new TypeToken<List<UserBaseInfo>>() {
				}.getType());
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return userList;
	}

	public List<UserBaseInfo> getUsersByMemberCode(String memberCode, long refId) {
		String methodName = "getUsersByMemberCode";
		List<UserBaseInfo> userList = new ArrayList<UserBaseInfo>();

		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");

			Document query = new Document();
			query.append("code", memberCode);

			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("users.username", 1.0);
			projection.append("users.fullName", 1.0);
			projection.append("users.email", 1.0);
			projection.append("users.phoneNumber", 1.0);

			Document result = collection.find(query).projection(projection).first();
			MemberDTO memberDto = mongoTemplate.getConverter().read(MemberDTO.class, result);
			if (memberDto != null && memberDto.getUsers() != null) {
				userList = modelMapper.map(memberDto.getUsers(), new TypeToken<List<UserBaseInfo>>() {
				}.getType());
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return userList;
	}

	public List<UserBaseInfo> getUsersByBrokerCode(String brokerCode, long refId) {
		String methodName = "getUsersByBrokerCode";
		List<UserBaseInfo> userList = new ArrayList<UserBaseInfo>();

		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("brokers");

			Document query = new Document();
			query.append("code", brokerCode);

			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("user.username", 1.0);
			projection.append("user.fullName", 1.0);
			projection.append("user.email", 1.0);
			projection.append("user.phoneNumber", 1.0);

			Document result = collection.find(query).projection(projection).first();
			BrokerDTO brokerDto = mongoTemplate.getConverter().read(BrokerDTO.class, result);
			if (brokerDto != null && brokerDto.getUser() != null) {
				userList.add(modelMapper.map(brokerDto.getUser(), UserBaseInfo.class));
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return userList;
	}

	public List<UserBaseInfo> getUsersByCollaboratorCode(String collaboratorCode, long refId) {
		String methodName = "getUsersByCollaboratorCode";
		List<UserBaseInfo> userList = new ArrayList<UserBaseInfo>();

		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("collaborators");

			Document query = new Document();
			query.append("code", collaboratorCode);

			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("user.username", 1.0);
			projection.append("user.fullName", 1.0);
			projection.append("user.email", 1.0);
			projection.append("user.phoneNumber", 1.0);

			Document result = collection.find(query).projection(projection).first();
			CollaboratorDTO collaboratorDto = mongoTemplate.getConverter().read(CollaboratorDTO.class, result);
			if (collaboratorDto != null && collaboratorDto.getUser() != null) {
				userList.add(modelMapper.map(collaboratorDto.getUser(), UserBaseInfo.class));
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return userList;
	}

	public List<UserBaseInfo> getUsersByInvestorCode(String investorCode, long refId) {
		String methodName = "getUsersByInvestorCode";
		List<UserBaseInfo> userList = new ArrayList<UserBaseInfo>();

		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");

			Document query = new Document();
			query.append("investorCode", investorCode);

			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("users.username", 1.0);
			projection.append("users.fullName", 1.0);
			projection.append("users.email", 1.0);
			projection.append("users.phoneNumber", 1.0);

			Document result = collection.find(query).projection(projection).first();
			InvestorDTO investorDto = mongoTemplate.getConverter().read(InvestorDTO.class, result);
			if (investorDto != null && investorDto.getUsers() != null) {
				userList = modelMapper.map(investorDto.getUsers(), new TypeToken<List<UserBaseInfo>>() {
				}.getType());
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return userList;
	}
}
