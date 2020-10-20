/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.math3.util.Precision;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ApprovalConstant;
import com.newgen.am.common.ConfigLoader;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.FileUtility;
import com.newgen.am.common.LocalServiceConnection;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.RequestParamsParser;
import com.newgen.am.common.SystemFunctionCode;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.AccountStatusDTO;
import com.newgen.am.dto.AdminResponseObj;
import com.newgen.am.dto.ApprovalChangeGroupDTO;
import com.newgen.am.dto.ApprovalRiskParametersDTO;
import com.newgen.am.dto.ApprovalUpdateInvestorDTO;
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.CQGCMSCommodityDTO;
import com.newgen.am.dto.ChangeGroupDTO;
import com.newgen.am.dto.CommoditiesDTO;
import com.newgen.am.dto.DefaultSettingDTO;
import com.newgen.am.dto.EmailDTO;
import com.newgen.am.dto.ExchangeRateDTO;
import com.newgen.am.dto.ExchangeRateReponseDTO;
import com.newgen.am.dto.GeneralFeeDTO;
import com.newgen.am.dto.InvestorActivationDTO;
import com.newgen.am.dto.InvestorCSV;
import com.newgen.am.dto.InvestorDTO;
import com.newgen.am.dto.MarginInfoDTO;
import com.newgen.am.dto.MarginMultiplierDTO;
import com.newgen.am.dto.MarginRatioAlertDTO;
import com.newgen.am.dto.MarginTransCSV;
import com.newgen.am.dto.MarginTransactionDTO;
import com.newgen.am.dto.MemberCommoditiesDTO;
import com.newgen.am.dto.MemberDTO;
import com.newgen.am.dto.NotifyServiceDTO;
import com.newgen.am.dto.RiskParametersDTO;
import com.newgen.am.dto.UpdateInvestorDTO;
import com.newgen.am.dto.UserCSV;
import com.newgen.am.dto.UserDTO;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.Commodity;
import com.newgen.am.model.GeneralFee;
import com.newgen.am.model.Investor;
import com.newgen.am.model.InvestorActivationApproval;
import com.newgen.am.model.InvestorMarginInfo;
import com.newgen.am.model.InvestorMarginTransApproval;
import com.newgen.am.model.InvestorMarginTransaction;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.model.LoginInvestorUser;
import com.newgen.am.model.MarginRatioAlert;
import com.newgen.am.model.Member;
import com.newgen.am.model.NestedObjectInfo;
import com.newgen.am.model.PendingApproval;
import com.newgen.am.model.PendingData;
import com.newgen.am.model.SystemRole;
import com.newgen.am.repository.BrokerRepository;
import com.newgen.am.repository.CollaboratorRepository;
import com.newgen.am.repository.InvestorActivationApprovalRepository;
import com.newgen.am.repository.InvestorMarginInfoRepository;
import com.newgen.am.repository.InvestorMarginTransApprovalRepository;
import com.newgen.am.repository.InvestorMarginTransactionRepository;
import com.newgen.am.repository.InvestorRepository;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.LoginInvestorUserRepository;
import com.newgen.am.repository.MemberRepository;
import com.newgen.am.repository.PendingApprovalRepository;
import com.newgen.am.repository.SystemRoleRepository;

/**
 *
 * @author nhungtt
 */
@Service
public class InvestorService {

	private String className = "InvestorService";

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private InvestorRepository investorRepo;

	@Autowired
	private MemberRepository memberRepo;

	@Autowired
	private BrokerRepository brokerRepo;

	@Autowired
	private CollaboratorRepository collaboratorRepo;

	@Autowired
	private PendingApprovalRepository pendingApprovalRepo;

	@Autowired
	private InvestorMarginTransApprovalRepository invMarginTransApprovalRepo;

	@Autowired
	private InvestorMarginInfoRepository invMarginInfoRepo;

	@Autowired
	private InvestorMarginTransactionRepository invMarginTransRepo;

	@Autowired
	private InvestorActivationApprovalRepository invActivationApprovalRepo;

	@Autowired
	private ActivityLogService activityLogService;

	@Autowired
	private RequestParamsParser rqParamsParser;

	@Autowired
	private SystemRoleRepository sysRoleRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private RedisTemplate template;

	@Autowired
	private LoginInvestorUserRepository loginInvUserRepo;

	@Autowired
	private LoginAdminUserRepository loginAdmUserRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private InvestorMarginInfoService marginInfoService;

	@Autowired
	private CQGConnectorService cqgService;

	public AccountStatusDTO getInvestorAccount(HttpServletRequest request, long refId) {
		String methodName = "getInvestorAccount";
		AccountStatusDTO investorAccDto = new AccountStatusDTO();
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);

			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("investorCode", userInfo.getInvestorCode());

			BasicDBObject projection = new BasicDBObject();
			projection.append("investorCode", 1);
			projection.append("investorName", 1);
			projection.append("generalFee", 1);
			projection.append("otherFee", 1);
			projection.append("account", 1);
			Document invDoc = collection.find(searchQuery).projection(projection).first();
			Investor investor = mongoTemplate.getConverter().read(Investor.class, invDoc);

			if (investor != null) {
				// get info from redis
				String investorInfo = (String) redisTemplate.opsForValue().get(investor.getInvestorCode());
				JsonObject jobj = new Gson().fromJson(investorInfo, JsonObject.class);
				investorAccDto.setTransactionFee(jobj.get("transactionFee").getAsLong());
				investorAccDto.setInitialRequiredMargin(jobj.get("initialRequiredMargin").getAsLong());
				investorAccDto.setActualProfitVND(jobj.get("actualProfitVND").getAsLong());
				investorAccDto.setEstimatedProfitVND(jobj.get("estimatedProfitVND").getAsLong());

				// set infro from db
				investorAccDto.setInvestorName(investor.getInvestorName());
				investorAccDto.setInvestorCode(investor.getInvestorCode());
				investorAccDto.setSodBalance(100000000l);
				investorAccDto.setChangedAmount(50000000l);
				investorAccDto.setGeneralFee(0l);

				// caculate some fields
				calculateCurrentBalance(investorAccDto, 0l);
				calculateNetMargin(investorAccDto);
				calculateAvailableMargin(investorAccDto);
				calculateAdditionalMargin(investorAccDto);
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return investorAccDto;
	}

	private void calculateAvailableMargin(AccountStatusDTO investorAccDto) {
		investorAccDto.setAvailableMargin(Utility.getLong(investorAccDto.getNetMargin())
				- Utility.getLong(investorAccDto.getInitialRequiredMargin()));
	}

	private void calculateNetMargin(AccountStatusDTO investorAccDto) {
		investorAccDto.setNetMargin(Utility.getLong(investorAccDto.getCurrentBalance())
				+ Utility.getLong(investorAccDto.getEstimatedProfitVND()));
	}

	private void calculateCurrentBalance(AccountStatusDTO investorAccDto, Long otherFee) {
		long currentBalance = Utility.getLong(investorAccDto.getSodBalance())
				+ Utility.getLong(investorAccDto.getChangedAmount())
				- Utility.getLong(investorAccDto.getTransactionFee()) - Utility.getLong(otherFee)
				- Utility.getLong(investorAccDto.getGeneralFee())
				+ Utility.getLong(investorAccDto.getActualProfitVND());
		investorAccDto.setCurrentBalance(Utility.getLong(currentBalance));
	}

	private void calculateAdditionalMargin(AccountStatusDTO investorAccDto) {
		if (investorAccDto.getAvailableMargin() >= 0) {
			investorAccDto.setAdditionalMargin(0);
		} else {
			investorAccDto.setAdditionalMargin(Math.abs(Utility.getLong(investorAccDto.getAvailableMargin())));
		}
	}

	public List<String> getInvestorCodesByUser(long refId) {
		String methodName = "getInvestorCodesByUser";
		List<String> investorCodes = new ArrayList<String>();
		try {
			LoginAdminUser user = loginAdmUserRepo.findByUsername(Utility.getCurrentUsername());
			Document query = new Document();
			if (user == null) {
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			if (Utility.isNotNull(user.getCollaboratorCode())) {
				query.append("collaboratorCode", user.getCollaboratorCode());
			} else if (Utility.isNotNull(user.getBrokerCode())) {
				query.append("brokerCode", user.getBrokerCode());
			} else if (Utility.isNotNull(user.getMemberCode())) {
				query.append("memberCode", user.getMemberCode());
			}

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");

			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("investorCode", 1.0);

			MongoCursor<Document> cur = collection.find(query).projection(projection).iterator();

			while (cur.hasNext()) {
				InvestorDTO investor = mongoTemplate.getConverter().read(InvestorDTO.class, cur.next());
				if (investor != null)
					investorCodes.add(investor.getInvestorCode());
			}
			return investorCodes;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Document getQueryDocument(RequestParamsParser.SearchCriteria searchCriteria, UserInfoDTO userInfo) {
		Document query = new Document();
		// get redis user info
		if (Utility.isDeptUser(userInfo)) {
			// do nothing
			query = searchCriteria.getQuery();
		} else if (Utility.isMemberUser(userInfo)) {
			// match code=memberCode
			query = searchCriteria.getQuery().append("memberCode", userInfo.getMemberCode());
		} else if (Utility.isBrokerUser(userInfo)) {
			query = searchCriteria.getQuery().append("memberCode", userInfo.getMemberCode()).append("brokerCode",
					userInfo.getBrokerCode());
		} else if (Utility.isCollaboratorUser(userInfo)) {
			query = searchCriteria.getQuery().append("memberCode", userInfo.getMemberCode())
					.append("brokerCode", userInfo.getBrokerCode())
					.append("collaboratorCode", userInfo.getCollaboratorCode());
		} else if (Utility.isInvestorUser(userInfo)) {
			query = searchCriteria.getQuery().append("memberCode", userInfo.getMemberCode())
					.append("brokerCode", userInfo.getBrokerCode())
					.append("collaboratorCode", userInfo.getCollaboratorCode())
					.append("investorCode", userInfo.getInvestorCode());
		} else {
			throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
		}
		return query;
	}

	public BasePagination<InvestorDTO> list(HttpServletRequest request, long refId) {
		String methodName = "list";
		BasePagination<InvestorDTO> pagination = null;
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);

			List<? extends Bson> pipeline = Arrays.asList(
					new Document().append("$match", getQueryDocument(searchCriteria, userInfo)),
					new Document().append("$sort", searchCriteria.getSort()),
					new Document().append("$project",
							new Document().append("_id", new Document().append("$toString", "$_id"))
									.append("memberCode", 1.0).append("memberName", 1.0).append("brokerCode", 1.0)
									.append("brokerName", 1.0).append("collaboratorCode", 1.0)
									.append("collaboratorName", 1.0).append("investorCode", 1.0)
									.append("investorName", 1.0).append("status", 1.0).append("note", 1.0)
									.append("createdDate", 1.0)),
					new Document().append("$facet",
							new Document().append("stage1", Arrays.asList(new Document().append("$count", "total")))
									.append("stage2",
											Arrays.asList(new Document().append("$skip", searchCriteria.getSkip()),
													new Document().append("$limit", searchCriteria.getLimit())))),
					new Document().append("$unwind", new Document().append("path", "$stage1")), new Document().append(
							"$project", new Document().append("count", "$stage1.total").append("data", "$stage2")));

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");
			Document resultDoc = collection.aggregate(pipeline).first();
			pagination = mongoTemplate.getConverter().read(BasePagination.class, resultDoc);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return pagination;
	}

	public List<InvestorCSV> listCsv(HttpServletRequest request, long refId) {
		String methodName = "listCsv";
		List<InvestorCSV> investorList = new ArrayList<>();
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);

			List<? extends Bson> pipeline = Arrays.asList(
					new Document().append("$match", getQueryDocument(searchCriteria, userInfo)),
					new Document().append("$sort", searchCriteria.getSort()),
					new Document().append("$project", new Document()
							.append("_id", new Document().append("$toString", "$_id")).append("memberCode", 1.0)
							.append("memberName", 1.0).append("brokerCode", 1.0).append("brokerName", 1.0)
							.append("collaboratorCode", 1.0).append("collaboratorName", 1.0).append("investorCode", 1.0)
							.append("investorName", 1.0).append("status", 1.0).append("note", 1.0).append("createdDate",
									new Document().append("$dateToString",
											new Document().append("format", "%d/%m/%Y %H:%M:%S").append("date",
													new Document().append("$toDate", "$createdDate"))))));
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");
			MongoCursor<Document> cur = collection.aggregate(pipeline).iterator();
			while (cur.hasNext()) {
				InvestorCSV investorCsv = mongoTemplate.getConverter().read(InvestorCSV.class, cur.next());
				if (investorCsv != null)
					investorList.add(investorCsv);
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return investorList;
	}

	public void createInvestor(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "createInvestor";
		try {
			InvestorDTO investorDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(),
					InvestorDTO.class);

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_INVESTOR,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_INVESTOR_DESC, investorDto.getInvestorCode(),
					pendingApproval.getId());

			Document company = null;
			Document individual = null;
			Document contact = null;

			if (Utility.isInvestorCompany(investorDto.getType())) {
				// create Delegate document
				Document delegate = new Document();
				delegate.append("fullName", investorDto.getCompany().getDelegate().getFullName());
				delegate.append("birthDay", investorDto.getCompany().getDelegate().getBirthDay());
				delegate.append("identityCard", investorDto.getCompany().getDelegate().getIdentityCard());
				delegate.append("idCreatedDate", investorDto.getCompany().getDelegate().getIdCreatedDate());
				delegate.append("idCreatedLocation", investorDto.getCompany().getDelegate().getIdCreatedLocation());
				delegate.append("email", investorDto.getCompany().getDelegate().getEmail());
				delegate.append("phoneNumber", investorDto.getCompany().getDelegate().getPhoneNumber());
				delegate.append("address", investorDto.getCompany().getDelegate().getAddress());
				delegate.append("scannedFrontIdCard", investorDto.getCompany().getDelegate().getScannedFrontIdCard());
				delegate.append("scannedBackIdCard", investorDto.getCompany().getDelegate().getScannedBackIdCard());
				delegate.append("scannedSignature", investorDto.getCompany().getDelegate().getScannedSignature());

				// create Company document
				company = new Document();
				company.append("name", investorDto.getCompany().getName());
				company.append("taxCode", investorDto.getCompany().getTaxCode());
				company.append("address", investorDto.getCompany().getAddress());
				company.append("phoneNumber", investorDto.getCompany().getPhoneNumber());
				company.append("fax", investorDto.getCompany().getFax());
				company.append("email", investorDto.getCompany().getEmail());
				company.append("delegate", delegate);

				// create Contact document
				contact = new Document();
				contact.append("fullName", investorDto.getCompany().getDelegate().getFullName());
				contact.append("phoneNumber", investorDto.getCompany().getDelegate().getPhoneNumber());
				contact.append("email", investorDto.getCompany().getDelegate().getEmail());
			} else if (Utility.isInvestorIndividual(investorDto.getType())) {
				individual = new Document();
				individual.append("fullName", investorDto.getIndividual().getFullName());
				individual.append("birthDay", investorDto.getIndividual().getBirthDay());
				individual.append("identityCard", investorDto.getIndividual().getIdentityCard());
				individual.append("idCreatedDate", investorDto.getIndividual().getIdCreatedDate());
				individual.append("idCreatedLocation", investorDto.getIndividual().getIdCreatedLocation());
				individual.append("email", investorDto.getIndividual().getEmail());
				individual.append("phoneNumber", investorDto.getIndividual().getPhoneNumber());
				individual.append("address", investorDto.getIndividual().getAddress());
				individual.append("scannedFrontIdCard", investorDto.getIndividual().getScannedFrontIdCard());
				individual.append("scannedBackIdCard", investorDto.getIndividual().getScannedBackIdCard());
				individual.append("scannedSignature", investorDto.getIndividual().getScannedSignature());

				contact = new Document();
				contact.append("fullName", investorDto.getIndividual().getFullName());
				contact.append("phoneNumber", investorDto.getIndividual().getPhoneNumber());
				contact.append("email", investorDto.getIndividual().getEmail());
			}

			// create default investor role
			SystemRole defaultInvestorRole = sysRoleRepository.findByName(Constant.INVESTOR_DEFAULT_ROLE);
			if (Utility.isNull(defaultInvestorRole)) {
				throw new CustomException(ErrorMessage.DEFAULT_ROLE_DOESNT_EXIST, HttpStatus.OK);
			}

			Document investorRole = new Document();
			investorRole.append("name", defaultInvestorRole.getName());
			investorRole.append("description", defaultInvestorRole.getDescription());

			Document newInvestor = new Document();
			newInvestor.append("createdUser", Utility.getCurrentUsername());
			newInvestor.append("createdDate", System.currentTimeMillis());
			newInvestor.append("_id", new ObjectId());
			newInvestor.append("investorCode", investorDto.getInvestorCode());
			newInvestor.append("investorName", investorDto.getInvestorName());
			newInvestor.append("status", Constant.STATUS_PENDING_ACTIVATE);
			newInvestor.append("note", investorDto.getNote());
			newInvestor.append("memberCode", investorDto.getMemberCode());
			newInvestor.append("memberName", investorDto.getMemberName());
			newInvestor.append("brokerCode", investorDto.getBrokerCode());
			newInvestor.append("brokerName", investorDto.getBrokerName());
			newInvestor.append("collaboratorCode", investorDto.getCollaboratorCode());
			newInvestor.append("collaboratorName", investorDto.getCollaboratorName());
			newInvestor.append("type", investorDto.getType());
			newInvestor.append("company", company);
			newInvestor.append("individual", individual);
			newInvestor.append("contact", contact);
			newInvestor.append("role", investorRole);

			MongoDatabase database = MongoDBConnection.getMongoDatabase();

			// applied all default setting from member
			MongoCollection<Document> memberCollection = database.getCollection("members");

			Document memberQuery = new Document();
			memberQuery.append("code", investorDto.getMemberCode());

			Document memberProjection = new Document();
			memberProjection.append("_id", 0.0);
			memberProjection.append("orderLimit", 1.0);
			memberProjection.append("marginRatioAlert", 1.0);
			memberProjection.append("marginMultiplier", 1.0);
			memberProjection.append("generalFees", 1.0);

			Document memberDoc = memberCollection.find(memberQuery).projection(memberProjection).first();
			MemberDTO memberDto = mongoTemplate.getConverter().read(MemberDTO.class, memberDoc);

			newInvestor.append("orderLimit", memberDto.getOrderLimit());
			newInvestor.append("marginRatioAlert", createMarginRatioAlertDoc(memberDto.getMarginRatioAlert()));
			newInvestor.append("marginMultiplier", memberDto.getMarginMultiplier());
			List<Document> generalFees = createGeneralFeesDoc(memberDto.getGeneralFees());
			if (generalFees != null) {
				newInvestor.append("generalFees", generalFees);
			}

			// insert new investor
			MongoCollection<Document> collection = database.getCollection("investors");
			collection.insertOne(newInvestor);

			// insert a new investor_activation_approval
			insertNewInvestorActivationApproval(userInfo, investorDto);

			// insert new investor's user
			createDefaultInvestorUser(request, investorDto, investorRole, refId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Document createMarginRatioAlertDoc(MarginRatioAlert marginRatioAlert) {
		if (marginRatioAlert != null) {
			Document marginRatioDoc = new Document();
			marginRatioDoc.append("warningRatio", marginRatioAlert.getWarningRatio());
			marginRatioDoc.append("cancelOrderRatio", marginRatioAlert.getCancelOrderRatio());
			marginRatioDoc.append("finalizationRatio", marginRatioAlert.getFinalizationRatio());
			return marginRatioDoc;
		}
		return null;
	}

	private List<Document> createGeneralFeesDoc(List<GeneralFee> generalFees) {
		if (generalFees != null && generalFees.size() > 0) {
			List<Document> generalFeesDoc = new ArrayList<Document>();

			for (GeneralFee fee : generalFees) {
				Document feeDoc = new Document();
				feeDoc.append("name", fee.getName());
				feeDoc.append("processMethod", fee.getProcessMethod());
				feeDoc.append("feeAmount", fee.getFeeAmount());
				feeDoc.append("appliedDate", fee.getAppliedDate());

				generalFeesDoc.add(feeDoc);
			}

			return generalFeesDoc;
		}

		return null;
	}

	private void insertNewInvestorActivationApproval(UserInfoDTO userInfo, InvestorDTO investorDto) {
		InvestorActivationDTO invActivationDto = new InvestorActivationDTO();
		invActivationDto.setInvestorCode(investorDto.getInvestorCode());
		invActivationDto.setInvestorName(investorDto.getInvestorName());
		if (investorDto.getCompany() != null && investorDto.getCompany().getDelegate() != null) {
			invActivationDto.setPhoneNumber(investorDto.getCompany().getDelegate().getPhoneNumber());
			invActivationDto.setEmail(investorDto.getCompany().getDelegate().getEmail());
			invActivationDto.setIdentityCard(investorDto.getCompany().getDelegate().getIdentityCard());
			invActivationDto.setNote(investorDto.getNote());
		}

		if (investorDto.getIndividual() != null) {
			invActivationDto.setPhoneNumber(investorDto.getIndividual().getPhoneNumber());
			invActivationDto.setEmail(investorDto.getIndividual().getEmail());
			invActivationDto.setIdentityCard(investorDto.getIndividual().getIdentityCard());
			invActivationDto.setNote(investorDto.getNote());
		}

		NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
		nestedObjInfo.setDeptCode(userInfo.getDeptCode());

		PendingData pendingData = new PendingData();
		pendingData.setServiceFunctionName(ApprovalConstant.INVESTOR_ACTIVATE);
		pendingData.setCollectionName("investors");
		pendingData.setAction(Constant.APPROVAL_ACTION_ACTIVATE_INVESTOR);
		pendingData.setQueryField("investorCode");
		pendingData.setQueryValue(investorDto.getInvestorCode());
		pendingData.setPendingValue(new Gson().toJson(invActivationDto));

		InvestorActivationApproval invActivationApproval = new InvestorActivationApproval();
		invActivationApproval.setInvestorCode(investorDto.getInvestorCode());
		invActivationApproval.setApiUrl(ApprovalConstant.APPROVAL_ACCOUNT_ACTIVATION_URL);
		invActivationApproval.setFunctionCode(SystemFunctionCode.INVESTOR_ACTIVATE_CODE);
		invActivationApproval.setFunctionName(SystemFunctionCode.INVESTOR_ACTIVATE_NAME);
		invActivationApproval.setDescription(SystemFunctionCode
				.getApprovalDescription(invActivationApproval.getFunctionName(), investorDto.getInvestorCode()));
		invActivationApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
		invActivationApproval.setNestedObjInfo(nestedObjInfo);
		invActivationApproval.setPendingData(pendingData);
		invActivationApprovalRepo.save(invActivationApproval);
	}

	public void createInvestorPA(HttpServletRequest request, InvestorDTO investorDto, long refId) {
		String methodName = "createInvestorPA";
		boolean existedInvestor = false;
		try {
			existedInvestor = investorRepo.existsInvestorByInvestorCode(investorDto.getInvestorCode());
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!existedInvestor) {
			try {
				if (Utility.isInvestorCompany(investorDto.getType())) {
					if (Utility.isNull(investorDto.getCompany())) {
						throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
					}
				} else if (Utility.isInvestorIndividual(investorDto.getType())) {
					if (Utility.isNull(investorDto.getIndividual())) {
						throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
					}
				} else {
					throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
				}

				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertInvestorCreatePA(userInfo, investorDto, refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_INVESTOR,
						ActivityLogService.ACTIVITY_CREATE_INVESTOR_DESC, investorDto.getInvestorCode(), approvalId);
			} catch (CustomException e) {
				throw e;
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "This investor code already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		}
	}

	private String insertInvestorCreatePA(UserInfoDTO userInfo, InvestorDTO investorDto, long refId) {
		String methodName = "insertInvestorCreatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());
			nestedObjInfo.setMemberCode(userInfo.getMemberCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.INVESTOR_CREATE);
			pendingData.setCollectionName("investors");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setPendingValue(new Gson().toJson(investorDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_INVESTOR_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.INVESTOR_CREATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(),
					investorDto.getInvestorCode()));
			pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
			pendingApproval.setNestedObjInfo(nestedObjInfo);
			pendingApproval.setPendingData(pendingData);
			approvalId = pendingApprovalRepo.save(pendingApproval).getId();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return approvalId;
	}

	private void createDefaultInvestorUser(HttpServletRequest request, InvestorDTO investorDto, Document investorRole,
			long refId) {
		String methodName = "createInvestorUser";
		boolean existedUser = false;
		String username = investorDto.getInvestorCode();
		try {
			existedUser = loginInvUserRepo.existsByUsername(username);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!existedUser) {
			try {
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("investors");

				String fullName = "";
				String email = "";
				String phoneNumber = "";
				if (Utility.isNotNull(investorDto.getCompany())
						&& Utility.isNotNull(investorDto.getCompany().getDelegate())) {
					fullName = investorDto.getCompany().getDelegate().getFullName();
					email = investorDto.getCompany().getDelegate().getEmail();
					phoneNumber = investorDto.getCompany().getDelegate().getPhoneNumber();
				} else if (Utility.isNotNull(investorDto.getIndividual())) {
					fullName = investorDto.getIndividual().getFullName();
					email = investorDto.getIndividual().getEmail();
					phoneNumber = investorDto.getIndividual().getPhoneNumber();
				}

				Document investorUser = new Document();
				investorUser.append("_id", new ObjectId());
				investorUser.append("username", username);
				investorUser.append("fullName", fullName);
				investorUser.append("email", email);
				investorUser.append("phoneNumber", phoneNumber);
				investorUser.append("status", Constant.STATUS_PENDING_ACTIVATE);
				investorUser.append("note", investorDto.getNote());
				investorUser.append("isPasswordExpiryCheck", false);
				investorUser.append("passwordExpiryDays", 0);
				investorUser.append("expiryAlertDays", 0);
				investorUser.append("createdUser", Utility.getCurrentUsername());
				investorUser.append("createdDate", System.currentTimeMillis());
				investorUser.append("role", investorRole);

				BasicDBObject query = new BasicDBObject();
				query.append("investorCode", investorDto.getInvestorCode());

				collection.updateOne(query, Updates.addToSet("users", investorUser));

				// insert loginAdminUser
				UserInfoDTO investorUserDto = new UserInfoDTO();
				investorUserDto.setMemberCode(investorDto.getMemberCode());
				investorUserDto.setMemberName(investorDto.getMemberName());
				investorUserDto.setBrokerCode(investorDto.getBrokerCode());
				investorUserDto.setBrokerName(investorDto.getBrokerName());
				investorUserDto.setCollaboratorCode(investorDto.getCollaboratorCode());
				investorUserDto.setCollaboratorName(investorDto.getCollaboratorName());
				investorUserDto.setInvestorCode(investorDto.getInvestorCode());
				investorUserDto.setInvestorName(investorDto.getInvestorName());
				investorUserDto.setUsername(username);
				investorUserDto.setFullName(fullName);
				investorUserDto.setEmail(email);
				investorUserDto.setPhoneNumber(phoneNumber);

				String password = Utility.generateRandomPassword();
				String pin = Utility.generateRandomPin();
				createDefaultLoginInvestorUser(investorDto.getInvestorCode(), investorUserDto, password, pin, refId);

				// send email
				sendCreateNewUserEmail(email, username, password, pin, refId);
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "This username already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		}
	}

	private LoginInvestorUser createDefaultLoginInvestorUser(String investorCode, UserInfoDTO investorUserDto,
			String password, String pin, long refId) {
		String methodName = "createLoginInvestorUser";
		try {
			LoginInvestorUser loginInvUser = modelMapper.map(investorUserDto, LoginInvestorUser.class);
			loginInvUser.setPassword(passwordEncoder.encode(password));
			loginInvUser.setPin(passwordEncoder.encode(pin));
			loginInvUser.setStatus(Constant.STATUS_PENDING_ACTIVATE);
			loginInvUser.setCreatedUser(Utility.getCurrentUsername());
			loginInvUser.setCreatedDate(System.currentTimeMillis());
			LoginInvestorUser newLoginInvUser = loginInvUserRepo.save(loginInvUser);
			return newLoginInvUser;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private LoginInvestorUser createLoginInvestorUser(InvestorDTO investorDto, UserDTO investorUserDto, String password,
			String pin, long refId) {
		String methodName = "createLoginInvestorUser";
		try {
			LoginInvestorUser loginInvUser = modelMapper.map(investorUserDto, LoginInvestorUser.class);
			loginInvUser.setMemberCode(investorDto.getMemberCode());
			loginInvUser.setMemberName(investorDto.getMemberName());
			loginInvUser.setBrokerCode(investorDto.getBrokerCode());
			loginInvUser.setBrokerName(investorDto.getBrokerName());
			loginInvUser.setCollaboratorCode(investorDto.getCollaboratorCode());
			loginInvUser.setCollaboratorName(investorDto.getCollaboratorName());
			loginInvUser.setInvestorCode(investorDto.getInvestorCode());
			loginInvUser.setInvestorName(investorDto.getInvestorName());
			loginInvUser.setPassword(passwordEncoder.encode(password));
			loginInvUser.setPin(passwordEncoder.encode(pin));
			loginInvUser.setStatus(Constant.STATUS_ACTIVE);
			loginInvUser.setCreatedUser(Utility.getCurrentUsername());
			loginInvUser.setCreatedDate(System.currentTimeMillis());
			LoginInvestorUser newLoginInvUser = loginInvUserRepo.save(loginInvUser);
			return newLoginInvUser;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void sendCreateNewUserEmail(String toEmail, String username, String password, String pin, long refId) {
		String methodName = "sendCreateNewUserEmail";
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			EmailDTO email = new EmailDTO();
			email.setSettingType(Constant.SERVICE_NOTIFICATION_SETTING_TYPE_CREATE_USER);
			email.setSendingObject(Constant.SERVICE_NOTIFICATION_SENDING_OBJ);
			email.setTo(toEmail);
			email.setSubject(FileUtility.CREATE_NEW_USER_EMAIL_SUBJECT);

			String emailBody = String.format(
					FileUtility.loadFileContent(
							ConfigLoader.getMainConfig().getString(FileUtility.CREATE_NEW_USER_EMAIL_FILE), refId),
					username, password, pin);
			email.setBodyStr(emailBody);
			String emailJson = new Gson().toJson(email);
			AMLogger.logMessage(className, methodName, refId, "Email: " + emailJson);
			serviceCon.sendPostRequest(serviceCon.getEmailNotificationServiceURL(), emailJson, null);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void updateInvestor(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "updateInvestor";
		try {
			String investorCode = pendingApproval.getPendingData().getQueryValue();
			UpdateInvestorDTO investorDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(),
					UpdateInvestorDTO.class);

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_UPDATE_INVESTOR,
					ActivityLogService.ACTIVITY_APPROVAL_UPDATE_INVESTOR_DESC, investorCode, pendingApproval.getId());

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> invCollection = database.getCollection("investors");

			BasicDBObject updateInvestor = new BasicDBObject();
			boolean isUserUpdated = false;
			boolean isStatusUpdated = false;

			if (Utility.isNotNull(investorDto.getInvestorName()))
				updateInvestor.append("investorName", investorDto.getInvestorName());
			if (Utility.isNotNull(investorDto.getStatus())) {
				isStatusUpdated = true;
				updateInvestor.append("status", investorDto.getStatus().toUpperCase());
			}
			if (Utility.isNotNull(investorDto.getNote())) {
				updateInvestor.append("note", investorDto.getNote());
				updateInvestor.append("users.$.note", investorDto.getNote());
				isUserUpdated = true;
			}

			if (Utility.isNotNull(investorDto.getCompany())) {
				if (Utility.isNotNull(investorDto.getCompany().getName()))
					updateInvestor.append("company.name", investorDto.getCompany().getName());
				if (Utility.isNotNull(investorDto.getCompany().getTaxCode()))
					updateInvestor.append("company.taxCode", investorDto.getCompany().getTaxCode());
				if (Utility.isNotNull(investorDto.getCompany().getAddress()))
					updateInvestor.append("company.address", investorDto.getCompany().getAddress());
				if (Utility.isNotNull(investorDto.getCompany().getPhoneNumber()))
					updateInvestor.append("company.phoneNumber", investorDto.getCompany().getPhoneNumber());
				if (Utility.isNotNull(investorDto.getCompany().getFax()))
					updateInvestor.append("company.fax", investorDto.getCompany().getFax());
				if (Utility.isNotNull(investorDto.getCompany().getEmail()))
					updateInvestor.append("company.email", investorDto.getCompany().getEmail());

				if (Utility.isNotNull(investorDto.getCompany().getDelegate())) {
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getFullName())) {
						updateInvestor.append("company.delegate.fullName",
								investorDto.getCompany().getDelegate().getFullName());
						updateInvestor.append("users.$.fullName", investorDto.getCompany().getDelegate().getFullName());
						updateInvestor.append("contact.fullName", investorDto.getCompany().getDelegate().getFullName());
						isUserUpdated = true;
					}
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getBirthDay()))
						updateInvestor.append("company.delegate.birthDay",
								investorDto.getCompany().getDelegate().getBirthDay());
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getIdentityCard()))
						updateInvestor.append("company.delegate.identityCard",
								investorDto.getCompany().getDelegate().getIdentityCard());
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getIdCreatedDate()))
						updateInvestor.append("company.delegate.idCreatedDate",
								investorDto.getCompany().getDelegate().getIdCreatedDate());
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getIdCreatedLocation()))
						updateInvestor.append("company.delegate.idCreatedLocation",
								investorDto.getCompany().getDelegate().getIdCreatedLocation());
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getEmail())) {
						updateInvestor.append("company.delegate.email",
								investorDto.getCompany().getDelegate().getEmail());
						updateInvestor.append("users.$.email", investorDto.getCompany().getDelegate().getEmail());
						updateInvestor.append("contact.email", investorDto.getCompany().getDelegate().getEmail());
						isUserUpdated = true;
					}
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getPhoneNumber())) {
						updateInvestor.append("company.delegate.phoneNumber",
								investorDto.getCompany().getDelegate().getPhoneNumber());
						updateInvestor.append("users.$.phoneNumber",
								investorDto.getCompany().getDelegate().getPhoneNumber());
						updateInvestor.append("contact.phoneNumber",
								investorDto.getCompany().getDelegate().getPhoneNumber());
						isUserUpdated = true;
					}
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getAddress()))
						updateInvestor.append("company.delegate.address",
								investorDto.getCompany().getDelegate().getAddress());
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getScannedFrontIdCard()))
						updateInvestor.append("company.delegate.scannedFrontIdCard",
								investorDto.getCompany().getDelegate().getScannedFrontIdCard());
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getScannedBackIdCard()))
						updateInvestor.append("company.delegate.scannedBackIdCard",
								investorDto.getCompany().getDelegate().getScannedBackIdCard());
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getScannedSignature()))
						updateInvestor.append("company.delegate.scannedSignature",
								investorDto.getCompany().getDelegate().getScannedSignature());
				}
			}

			if (Utility.isNotNull(investorDto.getIndividual())) {
				if (Utility.isNotNull(investorDto.getIndividual().getFullName())) {
					updateInvestor.append("individual.fullName", investorDto.getIndividual().getFullName());
					updateInvestor.append("users.$.fullName", investorDto.getIndividual().getFullName());
					updateInvestor.append("contact.fullName", investorDto.getIndividual().getFullName());
					isUserUpdated = true;
				}
				if (Utility.isNotNull(investorDto.getIndividual().getBirthDay()))
					updateInvestor.append("individual.birthDay", investorDto.getIndividual().getBirthDay());
				if (Utility.isNotNull(investorDto.getIndividual().getIdentityCard()))
					updateInvestor.append("individual.identityCard", investorDto.getIndividual().getIdentityCard());
				if (Utility.isNotNull(investorDto.getIndividual().getIdCreatedDate()))
					updateInvestor.append("individual.idCreatedDate", investorDto.getIndividual().getIdCreatedDate());
				if (Utility.isNotNull(investorDto.getIndividual().getIdCreatedLocation()))
					updateInvestor.append("individual.idCreatedLocation",
							investorDto.getIndividual().getIdCreatedLocation());
				if (Utility.isNotNull(investorDto.getIndividual().getEmail())) {
					updateInvestor.append("individual.email", investorDto.getIndividual().getEmail());
					updateInvestor.append("users.$.email", investorDto.getIndividual().getEmail());
					updateInvestor.append("contact.email", investorDto.getIndividual().getEmail());
					isUserUpdated = true;
				}
				if (Utility.isNotNull(investorDto.getIndividual().getPhoneNumber())) {
					updateInvestor.append("individual.phoneNumber", investorDto.getIndividual().getPhoneNumber());
					updateInvestor.append("users.$.phoneNumber", investorDto.getIndividual().getPhoneNumber());
					updateInvestor.append("contact.phoneNumber", investorDto.getIndividual().getPhoneNumber());
					isUserUpdated = true;
				}
				if (Utility.isNotNull(investorDto.getIndividual().getAddress()))
					updateInvestor.append("individual.address", investorDto.getIndividual().getAddress());
				if (Utility.isNotNull(investorDto.getIndividual().getScannedFrontIdCard()))
					updateInvestor.append("individual.scannedFrontIdCard",
							investorDto.getIndividual().getScannedFrontIdCard());
				if (Utility.isNotNull(investorDto.getIndividual().getScannedBackIdCard()))
					updateInvestor.append("individual.scannedBackIdCard",
							investorDto.getIndividual().getScannedBackIdCard());
				if (Utility.isNotNull(investorDto.getIndividual().getScannedSignature()))
					updateInvestor.append("individual.scannedSignature",
							investorDto.getIndividual().getScannedSignature());
			}

			if (updateInvestor.isEmpty()) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			} else {
				updateInvestor.append("lastModifiedUser", Utility.getCurrentUsername());
				updateInvestor.append("lastModifiedDate", System.currentTimeMillis());

				if (isUserUpdated) {
					updateInvestor.append("users.$.lastModifiedUser", Utility.getCurrentUsername());
					updateInvestor.append("users.$.lastModifiedDate", System.currentTimeMillis());
				}

				BasicDBObject query = new BasicDBObject();
				query.append("investorCode", investorCode);
				query.append("users.username", investorCode);

				BasicDBObject update = new BasicDBObject();
				update.append("$set", updateInvestor);

				invCollection.updateOne(query, update);

				if (isStatusUpdated) {
					// update status of all invetor users belong to this investor
					Document invQuery = new Document();
					invQuery.append("investorCode", investorCode);

					Document updateDoc = new Document();
					updateDoc.append("users.$[].status", investorDto.getStatus().toUpperCase());

					Document invUpdate = new Document();
					invUpdate.append("$set", updateDoc);

					invCollection.updateMany(invQuery, invUpdate);

					// update status of all login investor users belong to this investor
					MongoCollection<Document> loginAdmCollection = database.getCollection("login_investor_users");

					Document loginInvQuery = new Document();
					loginInvQuery.append("investorCode", investorCode);

					Document loginInvUpdateDoc = new Document();
					loginInvUpdateDoc.append("status", investorDto.getStatus().toUpperCase());

					Document loginInvUpdate = new Document();
					loginInvUpdate.append("$set", loginInvUpdateDoc);

					loginAdmCollection.updateMany(loginInvQuery, loginInvUpdate);

					// logout all users if status is invactive
					if (Constant.STATUS_INACTIVE.equalsIgnoreCase(investorDto.getStatus())) {
						List<? extends Bson> pipeline = Arrays.asList(
								new Document().append("$match", new Document().append("investorCode", investorCode)),
								new Document().append("$project", new Document().append("_id", 0.0).append("userID",
										new Document().append("$concatArrays", Arrays.asList("$users.username")))));
						Document result = invCollection.aggregate(pipeline).first();
						NotifyServiceDTO notifyDto = mongoTemplate.getConverter().read(NotifyServiceDTO.class, result);

						Utility.sendHandleLogout(notifyDto.getUserID(), refId);
					}
				}
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void updateInvestorPA(HttpServletRequest request, String investorCode, ApprovalUpdateInvestorDTO investorDto,
			long refId) {
		String methodName = "updateInvestorPA";
		try {
			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertInvestorUpdatePA(userInfo, investorCode, investorDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_INVESTOR,
					ActivityLogService.ACTIVITY_UPDATE_INVESTOR_DESC, investorCode, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertInvestorUpdatePA(UserInfoDTO userInfo, String investorCode,
			ApprovalUpdateInvestorDTO investorDto, long refId) {
		String methodName = "insertInvestorUpdatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());
			nestedObjInfo.setMemberCode(userInfo.getMemberCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.INVESTOR_UPDATE);
			pendingData.setCollectionName("investors");
			pendingData.setQueryField("investorCode");
			pendingData.setQueryValue(investorCode);
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setOldValue(new Gson().toJson(investorDto.getOldData()));
			pendingData.setPendingValue(new Gson().toJson(investorDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_INVESTOR_UPDATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.INVESTOR_UPDATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), investorCode));
			pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
			pendingApproval.setNestedObjInfo(nestedObjInfo);
			pendingApproval.setPendingData(pendingData);
			approvalId = pendingApprovalRepo.save(pendingApproval).getId();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return approvalId;
	}

	public InvestorDTO getInvestorDetail(String investorCode, long refId) {
		String methodName = "getInvestorDetail";
		try {
			Document query = new Document();
			query.append("investorCode", investorCode);

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");

			Document investorDoc = collection.find(query).first();
			if (investorDoc == null) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			Investor investor = mongoTemplate.getConverter().read(Investor.class, investorDoc);

			if (investor != null) {
				MarginInfoDTO marginInfo = new MarginInfoDTO();
				marginInfo.setInvestorCode(investor.getInvestorCode());
				marginInfo.setInvestorName(investor.getInvestorName());
				if (investor.getAccount() != null) {
					InvestorMarginInfo invMarginInfo = marginInfoService.getInvestorMarginInfo(investorCode, refId);
					marginInfo.setCurrency(investor.getAccount().getCurrency());
					marginInfo.setMarginDeficitInterestRate(investor.getAccount().getMarginDeficitInterestRate());
					marginInfo.setMarginSurplusInterestRate(investor.getAccount().getMarginSurplusInterestRate());
					marginInfo.setAvailableBalance(invMarginInfo.getSodBalance() + invMarginInfo.getChangedAmount());
				}

				InvestorDTO investorDto = modelMapper.map(investor, InvestorDTO.class);
				investorDto.setAccount(marginInfo);
				return investorDto;
			} else
				return null;

		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public InvestorDTO getInvestorInfo(String investorCode, long refId) {
		String methodName = "getInvestorInfo";
		try {
			Document query = new Document();
			query.append("investorCode", investorCode);

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");

			Document investorDoc = collection.find(query).first();
			if (investorDoc == null) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			Investor investor = mongoTemplate.getConverter().read(Investor.class, investorDoc);

			if (investor != null) {
				// clear some fields
				investor.setUsers(null);
				if (investor.getCompany() != null && investor.getCompany().getDelegate() != null) {
					investor.getCompany().getDelegate().setScannedBackIdCard(null);
					investor.getCompany().getDelegate().setScannedFrontIdCard(null);
					investor.getCompany().getDelegate().setScannedSignature(null);
				}

				if (investor.getIndividual() != null) {
					investor.getIndividual().setScannedBackIdCard(null);
					investor.getIndividual().setScannedFrontIdCard(null);
					investor.getIndividual().setScannedSignature(null);
				}

				MarginInfoDTO marginInfo = new MarginInfoDTO();
				marginInfo.setInvestorCode(investor.getInvestorCode());
				marginInfo.setInvestorName(investor.getInvestorName());
				if (investor.getAccount() != null) {
					InvestorMarginInfo invMarginInfo = marginInfoService.getInvestorMarginInfo(investorCode, refId);
					marginInfo.setCurrency(investor.getAccount().getCurrency());
					marginInfo.setMarginDeficitInterestRate(investor.getAccount().getMarginDeficitInterestRate());
					marginInfo.setMarginSurplusInterestRate(investor.getAccount().getMarginSurplusInterestRate());
					marginInfo.setAvailableBalance(invMarginInfo.getSodBalance() + invMarginInfo.getChangedAmount());
				}

				InvestorDTO investorDto = modelMapper.map(investor, InvestorDTO.class);
				investorDto.setAccount(marginInfo);
				return investorDto;
			} else
				return null;

		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public InvestorDTO getInvestorInfoByCQGAccountId(String cqgAccountId, long refId) {
		String methodName = "getInvestorInfoByCQGAccountId";
		try {
			Document query = new Document();
			query.append("cqgInfo.accountId", cqgAccountId);

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");

			Document investorDoc = collection.find(query).first();
			if (investorDoc == null) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			Investor investor = mongoTemplate.getConverter().read(Investor.class, investorDoc);

			if (investor != null) {
				// clear some fields
				investor.setUsers(null);
				if (investor.getCompany() != null && investor.getCompany().getDelegate() != null) {
					investor.getCompany().getDelegate().setScannedBackIdCard(null);
					investor.getCompany().getDelegate().setScannedFrontIdCard(null);
					investor.getCompany().getDelegate().setScannedSignature(null);
				}

				if (investor.getIndividual() != null) {
					investor.getIndividual().setScannedBackIdCard(null);
					investor.getIndividual().setScannedFrontIdCard(null);
					investor.getIndividual().setScannedSignature(null);
				}

				MarginInfoDTO marginInfo = new MarginInfoDTO();
				marginInfo.setInvestorCode(investor.getInvestorCode());
				marginInfo.setInvestorName(investor.getInvestorName());
				if (investor.getAccount() != null) {
					marginInfo.setCurrency(investor.getAccount().getCurrency());
					marginInfo.setMarginDeficitInterestRate(investor.getAccount().getMarginDeficitInterestRate());
					marginInfo.setMarginSurplusInterestRate(investor.getAccount().getMarginSurplusInterestRate());
				}

				InvestorDTO investorDto = modelMapper.map(investor, InvestorDTO.class);
				investorDto.setAccount(marginInfo);
				return investorDto;
			} else
				return null;

		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public BasePagination<UserDTO> listInvestorUsers(HttpServletRequest request, String investorCode, long refId) {
		String methodName = "listInvestorUsers";
		BasePagination<UserDTO> pagination = null;
		try {
			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			Document query1 = new Document();
			query1.append("investorCode", investorCode);

			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "users.", refId);

			List<? extends Bson> pipeline = Arrays.asList(new Document().append("$match", query1),
					new Document().append("$unwind", new Document().append("path", "$users")),
					new Document().append("$match", searchCriteria.getQuery()),
					new Document().append("$sort", searchCriteria.getSort()),
					new Document().append("$project",
							new Document().append("_id", new Document().append("$toString", "$users._id"))
									.append("username", "$users.username").append("fullName", "$users.fullName")
									.append("email", "$users.email").append("phoneNumber", "$users.phoneNumber")
									.append("status", "$users.status")
									.append("isPasswordExpiryCheck", "$users.isPasswordExpiryCheck")
									.append("passwordExpiryDays", "$users.passwordExpiryDays")
									.append("expiryAlertDays", "$users.expiryAlertDays")
									.append("createdDate", "$users.createdDate")),
					new Document().append("$facet",
							new Document().append("stage1", Arrays.asList(new Document().append("$count", "total")))
									.append("stage2",
											Arrays.asList(new Document().append("$skip", searchCriteria.getSkip()),
													new Document().append("$limit", searchCriteria.getLimit())))),
					new Document().append("$unwind", new Document().append("path", "$stage1")), new Document().append(
							"$project", new Document().append("count", "$stage1.total").append("data", "$stage2")));

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");
			Document resultDoc = collection.aggregate(pipeline).first();
			pagination = mongoTemplate.getConverter().read(BasePagination.class, resultDoc);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return pagination;
	}

	public List<UserCSV> listInvestorUsersCsv(HttpServletRequest request, String investorCode, long refId) {
		String methodName = "listInvestorUsersCsv";
		List<UserCSV> userList = new ArrayList<>();
		try {
			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			Document query = new Document();
			query.append("investorCode", investorCode);

			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "users.", refId);

			List<? extends Bson> pipeline = Arrays
					.asList(new Document().append("$match", query),
							new Document().append("$unwind", new Document().append("path", "$users")),
							new Document().append("$match", searchCriteria.getQuery()),
							new Document().append("$sort", searchCriteria.getSort()),
							new Document().append("$project",
									new Document().append("_id", new Document().append("$toString", "$users._id"))
											.append("username", "$users.username").append("fullName", "$users.fullName")
											.append("email", "$users.email").append("phoneNumber", "$users.phoneNumber")
											.append("status", "$users.status")
											.append("isPasswordExpiryCheck", "$users.isPasswordExpiryCheck")
											.append("passwordExpiryDays", "$users.passwordExpiryDays")
											.append("expiryAlertDays", "$users.expiryAlertDays")
											.append("createdDate", new Document().append("$dateToString",
													new Document().append("format", "%d/%m/%Y %H:%M:%S").append("date",
															new Document().append("$toDate", "$users.createdDate"))))));

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");
			try (MongoCursor<Document> cur = collection.aggregate(pipeline).iterator()) {

				while (cur.hasNext()) {
					UserCSV userCSV = mongoTemplate.getConverter().read(UserCSV.class, cur.next());
					if (userCSV != null)
						userList.add(userCSV);
				}
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userList;
	}

	public UserDTO getInvestorUser(String investorCode, String username, long refId) {
		String methodName = "getInvestorUser";
		try {
			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");

			List<? extends Bson> pipeline = Arrays.asList(
					new Document().append("$match", new Document().append("investorCode", investorCode)),
					new Document().append("$unwind", new Document().append("path", "$users")),
					new Document().append("$match", new Document().append("users.username", username)),
					new Document().append("$project", new Document().append("_id", 0.0).append("users", 1.0)),
					new Document().append("$replaceRoot", new Document().append("newRoot", "$users")));

			Document resultDoc = collection.aggregate(pipeline).first();
			UserDTO userDto = mongoTemplate.getConverter().read(UserDTO.class, resultDoc);
			return userDto;
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void createInvestorUser(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "createInvestorUser";
		try {
			String investorCode = pendingApproval.getPendingData().getQueryValue();
			UserDTO userDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(), UserDTO.class);

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog2(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_INVESTOR_USER2,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_INVESTOR_USER2_DESC, userDto.getUsername(),
					investorCode, pendingApproval.getId());
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");
			
			BasicDBObject query = new BasicDBObject();
			query.append("investorCode", investorCode);
			
			// get memberCode, brokerCode, collaboratorCode from investor
			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("memberCode", 1.0);
			projection.append("memberName", 1.0);
			projection.append("brokerCode", 1.0);
			projection.append("brokerName", 1.0);
			projection.append("collaboratorCode", 1.0);
			projection.append("collaboratorName", 1.0);
			projection.append("investorCode", 1.0);
			projection.append("investorName", 1.0);

			Document investorDoc = collection.find(query).projection(projection).first();
			InvestorDTO investorDto = mongoTemplate.getConverter().read(InvestorDTO.class, investorDoc);
						
			// insert loginAdminUser
			String password = Utility.generateRandomPassword();
			String pin = Utility.generateRandomPin();
			LoginInvestorUser newLoginInvUser = createLoginInvestorUser(investorDto, userDto, password, pin, refId);

			// send email
			sendCreateNewUserEmail(userDto.getEmail(), newLoginInvUser.getUsername(), password, pin, refId);
			
			// insert into investor's users
			Document newUser = new Document();
			newUser.append("_id", new ObjectId());
			newUser.append("username", userDto.getUsername());
			newUser.append("fullName", userDto.getFullName());
			newUser.append("email", userDto.getEmail());
			newUser.append("phoneNumber", userDto.getPhoneNumber());
			newUser.append("status", Constant.STATUS_ACTIVE);
			newUser.append("note", userDto.getNote());
			newUser.append("isPasswordExpiryCheck", userDto.getIsPasswordExpiryCheck());
			newUser.append("passwordExpiryDays", userDto.getPasswordExpiryDays());
			newUser.append("expiryAlertDays", userDto.getExpiryAlertDays());
			newUser.append("createdUser", Utility.getCurrentUsername());
			newUser.append("createdDate", System.currentTimeMillis());

			collection.updateOne(query, Updates.addToSet("users", newUser));
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void createInvestorUserPA(HttpServletRequest request, String investorCode, UserDTO userDto, long refId) {
		String methodName = "createInvestorUserPA";
		boolean existedUser = false;
		try {
			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			existedUser = loginInvUserRepo.existsByUsername(userDto.getUsername());
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!existedUser) {
			try {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertInvestorUserCreatePA(userInfo, investorCode, userDto, refId);
				// send activity log
				activityLogService.sendActivityLog2(userInfo, request,
						ActivityLogService.ACTIVITY_CREATE_INVESTOR_USER2,
						ActivityLogService.ACTIVITY_CREATE_INVESTOR_USER2_DESC, userDto.getUsername(), investorCode,
						approvalId);
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "This username already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		}
	}

	private String insertInvestorUserCreatePA(UserInfoDTO userInfo, String investorCode, UserDTO userDto, long refId) {
		String methodName = "insertInvestorUserCreatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());
			nestedObjInfo.setMemberCode(userInfo.getMemberCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.INVESTOR_USER_CREATE);
			pendingData.setCollectionName("investors");
			pendingData.setQueryField("investorCode");
			pendingData.setQueryValue(investorCode);
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setPendingValue(new Gson().toJson(userDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_INVESTOR_USER_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.INVESTOR_USER_CREATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription2(
					SystemFunctionCode.INVESTOR_USER_CREATE_DESC, userDto.getUsername(), investorCode));
			pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
			pendingApproval.setNestedObjInfo(nestedObjInfo);
			pendingApproval.setPendingData(pendingData);
			approvalId = pendingApprovalRepo.save(pendingApproval).getId();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return approvalId;
	}

	public void createDefaultSetting(HttpServletRequest request, String memberCode, String investorCode,
			DefaultSettingDTO investorDto, long refId) {
		String methodName = "createDefaultSetting";
		try {
			long memberDefaultPositionLimit = getMemberDefaultPositionLimit(memberCode, refId);
			if (memberDefaultPositionLimit > 0 && investorDto.getDefaultPositionLimit() > memberDefaultPositionLimit) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			}

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_DEFAULT_SETTING,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_DEFAULT_SETTING_DESC, investorCode, "");

			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			Document updateDocument = new Document();
			if (investorDto.getDefaultPositionLimit() > 0) {
				updateDocument.append("defaultPositionLimit", investorDto.getDefaultPositionLimit());
			}

			if (updateDocument.isEmpty()) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			} else {
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("investors");

				Document query = new Document();
				query.append("investorCode", investorCode);

				updateDocument.append("lastModifiedUser", Utility.getCurrentUsername());
				updateDocument.append("lastModifiedDate", System.currentTimeMillis());

				// update default position litmit and fee for all commodities
				List<Document> newCommodities = new ArrayList<Document>();
				List<CQGCMSCommodityDTO> cqgCommodities = new ArrayList<CQGCMSCommodityDTO>();
				
				Document projection = new Document();
				projection.append("_id", 0.0);
				projection.append("commodities", 1.0);

				Document resultDoc = collection.find(query).projection(projection).first();
				InvestorDTO invComm = mongoTemplate.getConverter().read(InvestorDTO.class, resultDoc);
				if (invComm != null && invComm.getCommodities() != null) {
					for (Commodity comm : invComm.getCommodities()) {
						Document newComm = new Document();
						newComm.append("commodityCode", comm.getCommodityCode());
						newComm.append("commodityName", comm.getCommodityCode());
						newComm.append("currency", Constant.CURRENCY_VND);
						if (Constant.POSITION_INHERITED.equalsIgnoreCase(comm.getPositionLimitType())) {
							if (investorDto.getDefaultPositionLimit() > 0) {
								newComm.append("positionLimitType", Constant.POSITION_INHERITED);
								newComm.append("positionLimit", investorDto.getDefaultPositionLimit());
								
								CQGCMSCommodityDTO cqgComm = new CQGCMSCommodityDTO();
								cqgComm.setSymbol(comm.getCommodityCode());
								cqgComm.setPositionLimit(investorDto.getDefaultPositionLimit());
								cqgCommodities.add(cqgComm);
							} else {
								newComm.append("positionLimitType", Constant.POSITION_INHERITED);
								newComm.append("positionLimit", comm.getPositionLimit());
							}
						} else {
							newComm.append("positionLimitType", comm.getPositionLimitType());
							newComm.append("positionLimit", comm.getPositionLimit());
						}
						newComm.append("commodityFee", comm.getCommodityFee());
						newCommodities.add(newComm);
					}
				}

				updateDocument.append("commodities", newCommodities);

				Document update = new Document();
				update.append("$set", updateDocument);

				// sync to CQG
				if (Utility.isCQGSyncOn()) {
					// update cqg risk params
					InvestorDTO investorInfo = getInvestorInfo(investorCode, refId);
					boolean result = cqgService.updateCQGRiskParams(investorInfo.getCqgInfo().getAccountId(), 0, 0, investorDto.getDefaultPositionLimit(), refId);
					if (!result) {
						throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
					}
					
					// update cqg account market limits
					result = cqgService.updateCQGAccountMarketLimits(investorInfo.getCqgInfo().getAccountId(),
							cqgCommodities, refId);
					if (!result) {
						throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
					}
				}
				
				// update to DB
				collection.updateOne(query, update);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private long getMemberDefaultPositionLimit(String memberCode, long refId) {
		String methodName = "getMemberDefaultPositionLimit";
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");

			Document query = new Document();
			query.append("code", memberCode);

			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("defaultPositionLimit", 1.0);

			Document result = collection.find(query).projection(projection).first();
			DefaultSettingDTO defaultDto = mongoTemplate.getConverter().read(DefaultSettingDTO.class, result);
			if (defaultDto != null)
				return defaultDto.getDefaultPositionLimit();
			else
				return 0;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void createInvestorCommodities(HttpServletRequest request, String memberCode, String investorCode,
			CommoditiesDTO commoditiesDto, long refId) {
		String methodName = "createInvestorCommodities";
		try {
			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_COMMODITIES_ASSIGN,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_COMMODITIES_ASSIGN_DESC, investorCode, "");
			
			if (commoditiesDto.getCommodities() != null && commoditiesDto.getCommodities().size() > 0) {
				List<Document> commodities = new ArrayList<Document>();
				List<Commodity> memberCommodities = getMemberCommodities(memberCode, refId);
				List<CQGCMSCommodityDTO> cqgCommodities = new ArrayList<CQGCMSCommodityDTO>();

				for (Commodity comm : commoditiesDto.getCommodities()) {
					// check if investor position limit is less than member's
					if (memberCommodities != null) {
						Commodity memberComm = getCommodityInAListByCode(memberCommodities, comm.getCommodityCode());
						if (memberComm != null && comm.getPositionLimit() > memberComm.getPositionLimit()) {
							throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
						}
					}

					CQGCMSCommodityDTO cqgComm = new CQGCMSCommodityDTO();
					cqgComm.setSymbol(comm.getCommodityCode());
					cqgComm.setPositionLimit(comm.getPositionLimit());
					cqgCommodities.add(cqgComm);

					Document commDoc = new Document();
					commDoc.append("commodityCode", comm.getCommodityCode());
					commDoc.append("commodityName", comm.getCommodityName());
					commDoc.append("commodityFee", comm.getCommodityFee());
					commDoc.append("positionLimitType", comm.getPositionLimitType());
					commDoc.append("positionLimit", comm.getPositionLimit());
					commDoc.append("currency", Constant.CURRENCY_VND);
					commodities.add(commDoc);
				}

				if (Utility.isCQGSyncOn()) {
					// update cqg account market limits
					InvestorDTO investorDto = getInvestorInfo(investorCode, refId);
					setRemovedCommoditiesMarkerLimits(cqgCommodities, investorDto);
					boolean result = cqgService.updateCQGAccountMarketLimits(investorDto.getCqgInfo().getAccountId(),
							cqgCommodities, refId);
					if (!result) {
						throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
					}
				}

				Document query = new Document();
				query.append("investorCode", investorCode);

				Document updateDoc = new Document();
				updateDoc.append("commodities", commodities);
				updateDoc.append("lastModifiedUser", Utility.getCurrentUsername());
				updateDoc.append("lastModifiedDate", System.currentTimeMillis());

				Document update = new Document();
				update.append("$set", updateDoc);

				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("investors");
				collection.updateOne(query, update);
			} else {
				Document query = new Document();
				query.append("investorCode", investorCode);

				Document updateDoc = new Document();
				updateDoc.append("commodities", null);
				updateDoc.append("lastModifiedUser", Utility.getCurrentUsername());
				updateDoc.append("lastModifiedDate", System.currentTimeMillis());

				Document update = new Document();
				update.append("$set", updateDoc);

				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("investors");
				collection.updateOne(query, update);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private List<Commodity> getMemberCommodities(String memberCode, long refId) {
		String methodName = "getMemberCommodities";
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");

			Document query = new Document();
			query.append("code", memberCode);

			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("commodities", 1.0);

			Document result = collection.find(query).projection(projection).first();
			MemberCommoditiesDTO memberDto = mongoTemplate.getConverter().read(MemberCommoditiesDTO.class, result);
			if (memberDto != null) {
				return memberDto.getCommodities();
			}
			return null;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Commodity getCommodityInAListByCode(List<Commodity> commodities, String code) {
		if (commodities != null && commodities.size() > 0) {
			for (Commodity comm : commodities) {
				if (code.equals(comm.getCommodityCode()))
					return comm;
			}
		}

		return null;
	}
	
	private boolean existCommodityInCQGCommodities(List<CQGCMSCommodityDTO> commodities, String code) {
		if (commodities != null && commodities.size() > 0) {
			for (CQGCMSCommodityDTO comm : commodities) {
				if (code.equals(comm.getSymbol()))
					return true;
			}
		}

		return false;
	}

	private void setRemovedCommoditiesMarkerLimits(List<CQGCMSCommodityDTO> cqgCommodities, InvestorDTO investorDto) {
		for (Commodity comm: investorDto.getCommodities()) {
			if (!existCommodityInCQGCommodities(cqgCommodities, comm.getCommodityCode())) {
				CQGCMSCommodityDTO cqgComm = new CQGCMSCommodityDTO();
				cqgComm.setSymbol(comm.getCommodityCode());
				cqgComm.setPositionLimit(0);
				cqgCommodities.add(cqgComm);
			} else {
				continue;
			}
		}
	}
	
	public void setInvestorNewPositionOrderLock(HttpServletRequest request, PendingApproval pendingApproval,
			long refId) {
		String methodName = "setInvestorNewPositionOrderLock";
		try {
			String investorCode = pendingApproval.getPendingData().getQueryValue();
			RiskParametersDTO riskParamDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(),
					RiskParametersDTO.class);

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_INVESTOR_RISK_NEW_POSITION_ORDER_LOCK,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_INVESTOR_RISK_NEW_POSITION_ORDER_LOCK_DESC,
					investorCode, pendingApproval.getId());

			Document updateDocument = new Document();
			updateDocument.append("riskParameters.newPositionOrderLock",
					riskParamDto.getRiskParameters().getNewPositionOrderLock());
			updateDocument.append("lastModifiedUser", Utility.getCurrentUsername());
			updateDocument.append("lastModifiedDate", System.currentTimeMillis());

			Document query = new Document();
			query.append("investorCode", investorCode);

			Document update = new Document();
			update.append("$set", updateDocument);

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");
			collection.updateOne(query, update);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void setInvestorNewPositionOrderLockPA(HttpServletRequest request, String investorCode,
			ApprovalRiskParametersDTO riskParamDto, long refId) {
		String methodName = "setInvestorNewPositionOrderLockPA";
		try {
			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertInvestorNewPositionOrderLockPA(userInfo, investorCode, riskParamDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_RISK_NEW_POSITION_ORDER_LOCK,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_RISK_NEW_POSITION_ORDER_LOCK_DESC, investorCode,
					approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertInvestorNewPositionOrderLockPA(UserInfoDTO userInfo, String investorCode,
			ApprovalRiskParametersDTO riskParamDto, long refId) {
		String methodName = "insertInvestorNewPositionOrderLockPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.INVESTOR_RISK_NEW_POSITION_LOCK_SET);
			pendingData.setCollectionName("investors");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setAppliedObject(String.format(ApprovalConstant.APPLIED_OBJ_INVESTOR, investorCode));
			pendingData.setQueryField("investorCode");
			pendingData.setQueryValue(investorCode);
			pendingData.setOldValue(new Gson().toJson(riskParamDto.getOldData()));
			pendingData.setPendingValue(new Gson().toJson(riskParamDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_INVESTOR_RISK_NEW_ORDER_LOCK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.INVESTOR_RISK_NEW_ORDER_LOCK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), investorCode));
			pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
			pendingApproval.setNestedObjInfo(nestedObjInfo);
			pendingApproval.setPendingData(pendingData);
			approvalId = pendingApprovalRepo.save(pendingApproval).getId();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return approvalId;
	}

	public void setInvestorOrderLock(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "setInvestorOrderLock";
		try {
			String investorCode = pendingApproval.getPendingData().getQueryValue();
			RiskParametersDTO riskParamDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(),
					RiskParametersDTO.class);

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_INVESTOR_RISK_ORDER_LOCK,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_INVESTOR_RISK_ORDER_LOCK_DESC, investorCode,
					pendingApproval.getId());

			Document updateDocument = new Document();
			updateDocument.append("riskParameters.orderLock", riskParamDto.getRiskParameters().getOrderLock());
			updateDocument.append("lastModifiedUser", Utility.getCurrentUsername());
			updateDocument.append("lastModifiedDate", System.currentTimeMillis());

			Document query = new Document();
			query.append("investorCode", investorCode);

			Document update = new Document();
			update.append("$set", updateDocument);

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");
			collection.updateOne(query, update);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void setInvestorOrderLockPA(HttpServletRequest request, String investorCode,
			ApprovalRiskParametersDTO riskParamDto, long refId) {
		String methodName = "setInvestorOrderLockPA";
		try {
			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertInvestorOrderLockPA(userInfo, investorCode, riskParamDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_RISK_ORDER_LOCK,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_RISK_ORDER_LOCK_DESC, investorCode, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertInvestorOrderLockPA(UserInfoDTO userInfo, String investorCode,
			ApprovalRiskParametersDTO riskParamDto, long refId) {
		String methodName = "insertInvestorOrderLockPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.INVESTOR_RISK_ORDER_LOCK_SET);
			pendingData.setCollectionName("investors");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setAppliedObject(String.format(ApprovalConstant.APPLIED_OBJ_INVESTOR, investorCode));
			pendingData.setQueryField("investorCode");
			pendingData.setQueryValue(investorCode);
			pendingData.setOldValue(new Gson().toJson(riskParamDto.getOldData()));
			pendingData.setPendingValue(new Gson().toJson(riskParamDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_INVESTOR_RISK_ORDER_LOCK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.INVESTOR_RISK_ORDER_LOCK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), investorCode));
			pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
			pendingApproval.setNestedObjInfo(nestedObjInfo);
			pendingApproval.setPendingData(pendingData);
			approvalId = pendingApprovalRepo.save(pendingApproval).getId();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return approvalId;
	}

	public void setMarginMultiplier(HttpServletRequest request, String investorCode, MarginMultiplierDTO marginMultDto,
			long refId) {
		String methodName = "setMarginMultiplier";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_MARGIN_MULTIPLIER,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_MARGIN_MULTIPLIER_DESC, investorCode, "");

			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			// update cqg risk params
			if (Utility.isCQGSyncOn()) {
				InvestorDTO investorInfo = getInvestorInfo(investorCode, refId);
				boolean result = cqgService.updateCQGRiskParams(investorInfo.getCqgInfo().getAccountId(), marginMultDto.getMarginMultiplier(), 0, 0, refId);
				if (!result) {
					throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
				}
			}
						
			Document updateDocument = new Document();
			updateDocument.append("marginMultiplier", marginMultDto.getMarginMultiplier());
			updateDocument.append("lastModifiedUser", Utility.getCurrentUsername());
			updateDocument.append("lastModifiedDate", System.currentTimeMillis());

			Document query = new Document();
			query.append("investorCode", investorCode);

			Document update = new Document();
			update.append("$set", updateDocument);

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");
			collection.updateOne(query, update);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void setMarginRatioAlert(HttpServletRequest request, String investorCode,
			MarginRatioAlertDTO marginRatioAlertDto, long refId) {
		String methodName = "setMarginRatioAlert";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_MARGIN_RATIO,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_MARGIN_RATIO_DESC, investorCode, "");

			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			boolean isValidRatio = true;
			if (marginRatioAlertDto.getMarginRatioAlert().getFinalizationRatio() >= marginRatioAlertDto
					.getMarginRatioAlert().getCancelOrderRatio()
					|| (marginRatioAlertDto.getMarginRatioAlert().getFinalizationRatio() >= marginRatioAlertDto
							.getMarginRatioAlert().getWarningRatio())) {
				isValidRatio = false;
			} else if (marginRatioAlertDto.getMarginRatioAlert().getCancelOrderRatio() >= marginRatioAlertDto
					.getMarginRatioAlert().getWarningRatio()) {
				isValidRatio = false;
			}

			if (!isValidRatio) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			}

			Document marginRatioAlert = new Document();
			marginRatioAlert.append("warningRatio", marginRatioAlertDto.getMarginRatioAlert().getWarningRatio());
			marginRatioAlert.append("cancelOrderRatio",
					marginRatioAlertDto.getMarginRatioAlert().getCancelOrderRatio());
			marginRatioAlert.append("finalizationRatio",
					marginRatioAlertDto.getMarginRatioAlert().getFinalizationRatio());

			Document updateDocument = new Document();
			updateDocument.append("marginRatioAlert", marginRatioAlert);
			updateDocument.append("lastModifiedUser", Utility.getCurrentUsername());
			updateDocument.append("lastModifiedDate", System.currentTimeMillis());

			Document query = new Document();
			query.append("investorCode", investorCode);

			Document update = new Document();
			update.append("$set", updateDocument);

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");
			collection.updateOne(query, update);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private boolean checkIfExistedFee(String investorCode, String feeName, long refId) {
		String methodName = "checkIfExistedFee";
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> memberCollection = database.getCollection("investors");

			Document query = new Document();
			query.append("investorCode", investorCode);
			query.append("generalFees.name", feeName);

			Document result = memberCollection.find(query).first();
			if (result != null)
				return true;
			return false;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void setGeneralFee(HttpServletRequest request, String investorCode, GeneralFeeDTO generalFeeDto,
			long refId) {
		String methodName = "setGeneralFee";
		try {
			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_GENERAL_FEE,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_GENERAL_FEE_DESC, investorCode, "");

			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			if (generalFeeDto != null) {
				if (checkIfExistedFee(investorCode, generalFeeDto.getName(), refId)) {
					throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
				}

				Document feeDoc = new Document();
				feeDoc.append("name", generalFeeDto.getName());
				feeDoc.append("processMethod", generalFeeDto.getProcessMethod());
				feeDoc.append("feeAmount", generalFeeDto.getFeeAmount());
				feeDoc.append("appliedDate", generalFeeDto.getAppliedDate());

				Document query = new Document();
				query.append("investorCode", investorCode);

				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("investors");
				collection.updateOne(query, Updates.addToSet("generalFees", feeDoc));
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void updateGeneralFee(HttpServletRequest request, String investorCode, GeneralFeeDTO generalFeeDto,
			long refId) {
		String methodName = "setGeneralFees";
		try {
			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_GENERAL_FEE,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_GENERAL_FEE_DESC, investorCode, "");

			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			if (generalFeeDto != null) {
				Document updateFeeDoc = new Document();
				updateFeeDoc.append("name", generalFeeDto.getName());
				updateFeeDoc.append("processMethod", generalFeeDto.getProcessMethod());
				updateFeeDoc.append("feeAmount", generalFeeDto.getFeeAmount());
				updateFeeDoc.append("appliedDate", generalFeeDto.getAppliedDate());

				Document query = new Document();
				query.append("investorCode", investorCode);

				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("investors");

				Document investorFields = new Document("generalFees", new Document("name", generalFeeDto.getName()));
				Document investorUpdate = new Document("$pull", investorFields);
				collection.updateOne(query, investorUpdate);
				collection.updateOne(query, Updates.addToSet("generalFees", updateFeeDoc));
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void changeBroker(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "changeBroker";
		try {
			String investorCode = pendingApproval.getPendingData().getQueryValue();
			ChangeGroupDTO changeGroupDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(),
					ChangeGroupDTO.class);

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog2(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_INVESTOR_BROKER_CHANGE,
					ActivityLogService.ACTIVITY_APPROVAL_INVESTOR_BROKER_CHANGE_DESC, investorCode,
					changeGroupDto.getGroupCode(), pendingApproval.getId());

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> invCollection = database.getCollection("investors");

			BasicDBObject invQuery = new BasicDBObject();
			invQuery.put("investorCode", investorCode);

			BasicDBObject invUpdateDoc = new BasicDBObject();
			invUpdateDoc.put("brokerCode", changeGroupDto.getGroupCode());
			invUpdateDoc.put("brokerName", changeGroupDto.getGroupName());

			BasicDBObject invUpdate = new BasicDBObject();
			invUpdate.put("$set", invUpdateDoc);

			invCollection.updateOne(invQuery, invUpdate);

			MongoCollection<Document> loginAdmUserCollection = database.getCollection("login_investor_users");
			BasicDBObject loginAdmUserQuery = new BasicDBObject();
			loginAdmUserQuery.put("investorCode", investorCode);

			BasicDBObject newLoginAdmUser = new BasicDBObject();
			newLoginAdmUser.put("brokerCode", changeGroupDto.getGroupCode());
			newLoginAdmUser.put("brokerName", changeGroupDto.getGroupName());

			BasicDBObject updateObj = new BasicDBObject();
			updateObj.put("$set", newLoginAdmUser);

			loginAdmUserCollection.updateMany(loginAdmUserQuery, updateObj);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void changeBrokerPA(HttpServletRequest request, String investorCode, ApprovalChangeGroupDTO changeGroupDto,
			long refId) {
		String methodName = "changeBrokerPA";

		try {
			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			if (!brokerRepo.existsBrokerByCode(changeGroupDto.getPendingData().getGroupCode())) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			}

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertChangeBrokerPA(userInfo, investorCode, changeGroupDto, refId);
			// send activity log
			activityLogService.sendActivityLog2(userInfo, request, ActivityLogService.ACTIVITY_INVESTOR_BROKER_CHANGE,
					ActivityLogService.ACTIVITY_INVESTOR_BROKER_CHANGE_DESC, investorCode,
					changeGroupDto.getPendingData().getGroupCode(), approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertChangeBrokerPA(UserInfoDTO userInfo, String investorCode,
			ApprovalChangeGroupDTO changeGroupDto, long refId) {
		String methodName = "insertChangeBrokerPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());
			nestedObjInfo.setMemberCode(userInfo.getMemberCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.INVESTOR_CHANGE_BROKER);
			pendingData.setCollectionName("investors");
			pendingData.setQueryField("investorCode");
			pendingData.setQueryValue(investorCode);
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setOldValue(new Gson().toJson(changeGroupDto.getOldData()));
			pendingData.setPendingValue(new Gson().toJson(changeGroupDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_INVESTOR_BROKER_CHANGE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.INVESTOR_BROKER_CHANGE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.INVESTOR_BROKER_CHANGE_DESC,
							investorCode, changeGroupDto.getPendingData().getGroupCode()));
			pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
			pendingApproval.setNestedObjInfo(nestedObjInfo);
			pendingApproval.setPendingData(pendingData);
			approvalId = pendingApprovalRepo.save(pendingApproval).getId();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return approvalId;
	}

	public void changeCollaborator(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "changeCollaborator";
		try {
			String investorCode = pendingApproval.getPendingData().getQueryValue();
			ChangeGroupDTO changeGroupDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(),
					ChangeGroupDTO.class);

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog2(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_INVESTOR_COLLABORATOR_CHANGE,
					ActivityLogService.ACTIVITY_APPROVAL_INVESTOR_COLLABORATOR_CHANGE_DESC, investorCode,
					changeGroupDto.getGroupCode(), pendingApproval.getId());

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> invCollection = database.getCollection("investors");

			BasicDBObject invQuery = new BasicDBObject();
			invQuery.put("investorCode", investorCode);

			BasicDBObject invUpdateDoc = new BasicDBObject();
			invUpdateDoc.put("collaboratorCode", changeGroupDto.getGroupCode());
			invUpdateDoc.put("collaboratorName", changeGroupDto.getGroupName());

			BasicDBObject invUpdate = new BasicDBObject();
			invUpdate.put("$set", invUpdateDoc);

			invCollection.updateOne(invQuery, invUpdate);

			MongoCollection<Document> loginAdmUserCollection = database.getCollection("login_investor_users");
			BasicDBObject loginAdmUserQuery = new BasicDBObject();
			loginAdmUserQuery.put("investorCode", investorCode);

			BasicDBObject newLoginAdmUser = new BasicDBObject();
			newLoginAdmUser.put("collaboratorCode", changeGroupDto.getGroupCode());
			newLoginAdmUser.put("collaboratorName", changeGroupDto.getGroupName());

			BasicDBObject updateObj = new BasicDBObject();
			updateObj.put("$set", newLoginAdmUser);

			loginAdmUserCollection.updateMany(loginAdmUserQuery, updateObj);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void changeCollaboratorPA(HttpServletRequest request, String investorCode,
			ApprovalChangeGroupDTO changeGroupDto, long refId) {
		String methodName = "changeCollaboratorPA";
		try {
			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			if (!collaboratorRepo.existsCollaboratorByCode(changeGroupDto.getPendingData().getGroupCode())) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			}

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertChangeCollaboratorPA(userInfo, investorCode, changeGroupDto, refId);
			// send activity log
			activityLogService.sendActivityLog2(userInfo, request,
					ActivityLogService.ACTIVITY_INVESTOR_COLLABORATOR_CHANGE,
					ActivityLogService.ACTIVITY_INVESTOR_COLLABORATOR_CHANGE_DESC, investorCode,
					changeGroupDto.getPendingData().getGroupCode(), approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertChangeCollaboratorPA(UserInfoDTO userInfo, String investorCode,
			ApprovalChangeGroupDTO changeGroupDto, long refId) {
		String methodName = "insertChangeCollaboratorPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());
			nestedObjInfo.setMemberCode(userInfo.getMemberCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.INVESTOR_CHANGE_COLLABORATOR);
			pendingData.setCollectionName("investors");
			pendingData.setQueryField("investorCode");
			pendingData.setQueryValue(investorCode);
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setOldValue(new Gson().toJson(changeGroupDto.getOldData()));
			pendingData.setPendingValue(new Gson().toJson(changeGroupDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_INVESTOR_COLLABORATOR_CHANGE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.INVESTOR_COLLABORATOR_CHANGE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.INVESTOR_COLLABORATOR_CHANGE_DESC,
							investorCode, changeGroupDto.getPendingData().getGroupCode()));
			pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
			pendingApproval.setNestedObjInfo(nestedObjInfo);
			pendingApproval.setPendingData(pendingData);
			approvalId = pendingApprovalRepo.save(pendingApproval).getId();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return approvalId;
	}

	public void updateMarginInfo(HttpServletRequest request, String investorCode, MarginInfoDTO marginDto, long refId) {
		String methodName = "updateMarginInfo";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_MARGIN_INFO,
					ActivityLogService.ACTIVITY_UPDATE_MARGIN_INFO_DESC, investorCode, "");

			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}

			Document updateDocument = new Document();
			updateDocument.append("account.marginSurplusInterestRate", marginDto.getMarginSurplusInterestRate());
			updateDocument.append("account.marginDeficitInterestRate", marginDto.getMarginDeficitInterestRate());
			updateDocument.append("lastModifiedUser", Utility.getCurrentUsername());
			updateDocument.append("lastModifiedDate", System.currentTimeMillis());

			Document query = new Document();
			query.append("investorCode", investorCode);

			Document update = new Document();
			update.append("$set", updateDocument);

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");
			collection.updateOne(query, update);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public long getWithdrawableAmount(long refId) {
		return 100000000l;
	}

	public void depositMargin(HttpServletRequest request, InvestorMarginTransApproval pendingApproval, long refId) {
		String methodName = "depositMargin";
		try {
			String investorCode = pendingApproval.getPendingData().getQueryValue();
			MarginTransactionDTO marginTransDto = new Gson()
					.fromJson(pendingApproval.getPendingData().getPendingValue(), MarginTransactionDTO.class);

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_INVESTOR_DEPOSIT_MONEY,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_INVESTOR_DEPOSIT_MONEY_DESC, investorCode,
					pendingApproval.getId());

			if (Utility.isCQGSyncOn()) {
				updateCQGBalance(investorCode, marginTransDto.getAmount(), refId);
			}
			try {
				// update changedAmount in investor_margin_info
				InvestorMarginInfo marginInfo = marginInfoService.getInvestorMarginInfo(investorCode, refId);
				double changedAmount = marginInfo.getChangedAmount() + marginTransDto.getAmount();

				marginInfoService.updateChangedAmount(investorCode, changedAmount, refId);

				// update investor_margin_trans
				InvestorMarginTransaction marginTrans = modelMapper.map(marginTransDto, InvestorMarginTransaction.class);
				marginTrans.setCurrency(Constant.CURRENCY_VND);
				marginTrans.setApprovalDate(System.currentTimeMillis());
				marginTrans.setApprovalUser(userInfo.getUsername());
				marginTrans.setSessionDate(getSessionDate(refId));
				invMarginTransRepo.save(marginTrans);
			} catch (Exception e) {
				// rollback CQG balance
				if (Utility.isCQGSyncOn()) {
					updateCQGBalance(investorCode, - marginTransDto.getAmount(), refId);
				}
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private boolean updateCQGBalance(String investorCode, long vndAmount, long refId) {
		// update cqg balance
		InvestorDTO investorDto = getInvestorInfo(investorCode, refId);
		double exchangeRate = getExchangeRate(refId);
		double usdChangedAmt = Precision.round(vndAmount/exchangeRate, 2);

		boolean result = cqgService.updateCQGAccountBalance(investorCode,
				investorDto.getCqgInfo().getAccountId(), usdChangedAmt, refId);
		if (!result) {
			throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
		}
		return result;
	}
	
	private String getSessionDate(long refId) {
		String methodName = "getSessionDate";
		String sessionDate = "";
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			String[] res = serviceCon.sendGetRequest(serviceCon.getSessionDateServiceURL(),
					ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
			if (res.length >= 2 && "200".equals(res[0])) {
				AdminResponseObj response = new Gson().fromJson(res[1], AdminResponseObj.class);
				if (response != null && Constant.RESPONSE_OK.equalsIgnoreCase(response.getStatus())) {
					sessionDate = response.getData().getDate();
				}
			}
			return sessionDate;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void depositMarginPA(HttpServletRequest request, MarginTransactionDTO marginTransDto, long refId) {
		String methodName = "depositMarginPA";
		try {
			// check if investor is activated
			InvestorDTO investorDto = getInvestorInfo(marginTransDto.getInvestorCode(), refId);
			if (Utility.isNotNull(investorDto.getCqgInfo())) {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to inv_margin_trans_approvals
				String approvalId = insertInvestorMarginDepositPA(userInfo, marginTransDto, refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request,
						ActivityLogService.ACTIVITY_CREATE_INVESTOR_DEPOSIT_MONEY,
						ActivityLogService.ACTIVITY_CREATE_INVESTOR_DEPOSIT_MONEY_DESC, marginTransDto.getInvestorCode(),
						approvalId);
			} else {
				throw new CustomException(ErrorMessage.INVESTOR_IS_NOT_ACTIVATED, HttpStatus.OK);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertInvestorMarginDepositPA(UserInfoDTO userInfo, MarginTransactionDTO marginTransDto,
			long refId) {
		String methodName = "insertInvestorMarginDepositPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());
			nestedObjInfo.setMemberCode(userInfo.getMemberCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.INVESTOR_DEPOSIT_MONEY);
			pendingData.setCollectionName("investors");
			pendingData.setQueryField("investorCode");
			pendingData.setQueryValue(marginTransDto.getInvestorCode());
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setPendingValue(new Gson().toJson(marginTransDto));

			InvestorMarginTransApproval marginTransApproval = new InvestorMarginTransApproval();
			marginTransApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_ACCOUNT_TRANS_URL, approvalId));
			marginTransApproval.setFunctionCode(SystemFunctionCode.APPROVAL_INVESTOR_MARGIN_DEPOSIT_CODE);
			marginTransApproval.setFunctionName(SystemFunctionCode.INVESTOR_MARGIN_DEPOSIT_NAME);
			marginTransApproval.setDescription(SystemFunctionCode
					.getApprovalDescription(marginTransApproval.getFunctionName(), marginTransDto.getInvestorCode()));
			marginTransApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
			marginTransApproval.setNestedObjInfo(nestedObjInfo);
			marginTransApproval.setPendingData(pendingData);
			approvalId = invMarginTransApprovalRepo.save(marginTransApproval).getId();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return approvalId;
	}

	public void withdrawMargin(HttpServletRequest request, InvestorMarginTransApproval pendingApproval, long refId) {
		String methodName = "withdrawMargin";

		try {
			String investorCode = pendingApproval.getPendingData().getQueryValue();
			MarginTransactionDTO marginTransDto = new Gson()
					.fromJson(pendingApproval.getPendingData().getPendingValue(), MarginTransactionDTO.class);

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_INVESTOR_WITHDRAWAL_MONEY,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_INVESTOR_WITHDRAWAL_MONEY_DESC, investorCode,
					pendingApproval.getId());

			if (Utility.isCQGSyncOn()) {
				updateCQGBalance(investorCode, - marginTransDto.getAmount(), refId);
			}
			
			try {
				// update changedAmount, pendingWithdrawalAmount in investor_margin_info
				InvestorMarginInfo marginInfo = marginInfoService.getInvestorMarginInfo(investorCode, refId);
				double changedAmount = marginInfo.getChangedAmount() - marginTransDto.getAmount();
				double pendingWithdrawalAmount = marginInfo.getPendingWithdrawalAmount() - marginTransDto.getAmount();

				marginInfoService.updateChangedAmountAndPendingWithdrawalAmount(investorCode, changedAmount,
						pendingWithdrawalAmount, refId);

				// insert new investor_margin_trans
				InvestorMarginTransaction marginTrans = modelMapper.map(marginTransDto, InvestorMarginTransaction.class);
				marginTrans.setCurrency(Constant.CURRENCY_VND);
				marginTrans.setApprovalDate(System.currentTimeMillis());
				marginTrans.setApprovalUser(userInfo.getUsername());
				marginTrans.setSessionDate(getSessionDate(refId));
				invMarginTransRepo.save(marginTrans);
				
				// call checking maring ratio
				LocalServiceConnection localServcieConn = new LocalServiceConnection();
				localServcieConn.sendPostRequest(localServcieConn.getProcessMarginServiceURL(investorCode), "", ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
			} catch (Exception e) {
				// rollback CQG balance
				if (Utility.isCQGSyncOn()) {
					updateCQGBalance(investorCode, marginTransDto.getAmount(), refId);
				}
				
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void withdrawMarginPA(HttpServletRequest request, MarginTransactionDTO marginTransDto, long refId) {
		String methodName = "withdrawMarginPA";
		try {
			if (marginTransDto.getAmount() > getInvestorWithdrawalAmount(marginTransDto.getInvestorCode(), refId)) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			}
			
			// check if member can withdraw money
			String memberCode = marginTransDto.getMemberCode();
			Member member = memberRepo.findByCode(memberCode);

			if ("N".equals(member.getRiskParameters().getMarginWithdrawalLock())) {
				// check if investor is activated
				InvestorDTO investorDto = getInvestorInfo(marginTransDto.getInvestorCode(), refId);
				if (Utility.isNotNull(investorDto.getCqgInfo())) {
					// get redis user info
					UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
					// insert data to inv_margin_trans_approvals
					String approvalId = insertInvestorMarginWithdrawPA(userInfo, marginTransDto, refId);
					// send activity log
					activityLogService.sendActivityLog(userInfo, request,
							ActivityLogService.ACTIVITY_CREATE_INVESTOR_WITHDRAWAL_MONEY,
							ActivityLogService.ACTIVITY_CREATE_INVESTOR_WITHDRAWAL_MONEY_DESC,
							marginTransDto.getInvestorCode(), approvalId);

					// update pendingWithdrawalAmount in investor_margin_info
					InvestorMarginInfo marginInfo = marginInfoService
							.getInvestorMarginInfo(marginTransDto.getInvestorCode(), refId);
					double pendingWithdrawalAmount = marginInfo.getPendingWithdrawalAmount() + marginTransDto.getAmount();

					marginInfoService.updatePendingWithdrawalAmount(marginTransDto.getInvestorCode(),
							pendingWithdrawalAmount, refId);
				} else {
					throw new CustomException(ErrorMessage.INVESTOR_IS_NOT_ACTIVATED, HttpStatus.OK);
				}
			} else {
				throw new CustomException(ErrorMessage.WITHDRAWAL_DENIED, HttpStatus.OK);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private long getInvestorWithdrawalAmount(String investorCode, long refId) {
		String methodName = "getInvestorWithdrawalAmount";
		long withdrawalAmount = 0;
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			String[] res = serviceCon.sendGetRequest(serviceCon.getWithdrawalAmountServiceURL(investorCode),
					ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
			if (res.length >= 2 && "200".equals(res[0])) {
				AdminResponseObj response = new Gson().fromJson(res[1], AdminResponseObj.class);
				if (response != null && Constant.RESPONSE_OK.equalsIgnoreCase(response.getStatus())) {
					withdrawalAmount = response.getData().getAmount();
				}
			}
			return withdrawalAmount;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private double getExchangeRate(long refId) {
		String methodName = "getExchangeRate";
		double exchangeRate = 1;
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			String serviceURL = serviceCon.getExchangeRateServiceURL();
			AMLogger.logMessage(className, methodName, refId, "Exchange Rate Serivce URL: " + serviceURL);
			String[] res = serviceCon.sendGetRequest(serviceCon.getExchangeRateServiceURL(),
					ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
			AMLogger.logMessage(className, methodName, refId, "Exchange Rate Serivce Reseponse: " + res[0] + " => " + res[1]);
			if (res.length >= 2 && "200".equals(res[0])) { 
				ExchangeRateReponseDTO response = new Gson().fromJson(res[1], ExchangeRateReponseDTO.class);
				if (response != null && Constant.RESPONSE_OK.equalsIgnoreCase(response.getStatus())) {
					for (ExchangeRateDTO exRate : response.getData()) {
						if (Constant.CURRENCY_USD.equals(exRate.getMonetaryBase()) && Constant.STATUS_ACTIVE.equalsIgnoreCase(exRate.getStatus())) {
							exchangeRate = exRate.getExchangeRate();
							break;
						}
					}
				}
			}
			return exchangeRate;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertInvestorMarginWithdrawPA(UserInfoDTO userInfo, MarginTransactionDTO marginTransDto,
			long refId) {
		String methodName = "insertInvestorMarginWithdrawPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());
			nestedObjInfo.setMemberCode(userInfo.getMemberCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.INVESTOR_WITHDRAW_MONEY);
			pendingData.setCollectionName("investors");
			pendingData.setQueryField("investorCode");
			pendingData.setQueryValue(marginTransDto.getInvestorCode());
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setPendingValue(new Gson().toJson(marginTransDto));

			InvestorMarginTransApproval marginTransApproval = new InvestorMarginTransApproval();
			marginTransApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_ACCOUNT_TRANS_URL, approvalId));
			marginTransApproval.setFunctionCode(SystemFunctionCode.APPROVAL_INVESTOR_MARGIN_WITHDRAWAL_CODE);
			marginTransApproval.setFunctionName(SystemFunctionCode.INVESTOR_MARGIN_WITHDRAWAL_NAME);
			marginTransApproval.setDescription(SystemFunctionCode
					.getApprovalDescription(marginTransApproval.getFunctionName(), marginTransDto.getInvestorCode()));
			marginTransApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
			marginTransApproval.setNestedObjInfo(nestedObjInfo);
			marginTransApproval.setPendingData(pendingData);
			approvalId = invMarginTransApprovalRepo.save(marginTransApproval).getId();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return approvalId;
	}

	public BasePagination<InvestorMarginTransaction> listMarginTransactions(HttpServletRequest request, long refId) {
		String methodName = "listMarginTransactions";
		BasePagination<InvestorMarginTransaction> pagination = null;
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);

			List<? extends Bson> pipeline = Arrays
					.asList(new Document().append("$match", getQueryDocument(searchCriteria, userInfo)),
							new Document().append("$sort", searchCriteria.getSort()),
							new Document().append("$project", new Document()
									.append("_id", new Document().append("$toString", "$_id")).append("memberCode", 1.0)
									.append("memberName", 1.0).append("brokerCode", 1.0).append("brokerName", 1.0)
									.append("collaboratorCode", 1.0).append("collaboratorName", 1.0)
									.append("investorCode", 1.0).append("investorName", 1.0)
									.append("transactionType", 1.0).append("amount", 1.0).append("currency", 1.0)
									.append("approvalUser", 1.0).append("approvalDate", 1.0).append("note", 1.0)),
							new Document().append("$facet", new Document()
									.append("stage1", Arrays.asList(new Document().append("$count", "total")))
									.append("stage2",
											Arrays.asList(new Document().append("$skip", searchCriteria.getSkip()),
													new Document().append("$limit", searchCriteria.getLimit())))),
							new Document().append("$unwind", new Document().append("path", "$stage1")),
							new Document().append("$project",
									new Document().append("count", "$stage1.total").append("data", "$stage2")));

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investor_margin_trans");
			Document resultDoc = collection.aggregate(pipeline).first();
			pagination = mongoTemplate.getConverter().read(BasePagination.class, resultDoc);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return pagination;
	}

	public List<MarginTransCSV> listMarginTransactionsCsv(HttpServletRequest request, long refId) {
		String methodName = "listMarginTransactionsCsv";
		List<MarginTransCSV> marginTrans = new ArrayList<MarginTransCSV>();
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);

			List<? extends Bson> pipeline = Arrays.asList(
					new Document().append("$match", getQueryDocument(searchCriteria, userInfo)),
					new Document().append("$sort", searchCriteria.getSort()),
					new Document().append("$project",
							new Document().append("_id", new Document().append("$toString", "$_id"))
									.append("memberCode", 1.0).append("memberName", 1.0).append("brokerCode", 1.0)
									.append("brokerName", 1.0).append("collaboratorCode", 1.0)
									.append("collaboratorName", 1.0).append("investorCode", 1.0)
									.append("investorName", 1.0).append("transactionType", 1.0).append("amount", 1.0)
									.append("currency", 1.0).append("approvalUser", 1.0)
									.append("approvalDate",
											new Document().append("$dateToString",
													new Document().append("format", "%d/%m/%Y %H:%M:%S").append("date",
															new Document().append("$toDate", "$createdDate"))))
									.append("note", 1.0)));

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investor_margin_trans");
			MongoCursor<Document> cur = collection.aggregate(pipeline).iterator();
			while (cur.hasNext()) {
				MarginTransCSV marginTransCsv = mongoTemplate.getConverter().read(MarginTransCSV.class, cur.next());
				if (marginTransCsv != null)
					marginTrans.add(marginTransCsv);
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return marginTrans;
	}

	public void refundDepositMargin(HttpServletRequest request, InvestorMarginTransApproval pendingApproval,
			long refId) {
		String methodName = "refundDepositMargin";
		try {
			String investorCode = pendingApproval.getPendingData().getQueryValue();
			MarginTransactionDTO marginTransDto = new Gson()
					.fromJson(pendingApproval.getPendingData().getPendingValue(), MarginTransactionDTO.class);

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_REFUND_INVESTOR_DEPOSIT_MONEY,
					ActivityLogService.ACTIVITY_APPROVAL_REFUND_INVESTOR_DEPOSIT_MONEY_DESC, investorCode,
					pendingApproval.getId());
			
			if (Utility.isCQGSyncOn()) {
				// update cqg balance
				InvestorDTO investorDto = getInvestorInfo(investorCode, refId);
				double exchangeRate = getExchangeRate(refId);
				double usdChangedAmt = - Precision.round(marginTransDto.getAmount()/exchangeRate, 2);
				boolean result = cqgService.updateCQGAccountBalance(investorCode,
						investorDto.getCqgInfo().getAccountId(), usdChangedAmt, refId);
				if (!result) {
					throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
				}
			}

			// update changedAmount in investor_margin_info
			InvestorMarginInfo marginInfo = marginInfoService.getInvestorMarginInfo(investorCode, refId);
			double changedAmount = marginInfo.getChangedAmount() - marginTransDto.getAmount();

			marginInfoService.updateChangedAmount(investorCode, changedAmount, refId);

			// update investor_margin_trans
			InvestorMarginTransaction marginTrans = modelMapper.map(marginTransDto, InvestorMarginTransaction.class);
			marginTrans.setCurrency(Constant.CURRENCY_VND);
			marginTrans.setApprovalDate(System.currentTimeMillis());
			marginTrans.setApprovalUser(userInfo.getUsername());
			invMarginTransRepo.save(marginTrans);

		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void refundDepositMarginPA(HttpServletRequest request, String depositApprovalId, long refId) {
		String methodName = "refundDepositMarginPA";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to inv_margin_trans_approvals
			String approvalId = insertRefundInvestorMarginDepositPA(userInfo, depositApprovalId, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_REFUND_INVESTOR_DEPOSIT_MONEY,
					ActivityLogService.ACTIVITY_REFUND_INVESTOR_DEPOSIT_MONEY_DESC, "", approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertRefundInvestorMarginDepositPA(UserInfoDTO userInfo, String depositApprovalId, long refId) {
		String methodName = "insertRefundInvestorMarginDepositPA";
		String approvalId = "";
		try {
			InvestorMarginTransApproval depositApproval = invMarginTransApprovalRepo.findById(depositApprovalId).get();
			MarginTransactionDTO depositTransDto = new Gson()
					.fromJson(depositApproval.getPendingData().getPendingValue(), MarginTransactionDTO.class);
			MarginTransactionDTO refundTransDto = depositTransDto;
			refundTransDto.setTransactionType(Constant.MARGIN_TRANS_TYPE_REFUND);

			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());
			nestedObjInfo.setMemberCode(userInfo.getMemberCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.INVESTOR_REFUND_DEPOSIT_MONEY);
			pendingData.setCollectionName("investors");
			pendingData.setQueryField("investorCode");
			pendingData.setQueryValue(refundTransDto.getInvestorCode());
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setPendingValue(new Gson().toJson(refundTransDto));

			InvestorMarginTransApproval marginTransApproval = new InvestorMarginTransApproval();
			marginTransApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_ACCOUNT_TRANS_URL, approvalId));
			marginTransApproval.setFunctionCode(SystemFunctionCode.APPROVAL_REFUND_INVESTOR_MARGIN_DEPOSIT_CODE);
			marginTransApproval.setFunctionName(SystemFunctionCode.REFUND_INVESTOR_MARGIN_DEPOSIT_NAME);
			marginTransApproval.setDescription(SystemFunctionCode
					.getApprovalDescription(marginTransApproval.getFunctionName(), refundTransDto.getInvestorCode()));
			marginTransApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
			marginTransApproval.setNestedObjInfo(nestedObjInfo);
			marginTransApproval.setPendingData(pendingData);
			approvalId = invMarginTransApprovalRepo.save(marginTransApproval).getId();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return approvalId;
	}
}
