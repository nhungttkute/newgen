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
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.ChangeGroupDTO;
import com.newgen.am.dto.CommoditiesDTO;
import com.newgen.am.dto.EmailDTO;
import com.newgen.am.dto.GeneralFeeDTO;
import com.newgen.am.dto.AccountStatusDTO;
import com.newgen.am.dto.InvestorCSV;
import com.newgen.am.dto.InvestorDTO;
import com.newgen.am.dto.MarginInfoDTO;
import com.newgen.am.dto.MarginMultiplierDTO;
import com.newgen.am.dto.MarginRatioAlertDTO;
import com.newgen.am.dto.MarginTransactionDTO;
import com.newgen.am.dto.OtherFeeDTO;
import com.newgen.am.dto.RiskParametersDTO;
import com.newgen.am.dto.UpdateInvestorDTO;
import com.newgen.am.dto.UserCSV;
import com.newgen.am.dto.UserDTO;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.Commodity;
import com.newgen.am.model.Investor;
import com.newgen.am.model.InvestorMarginTransApproval;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.model.LoginInvestorUser;
import com.newgen.am.model.NestedObjectInfo;
import com.newgen.am.model.PendingApproval;
import com.newgen.am.model.PendingData;
import com.newgen.am.model.SystemRole;
import com.newgen.am.repository.BrokerRepository;
import com.newgen.am.repository.CollaboratorRepository;
import com.newgen.am.repository.InvestorMarginTransApprovalRepository;
import com.newgen.am.repository.InvestorRepository;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.LoginInvestorUserRepository;
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
    private BrokerRepository brokerRepo;
    
    @Autowired
    private CollaboratorRepository collaboratorRepo;
    
    @Autowired
	private PendingApprovalRepository pendingApprovalRepo;
    
    @Autowired
    private InvestorMarginTransApprovalRepository invMarginTransApprovalRepo;
    
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
	PasswordEncoder passwordEncoder;

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
                investorAccDto.setSodBalance(investor.getAccount().getSodBalance());
                investorAccDto.setChangedAmount(investor.getAccount().getChangedAmount());
                investorAccDto.setGeneralFee(investor.getGeneralFee());

                // caculate some fields
                calculateCurrentBalance(investorAccDto, investor.getOtherFee());
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
        investorAccDto.setAvailableMargin(Utility.getLong(investorAccDto.getNetMargin()) - Utility.getLong(investorAccDto.getInitialRequiredMargin()));
    }

    private void calculateNetMargin(AccountStatusDTO investorAccDto) {
        investorAccDto.setNetMargin(Utility.getLong(investorAccDto.getCurrentBalance()) + Utility.getLong(investorAccDto.getEstimatedProfitVND()));
    }

    private void calculateCurrentBalance(AccountStatusDTO investorAccDto, Long otherFee) {
        long currentBalance = Utility.getLong(investorAccDto.getSodBalance()) + Utility.getLong(investorAccDto.getChangedAmount())
                - Utility.getLong(investorAccDto.getTransactionFee()) - Utility.getLong(otherFee)
                - Utility.getLong(investorAccDto.getGeneralFee()) + Utility.getLong(investorAccDto.getActualProfitVND());
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
    		if (user == null ) {
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
    
    public BasePagination<InvestorDTO> list(HttpServletRequest request, long refId) {
		String methodName = "list";
		BasePagination<InvestorDTO> pagination = null;
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", searchCriteria.getQuery()), 
                    new Document()
                            .append("$sort", searchCriteria.getSort()), 
                    new Document()
                            .append("$project", new Document()
                            		.append("_id", new Document().append("$toString", "$_id"))
                            		.append("memberCode", 1.0)
                            		.append("memberName", 1.0)
                            		.append("brokerCode", 1.0)
                            		.append("brokerName", 1.0)
                            		.append("collaboratorCode", 1.0)
                            		.append("collaboratorName", 1.0)
                                    .append("investorCode", 1.0)
                                    .append("investorName", 1.0)
                                    .append("status", 1.0)
                                    .append("note", 1.0)
                                    .append("createdDate", 1.0)
                            ), 
                    new Document()
                            .append("$facet", new Document()
                                    .append("stage1", Arrays.asList(
                                            new Document()
                                                    .append("$count", "total")
                                        )
                                    )
                                    .append("stage2", Arrays.asList(
                                            new Document()
                                                    .append("$skip", searchCriteria.getSkip()),
                                            new Document()
                                                    .append("$limit", searchCriteria.getLimit())
                                        )
                                    )
                            ), 
                    new Document()
                            .append("$unwind", new Document()
                                    .append("path", "$stage1")
                            ), 
                    new Document()
                            .append("$project", new Document()
                                    .append("count", "$stage1.total")
                                    .append("data", "$stage2")
                            )
            );
			
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

			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", searchCriteria.getQuery()), 
                    new Document()
                            .append("$sort", searchCriteria.getSort()), 
                    new Document()
                            .append("$project", new Document()
                            		.append("_id", new Document().append("$toString", "$_id"))
                            		.append("memberCode", 1.0)
                            		.append("memberName", 1.0)
                            		.append("brokerCode", 1.0)
                            		.append("brokerName", 1.0)
                            		.append("collaboratorCode", 1.0)
                            		.append("collaboratorName", 1.0)
                                    .append("investorCode", 1.0)
                                    .append("investorName", 1.0)
                                    .append("status", 1.0)
                                    .append("note", 1.0)
                                    .append("createdDate", new Document()
                                            .append("$dateToString", new Document()
                                                    .append("format", "%d/%m/%Y %H:%M:%S")
                                                    .append("date", new Document()
                                                            .append("$toDate", "$createdDate")
                                                    )
                                            )
                                    )
                            ));
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
	
	public void createInvestor(HttpServletRequest request, InvestorDTO investorDto, long refId) {
		String methodName = "createInvestor";
		boolean existedInvestor= false;
		try {
			existedInvestor = investorRepo.existsInvestorByInvestorCode(investorDto.getInvestorCode());
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!existedInvestor) {
			try {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertInvestorCreatePA(userInfo, investorDto, refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_INVESTOR,
						ActivityLogService.ACTIVITY_CREATE_INVESTOR_DESC, investorDto.getInvestorCode(), approvalId);
				
				Document company = null;
				Document individual = null;
				Document contact = null;
				
				if (Utility.isInvestorCompany(investorDto.getType())) {
					if (Utility.isNull(investorDto.getCompany())) {
						throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
					}
					
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
					if (Utility.isNull(investorDto.getIndividual())) {
						throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
					}
					
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
				} else {
					throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
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
				newInvestor.append("status", investorDto.getStatus());
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
				
				// insert new investor
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("investors");
				collection.insertOne(newInvestor);
				
				// insert new investor's user
				createDefaultInvestorUser(request, investorDto, investorRole, refId);
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
	
	public String insertInvestorCreatePA(UserInfoDTO userInfo, InvestorDTO investorDto, long refId) {
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
			pendingData.setValue(new Gson().toJson(investorDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setId(approvalId);
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_INVESTOR_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.INVESTOR_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), investorDto.getInvestorCode()));
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
	
	public void createDefaultInvestorUser(HttpServletRequest request, InvestorDTO investorDto, Document investorRole, long refId) {
		String methodName = "createInvestorUser";
		boolean existedUser = false;
		String username = Constant.INVESTOR_USER_PREFIX + investorDto.getInvestorCode();
		try {
			existedUser = loginInvUserRepo.existsByUsername(username);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!existedUser) {
			try {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request,
						ActivityLogService.ACTIVITY_CREATE_INVESTOR_USER,
						ActivityLogService.ACTIVITY_CREATE_INVESTOR_USER_DESC, username, "");

				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("investors");

				String fullName = "";
				String email = "";
				String phoneNumber = "";
				if (Utility.isNotNull(investorDto.getCompany()) && Utility.isNotNull(investorDto.getCompany().getDelegate())) {
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
				investorUser.append("status", Constant.STATUS_ACTIVE);
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
				UserDTO investorUserDto = new UserDTO();
				investorUserDto.setMemberCode(investorDto.getMemberCode());
				investorUserDto.setBrokerCode(investorDto.getBrokerCode());
				investorUserDto.setCollaboratorCode(investorDto.getCollaboratorCode());
				investorUserDto.setUsername(username);
				investorUserDto.setFullName(fullName);
				investorUserDto.setEmail(email);
				investorUserDto.setPhoneNumber(phoneNumber);
				
				String password = Utility.generateRandomPassword();
				String pin = Utility.generateRandomPin();
				createLoginInvestorUser(investorDto.getInvestorCode(), investorUserDto, password, pin, refId);

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
	
	private LoginInvestorUser createLoginInvestorUser(String investorCode, UserDTO investorUserDto, String password, String pin,
			long refId) {
		String methodName = "createLoginInvestorUser";
		try {
			LoginInvestorUser loginInvUser = modelMapper.map(investorUserDto, LoginInvestorUser.class);
			loginInvUser.setInvestorCode(investorCode);
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
			email.setTo(toEmail);
			email.setSubject(FileUtility.CREATE_NEW_USER_EMAIL_SUBJECT);

			String emailBody = String.format(
					FileUtility.loadFileContent(
							ConfigLoader.getMainConfig().getString(FileUtility.CREATE_NEW_USER_EMAIL_FILE), refId),
					username, password, pin);
			email.setBodyStr(emailBody);
			String emailJson = new Gson().toJson(email);
			AMLogger.logMessage(className, methodName, refId, "Email: " + emailJson);
			serviceCon.sendPostRequest(serviceCon.getEmailNotificationServiceURL(), emailJson);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void updateInvestor(HttpServletRequest request, String investorCode, UpdateInvestorDTO investorDto, long refId) {
		String methodName = "updateInvestor";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertInvestorUpdatePA(userInfo, investorCode, investorDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_INVESTOR,
					ActivityLogService.ACTIVITY_UPDATE_INVESTOR_DESC, investorCode, approvalId);

			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");
			
			BasicDBObject updateInvestor = new BasicDBObject();
			boolean isUserUpdated = false;
			
			if (Utility.isNotNull(investorDto.getInvestorName())) updateInvestor.append("investorName", investorDto.getInvestorName());
			if (Utility.isNotNull(investorDto.getStatus())) updateInvestor.append("status", investorDto.getStatus());
			if (Utility.isNotNull(investorDto.getNote())) {
				updateInvestor.append("note", investorDto.getNote());
				updateInvestor.append("users.$.note", investorDto.getNote());
				isUserUpdated = true;
			}
			
			
			if (Utility.isNotNull(investorDto.getCompany())) {
				if (Utility.isNotNull(investorDto.getCompany().getName())) updateInvestor.append("company.name", investorDto.getCompany().getName());
				if (Utility.isNotNull(investorDto.getCompany().getTaxCode())) updateInvestor.append("company.taxCode", investorDto.getCompany().getTaxCode());
				if (Utility.isNotNull(investorDto.getCompany().getAddress())) updateInvestor.append("company.address", investorDto.getCompany().getAddress());
				if (Utility.isNotNull(investorDto.getCompany().getPhoneNumber())) updateInvestor.append("company.phoneNumber", investorDto.getCompany().getPhoneNumber());
				if (Utility.isNotNull(investorDto.getCompany().getFax())) updateInvestor.append("company.fax", investorDto.getCompany().getFax());
				if (Utility.isNotNull(investorDto.getCompany().getEmail())) updateInvestor.append("company.email", investorDto.getCompany().getEmail());
				
				if (Utility.isNotNull(investorDto.getCompany().getDelegate())) {
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getFullName())) {
						updateInvestor.append("company.delegate.fullName", investorDto.getCompany().getDelegate().getFullName());
						updateInvestor.append("users.$.fullName", investorDto.getCompany().getDelegate().getFullName());
						updateInvestor.append("contact.fullName", investorDto.getCompany().getDelegate().getFullName());
						isUserUpdated = true;
					}
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getBirthDay())) updateInvestor.append("company.delegate.birthDay", investorDto.getCompany().getDelegate().getBirthDay());
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getIdentityCard())) updateInvestor.append("company.delegate.identityCard", investorDto.getCompany().getDelegate().getIdentityCard());
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getIdCreatedDate()))  updateInvestor.append("company.delegate.idCreatedDate", investorDto.getCompany().getDelegate().getIdCreatedDate());
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getIdCreatedLocation()))  updateInvestor.append("company.delegate.idCreatedLocation", investorDto.getCompany().getDelegate().getIdCreatedLocation());
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getEmail()))  {
						updateInvestor.append("company.delegate.email", investorDto.getCompany().getDelegate().getEmail());
						updateInvestor.append("users.$.email", investorDto.getCompany().getDelegate().getEmail());
						updateInvestor.append("contact.email", investorDto.getCompany().getDelegate().getEmail());
						isUserUpdated = true;
					}
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getPhoneNumber()))  {
						updateInvestor.append("company.delegate.phoneNumber", investorDto.getCompany().getDelegate().getPhoneNumber());
						updateInvestor.append("users.$.phoneNumber", investorDto.getCompany().getDelegate().getPhoneNumber());
						updateInvestor.append("contact.phoneNumber", investorDto.getCompany().getDelegate().getPhoneNumber());
						isUserUpdated = true;
					}
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getAddress()))  updateInvestor.append("company.delegate.address", investorDto.getCompany().getDelegate().getAddress());
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getScannedFrontIdCard()))  updateInvestor.append("company.delegate.scannedFrontIdCard", investorDto.getCompany().getDelegate().getScannedFrontIdCard());
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getScannedBackIdCard()))  updateInvestor.append("company.delegate.scannedBackIdCard", investorDto.getCompany().getDelegate().getScannedBackIdCard());
					if (Utility.isNotNull(investorDto.getCompany().getDelegate().getScannedSignature()))  updateInvestor.append("company.delegate.scannedSignature", investorDto.getCompany().getDelegate().getScannedSignature());
				}
			}
			
			if (Utility.isNotNull(investorDto.getIndividual())) {
				if (Utility.isNotNull(investorDto.getIndividual().getFullName())) {
					updateInvestor.append("individual.fullName", investorDto.getIndividual().getFullName());
					updateInvestor.append("users.$.fullName", investorDto.getIndividual().getFullName());
					updateInvestor.append("contact.fullName", investorDto.getIndividual().getFullName());
					isUserUpdated = true;
				}
				if (Utility.isNotNull(investorDto.getIndividual().getBirthDay())) updateInvestor.append("individual.birthDay", investorDto.getIndividual().getBirthDay());
				if (Utility.isNotNull(investorDto.getIndividual().getIdentityCard())) updateInvestor.append("individual.identityCard", investorDto.getIndividual().getIdentityCard());
				if (Utility.isNotNull(investorDto.getIndividual().getIdCreatedDate()))  updateInvestor.append("individual.idCreatedDate", investorDto.getIndividual().getIdCreatedDate());
				if (Utility.isNotNull(investorDto.getIndividual().getIdCreatedLocation()))  updateInvestor.append("individual.idCreatedLocation", investorDto.getIndividual().getIdCreatedLocation());
				if (Utility.isNotNull(investorDto.getIndividual().getEmail()))  {
					updateInvestor.append("individual.email", investorDto.getIndividual().getEmail());
					updateInvestor.append("users.$.email", investorDto.getIndividual().getEmail());
					updateInvestor.append("contact.email", investorDto.getIndividual().getEmail());
					isUserUpdated = true;
				}
				if (Utility.isNotNull(investorDto.getIndividual().getPhoneNumber()))  {
					updateInvestor.append("individual.phoneNumber", investorDto.getIndividual().getPhoneNumber());
					updateInvestor.append("users.$.phoneNumber", investorDto.getIndividual().getPhoneNumber());
					updateInvestor.append("contact.phoneNumber", investorDto.getIndividual().getPhoneNumber());
					isUserUpdated = true;
				}
				if (Utility.isNotNull(investorDto.getIndividual().getAddress()))  updateInvestor.append("individual.address", investorDto.getIndividual().getAddress());
				if (Utility.isNotNull(investorDto.getIndividual().getScannedFrontIdCard()))  updateInvestor.append("individual.scannedFrontIdCard", investorDto.getIndividual().getScannedFrontIdCard());
				if (Utility.isNotNull(investorDto.getIndividual().getScannedBackIdCard()))  updateInvestor.append("individual.scannedBackIdCard", investorDto.getIndividual().getScannedBackIdCard());
				if (Utility.isNotNull(investorDto.getIndividual().getScannedSignature()))  updateInvestor.append("individual.scannedSignature", investorDto.getIndividual().getScannedSignature());
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
				query.append("code", investorCode);
				query.append("users.username", Constant.INVESTOR_USER_PREFIX + investorCode);
				
				BasicDBObject update = new BasicDBObject();
				update.append("$set", updateInvestor);
				
				collection.updateOne(query, update);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public String insertInvestorUpdatePA(UserInfoDTO userInfo, String investorCode, UpdateInvestorDTO investorDto, long refId) {
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
			pendingData.setValue(new Gson().toJson(investorDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_INVESTOR_UPDATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.INVESTOR_UPDATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(),
					investorCode));
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
			MarginInfoDTO marginInfo = new MarginInfoDTO();
			marginInfo.setInvestorCode(investor.getInvestorCode());
			marginInfo.setInvestorName(investor.getInvestorName());
			if (investor.getAccount() != null) {
				marginInfo.setCurrency(investor.getAccount().getCurrency());
				marginInfo.setMarginDeficitInterestRate(investor.getAccount().getMarginDeficitInterestRate());
				marginInfo.setMarginSurplusInterestRate(investor.getAccount().getMarginSurplusInterestRate());
				marginInfo.setAvailableBalance(investor.getAccount().getSodBalance() - investor.getAccount().getChangedAmount());
			}
			
			InvestorDTO investorDto = modelMapper.map(investor, InvestorDTO.class);
			investorDto.setAccount(marginInfo);
			return investorDto;
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

			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", query1), 
                    new Document()
                            .append("$unwind", new Document()
                                    .append("path", "$users")
                            ), 
                    new Document()
                            .append("$match", searchCriteria.getQuery()), 
                    new Document()
                            .append("$sort", searchCriteria.getSort()), 
                    new Document()
                            .append("$project", new Document()
                                    .append("_id", new Document()
                                            .append("$toString", "$users._id")
                                    )
                                    .append("username", "$users.username")
                                    .append("fullName", "$users.fullName")
                                    .append("email", "$users.email")
                                    .append("phoneNumber", "$users.phoneNumber")
                                    .append("status", "$users.status")
                                    .append("isPasswordExpiryCheck", "$users.isPasswordExpiryCheck")
                                    .append("passwordExpiryDays", "$users.passwordExpiryDays")
                                    .append("expiryAlertDays", "$users.expiryAlertDays")
                                    .append("createdDate", "$users.createdDate")
                            ), 
                    new Document()
                            .append("$facet", new Document()
                                    .append("stage1", Arrays.asList(
                                            new Document()
                                                    .append("$count", "total")
                                        )
                                    )
                                    .append("stage2", Arrays.asList(
                                            new Document()
                                                    .append("$skip", searchCriteria.getSkip()),
                                            new Document()
                                                    .append("$limit", searchCriteria.getLimit())
                                        )
                                    )
                            ), 
                    new Document()
                            .append("$unwind", new Document()
                                    .append("path", "$stage1")
                            ), 
                    new Document()
                            .append("$project", new Document()
                                    .append("count", "$stage1.total")
                                    .append("data", "$stage2")
                            )
            );

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

			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", query), 
                    new Document()
                            .append("$unwind", new Document()
                                    .append("path", "$users")
                            ), 
                    new Document()
                            .append("$match", searchCriteria.getQuery()), 
                    new Document()
                            .append("$sort", searchCriteria.getSort()), 
                    new Document()
                            .append("$project", new Document()
                                    .append("_id", new Document()
                                            .append("$toString", "$users._id")
                                    )
                                    .append("username", "$users.username")
                                    .append("fullName", "$users.fullName")
                                    .append("email", "$users.email")
                                    .append("phoneNumber", "$users.phoneNumber")
                                    .append("status", "$users.status")
                                    .append("isPasswordExpiryCheck", "$users.isPasswordExpiryCheck")
                                    .append("passwordExpiryDays", "$users.passwordExpiryDays")
                                    .append("expiryAlertDays", "$users.expiryAlertDays")
                                    .append("createdDate", new Document().append("$dateToString",
											new Document().append("format", "%d/%m/%Y %H:%M:%S").append("date",
													new Document().append("$toDate", "$users.createdDate"))))
                            )
            );

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
	
	public void createInvestorUser(HttpServletRequest request, String investorCode, UserDTO userDto, long refId) {
		String methodName = "createInvestorUser";
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
						ActivityLogService.ACTIVITY_CREATE_INVESTOR_USER2_DESC, userDto.getUsername(), investorCode, approvalId);

				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("investors");

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

				BasicDBObject query = new BasicDBObject();
				query.append("investorCode", investorCode);

				collection.updateOne(query, Updates.addToSet("users", newUser));

				// insert loginAdminUser
				String password = Utility.generateRandomPassword();
				String pin = Utility.generateRandomPin();
				LoginInvestorUser newLoginInvUser = createLoginInvestorUser(investorCode, userDto, password, pin, refId);

				// send email
				sendCreateNewUserEmail(userDto.getEmail(), newLoginInvUser.getUsername(), password, pin, refId);
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "This username already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		}
	}
	
	public String insertInvestorUserCreatePA(UserInfoDTO userInfo, String investorCode, UserDTO userDto,
			long refId) {
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
			pendingData.setValue(new Gson().toJson(userDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_INVESTOR_USER_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.INVESTOR_USER_CREATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.INVESTOR_USER_CREATE_DESC, userDto.getUsername(),
					investorCode));
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
	
	public void createDefaultSetting(HttpServletRequest request, String investorCode, UpdateInvestorDTO investorDto, long refId) {
		String methodName = "createDefaultSetting";
		boolean needUpdateCommodities = false;
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_INVESTOR_DEFAULT_SETTING,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_DEFAULT_SETTING_DESC, investorCode, "");

			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			Document updateDocument = new Document();
			if (investorDto.getOrderLimit() > 0) {
				updateDocument.append("orderLimit", investorDto.getOrderLimit());
			}
			if (investorDto.getDefaultPositionLimit() > 0) {
				needUpdateCommodities = true;
				updateDocument.append("defaultPositionLimit", investorDto.getDefaultPositionLimit());
			}
			if (investorDto.getDefaultCommodityFee() > 0) {
				needUpdateCommodities = true;
				updateDocument.append("defaultCommodityFee", investorDto.getDefaultCommodityFee());
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
				
				if (needUpdateCommodities) {
					// update default position litmit and fee for all commodities
					List<Document> newCommodities = new ArrayList<Document>();
					
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
			            	if (investorDto.getDefaultCommodityFee() > 0) {
			            		newComm.append("commodityFee", investorDto.getDefaultCommodityFee());
			            	} else {
			            		newComm.append("commodityFee", comm.getCommodityFee());
			            	}
			            	if (Constant.POSITION_INHERITED.equalsIgnoreCase(comm.getPositionLimitType())) {
			            		if (investorDto.getDefaultPositionLimit() > 0) {
			            			newComm.append("positionLimitType", Constant.POSITION_INHERITED);
			            			newComm.append("positionLimit", investorDto.getDefaultPositionLimit());
			            		} else {
			            			newComm.append("positionLimitType", Constant.POSITION_INHERITED);
			            			newComm.append("positionLimit", comm.getPositionLimit());
			            		}
			            	} else {
			            		newComm.append("positionLimitType", comm.getPositionLimitType());
		            			newComm.append("positionLimit", comm.getPositionLimit());
			            	}
			            	newCommodities.add(newComm);
			            }
		            }
		            
		            updateDocument.append("commodities", newCommodities);
				}
				
	            
	            Document update = new Document();
				update.append("$set", updateDocument);
				
				collection.updateOne(query, update);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void createInvestorCommodities(HttpServletRequest request, String investorCode, CommoditiesDTO commoditiesDto, long refId) {
		String methodName = "createInvestorCommodities";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_COMMODITIES_ASSIGN,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_COMMODITIES_ASSIGN_DESC, investorCode,
					"");

			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			List<Document> commodities = new ArrayList<Document>();
			
			for (Commodity comm : commoditiesDto.getCommodities()) {
				Document commDoc = new Document();
				commDoc.append("commodityCode", comm.getCommodityCode());
				commDoc.append("commodityName", comm.getCommodityName());
				commDoc.append("commodityFee", comm.getCommodityFee());
				commDoc.append("positionLimitType", comm.getPositionLimitType());
				commDoc.append("positionLimit", comm.getPositionLimit());
				commDoc.append("currency", Constant.CURRENCY_VND);
				commodities.add(commDoc);
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
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void setInvestorNewPositionOrderLock(HttpServletRequest request, String investorCode, RiskParametersDTO riskParamDto, long refId) {
		String methodName = "setInvestorNewPositionOrderLock";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertInvestorNewPositionOrderLockPA(userInfo, investorCode, riskParamDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_INVESTOR_RISK_NEW_POSITION_ORDER_LOCK,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_RISK_NEW_POSITION_ORDER_LOCK_DESC, investorCode, approvalId);

			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			Document updateDocument = new Document();
			updateDocument.append("riskParameters.newPositionOrderLock", riskParamDto.getRiskParameters().getNewPositionOrderLock());
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
	
	public String insertInvestorNewPositionOrderLockPA(UserInfoDTO userInfo, String investorCode, RiskParametersDTO riskParamDto,
			long refId) {
		String methodName = "insertInvestorNewPositionOrderLockPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.INVESTOR_RISK_NEW_POSITION_LOCK_SET);
			pendingData.setCollectionName("investors");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setQueryField("investorCode");
			pendingData.setQueryValue(investorCode);
			pendingData.setValue(new Gson().toJson(riskParamDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
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
	
	public void setInvestorOrderLock(HttpServletRequest request, String investorCode, RiskParametersDTO riskParamDto, long refId) {
		String methodName = "setInvestorOrderLock";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertInvestorOrderLockPA(userInfo, investorCode, riskParamDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_INVESTOR_RISK_ORDER_LOCK,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_RISK_ORDER_LOCK_DESC, investorCode, approvalId);

			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
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
	
	public String insertInvestorOrderLockPA(UserInfoDTO userInfo, String investorCode, RiskParametersDTO riskParamDto,
			long refId) {
		String methodName = "insertInvestorOrderLockPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.INVESTOR_RISK_ORDER_LOCK_SET);
			pendingData.setCollectionName("investors");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setQueryField("investorCode");
			pendingData.setQueryValue(investorCode);
			pendingData.setValue(new Gson().toJson(riskParamDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
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
	
	public void setMarginMultiplier(HttpServletRequest request, String investorCode, MarginMultiplierDTO marginMultDto, long refId) {
		String methodName = "setMarginMultiplier";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_INVESTOR_MARGIN_MULTIPLIER,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_MARGIN_MULTIPLIER_DESC, investorCode, "");

			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
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
	
	public void setMarginRatioAlert(HttpServletRequest request, String investorCode, MarginRatioAlertDTO marginRatioAlertDto, long refId) {
		String methodName = "setMarginRatioAlert";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_INVESTOR_MARGIN_RATIO,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_MARGIN_RATIO_DESC, investorCode, "");

			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			boolean isValidRatio = true;
			if (marginRatioAlertDto.getMarginRatioAlert().getFinalizationRatio() > marginRatioAlertDto.getMarginRatioAlert().getCancelOrderRatio() || (marginRatioAlertDto.getMarginRatioAlert().getFinalizationRatio() > marginRatioAlertDto.getMarginRatioAlert().getWarningRatio())) {
				isValidRatio = false;
			} else if (marginRatioAlertDto.getMarginRatioAlert().getCancelOrderRatio() > marginRatioAlertDto.getMarginRatioAlert().getWarningRatio()) {
				isValidRatio = false;
			}
			 
			if (!isValidRatio) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			}
			
			Document marginRatioAlert = new Document();
			marginRatioAlert.append("warningRatio", marginRatioAlertDto.getMarginRatioAlert().getWarningRatio());
			marginRatioAlert.append("cancelOrderRatio", marginRatioAlertDto.getMarginRatioAlert().getCancelOrderRatio());
			marginRatioAlert.append("finalizationRatio", marginRatioAlertDto.getMarginRatioAlert().getFinalizationRatio());
			
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
	
	public void setGeneralFee(HttpServletRequest request, String investorCode, GeneralFeeDTO generalFeeDto, long refId) {
		String methodName = "setGeneralFee";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_INVESTOR_GENERAL_FEE,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_GENERAL_FEE_DESC, investorCode, "");

			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			Document updateDocument = new Document();
			updateDocument.append("generalFee", generalFeeDto.getGeneralFee());
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
	
	public void setOtherFee(HttpServletRequest request, String investorCode, OtherFeeDTO otherFeeDto, long refId) {
		String methodName = "setOtherFee";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_INVESTOR_OTHER_FEE,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_OTHER_FEE_DESC, investorCode, "");

			if (!investorRepo.existsInvestorByInvestorCode(investorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			Document updateDocument = new Document();
			updateDocument.append("otherFee", otherFeeDto.getOtherFee());
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
	
	public void changeBroker(HttpServletRequest request, ChangeGroupDTO changeGroupDto, String investorCode,
			long refId) {
		String methodName = "changeBroker";
		if (!brokerRepo.existsBrokerByCode(changeGroupDto.getGroupCode())) {
			throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
		}
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertChangeBrokerPA(userInfo, changeGroupDto.getGroupCode(), investorCode, refId);
			// send activity log
			activityLogService.sendActivityLog2(userInfo, request, ActivityLogService.ACTIVITY_INVESTOR_BROKER_CHANGE,
					ActivityLogService.ACTIVITY_INVESTOR_BROKER_CHANGE_DESC, investorCode, changeGroupDto.getGroupCode(), approvalId);

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

	public String insertChangeBrokerPA(UserInfoDTO userInfo, String toBrokerCode,
			String investorCode, long refId) {
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
			pendingData.setValue(toBrokerCode);

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_INVESTOR_BROKER_CHANGE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.INVESTOR_BROKER_CHANGE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.INVESTOR_BROKER_CHANGE_DESC, investorCode, toBrokerCode));
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
	
	public void changeCollaborator(HttpServletRequest request, ChangeGroupDTO changeGroupDto, String investorCode,
			long refId) {
		String methodName = "changeCollaborator";
		if (!collaboratorRepo.existsCollaboratorByCode(changeGroupDto.getGroupCode())) {
			throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
		}
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertChangeCollaboratorPA(userInfo, changeGroupDto.getGroupCode(), investorCode, refId);
			// send activity log
			activityLogService.sendActivityLog2(userInfo, request, ActivityLogService.ACTIVITY_INVESTOR_BROKER_CHANGE,
					ActivityLogService.ACTIVITY_INVESTOR_BROKER_CHANGE_DESC, investorCode, changeGroupDto.getGroupCode(), approvalId);

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

	public String insertChangeCollaboratorPA(UserInfoDTO userInfo, String toCollaboratorCode,
			String investorCode, long refId) {
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
			pendingData.setValue(toCollaboratorCode);

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_INVESTOR_COLLABORATOR_CHANGE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.INVESTOR_COLLABORATOR_CHANGE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.INVESTOR_COLLABORATOR_CHANGE_DESC, investorCode, toCollaboratorCode));
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
	
	public void depositMargin(HttpServletRequest request, MarginTransactionDTO marginTransDto, long refId) {
		String methodName = "depositMargin";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to inv_margin_trans_approvals
			String approvalId = insertInvestorMarginDepositPA(userInfo, marginTransDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_INVESTOR_DEPOSIT_MONEY,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_DEPOSIT_MONEY_DESC, marginTransDto.getInvestorCode(), approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public String insertInvestorMarginDepositPA(UserInfoDTO userInfo, MarginTransactionDTO marginTransDto, long refId) {
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
			pendingData.setValue(new Gson().toJson(marginTransDto));

			InvestorMarginTransApproval marginTransApproval = new InvestorMarginTransApproval();
			marginTransApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_ACCOUNT_TRANS_URL, approvalId));
			marginTransApproval.setCreatorDate(System.currentTimeMillis());
			marginTransApproval.setCreatorUser(userInfo.getUsername());
			marginTransApproval.setFunctionCode(SystemFunctionCode.APPROVAL_INVESTOR_MARGIN_DEPOSIT_CODE);
			marginTransApproval.setFunctionName(SystemFunctionCode.INVESTOR_MARGIN_DEPOSIT_NAME);
			marginTransApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(marginTransApproval.getFunctionName(), marginTransDto.getInvestorCode()));
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
	
	public void implDepositMargin(String approvalId) {
		
	}
	
	public void withdrawMargin(HttpServletRequest request, MarginTransactionDTO marginTransDto, long refId) {
		String methodName = "withdrawMargin";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to inv_margin_trans_approvals
			String approvalId = insertInvestorMarginWithdrawPA(userInfo, marginTransDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_INVESTOR_DEPOSIT_MONEY,
					ActivityLogService.ACTIVITY_CREATE_INVESTOR_DEPOSIT_MONEY_DESC, marginTransDto.getInvestorCode(), approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public String insertInvestorMarginWithdrawPA(UserInfoDTO userInfo, MarginTransactionDTO marginTransDto, long refId) {
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
			pendingData.setValue(new Gson().toJson(marginTransDto));

			InvestorMarginTransApproval marginTransApproval = new InvestorMarginTransApproval();
			marginTransApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_ACCOUNT_TRANS_URL, approvalId));
			marginTransApproval.setCreatorDate(System.currentTimeMillis());
			marginTransApproval.setCreatorUser(userInfo.getUsername());
			marginTransApproval.setFunctionCode(SystemFunctionCode.APPROVAL_INVESTOR_MARGIN_WITHDRAWAL_CODE);
			marginTransApproval.setFunctionName(SystemFunctionCode.INVESTOR_MARGIN_WITHDRAWAL_NAME);
			marginTransApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(marginTransApproval.getFunctionName(), marginTransDto.getInvestorCode()));
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
	
	public void implWithdrawMargin(String approvalId) {
		
	}
}
