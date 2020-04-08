package com.newgen.am;

import com.newgen.am.common.ConfigLoader;
import com.newgen.am.common.Constant;
import com.newgen.am.common.FileUtility;
import com.newgen.am.common.Utility;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.model.SystemRole;
import com.newgen.am.model.UserRole;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.SystemRoleRepository;
import com.newgen.am.service.DBSequenceService;
import com.newgen.am.service.DepartmentService;
import com.newgen.am.service.InvestorService;
import com.newgen.am.service.LoginAdminUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableMongoAuditing
@SpringBootApplication
public class NewgenAmApplication {

//    @Autowired
//    DBSequenceRepository dbSequenceRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DBSequenceService dbSeqService;
//    @Autowired
//    private LoginInvestorUserRepository loginInvUserRepo;
//    @Autowired
//    private DepartmentRepository deptRepo;
    @Autowired
    private LoginAdminUserRepository loginAdmUserRepo;
//    @Autowired
//    private MemberRepository memberRepo;
//    @Autowired
//    private BrokerRepository brokerRepo;
//    @Autowired
//    private CollaboratorRepository collaboratorRepo;
//    @Autowired
//    private InvestorRepository investorRepo;
//    @Autowired
//    private MemberRoleRepository memberRoleRepo;
//    @Autowired
//    private SystemFunctionRepository sysFuncRepo;
    @Autowired
    private SystemRoleRepository sysRoleRepo;
    
    @Autowired
    private LoginAdminUserService loginAdmUserService;
    
    @Autowired
    private DepartmentService deptService;
//    @Autowired
//    private InvestorUserService invUserService;
//    @Autowired
//    private LoginInvestorUserService loginInvUserService;
//    @Autowired
//    private RedisUserInfoRepository redisUsrInfoRepo;
//    @Autowired
//    private RedisTemplate template;
    @Autowired
    private InvestorService investorService;
//    @Autowired
//    private InvestorAccTransApprovalRepository accTransApprovalRepo;

    public static void main(String[] args) {
        SpringApplication.run(NewgenAmApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
//
//    @Bean
//    public CommandLineRunner testRedis() {
//        LoginAdminUser user = new LoginAdminUser();
//        user.setUsername("nhungtt");
//        user.setPassword("abcxyz");
//        user.setPin("123456");
//        return args -> {
//            deptService.sendCreateNewUserEmail("nhungtt0212@gmail.com", user, System.currentTimeMillis());
//        };
//    }

//    @Bean
//    public CommandLineRunner testDemo() {
//        // Insert new SystemRole
//        RoleFunction functionTest1 = new RoleFunction();
//        functionTest1.setCode("functionTest1");
//        functionTest1.setName("function test");
//        functionTest1.setParentCode("abc");
//        
//        RoleFunction functionTest2 = new RoleFunction();
//        functionTest2.setCode("functionTest1");
//        functionTest2.setName("function test");
//        functionTest2.setParentCode("abc");
//        
//        List<RoleFunction> collaFuncs = new ArrayList<>();
//        collaFuncs.add(functionTest1);
//        collaFuncs.add(functionTest2);
//        
//        SystemRole tvkdRole = new SystemRole();
//        tvkdRole.setId(dbSeqService.generateSequence(SystemRole.SEQUENCE_NAME));
//        tvkdRole.setName("TVKD");
//        tvkdRole.setDescription("Nhom quyen cho Thanh vien kinh doanh");
//        tvkdRole.setStatus("ACTIVE");
//        tvkdRole.setFunctions(collaFuncs);
//        
//        SystemRole mgRole = new SystemRole();
//        mgRole.setId(dbSeqService.generateSequence(SystemRole.SEQUENCE_NAME));
//        mgRole.setName("MG");
//        mgRole.setDescription("Nhom quyen cho Moi gioi");
//        mgRole.setStatus("ACTIVE");
//        mgRole.setFunctions(collaFuncs);
//        
//        SystemRole tkgdRole = new SystemRole();
//        tkgdRole.setId(dbSeqService.generateSequence(SystemRole.SEQUENCE_NAME));
//        tkgdRole.setName("TKGD");
//        tkgdRole.setDescription("Nhom quyen cho Tai khoan giao dich");
//        tkgdRole.setStatus("ACTIVE");
//        tkgdRole.setFunctions(collaFuncs);
//        
//        SystemRole ctvRole = new SystemRole();
//        ctvRole.setId(dbSeqService.generateSequence(SystemRole.SEQUENCE_NAME));
//        ctvRole.setName("CTV-MG");
//        ctvRole.setDescription("Nhom quyen cho Cong tac vien moi gioi");
//        ctvRole.setFunctions(collaFuncs);
//        
//        Member existedMem = memberRepo.findById(15l).get();
//        existedMem.setFunctions(collaFuncs);
//        
//        Broker existedBroker = brokerRepo.findById(15l).get();
//        existedBroker.setFunctions(collaFuncs);
//        
//        Collaborator existedColla = collaboratorRepo.findById(1l).get();
//        existedColla.setFunctions(collaFuncs);
//        
//        Investor existedInv = investorRepo.findById(15l).get();
//        existedInv.setCollaboratorId(1l);
//        
//        LoginAdminUser existedLoginAdmUser = loginAdmUserRepo.findById(4l).get();
//        existedLoginAdmUser.setMemberId(0l);
//        existedLoginAdmUser.setMemberUserId(0l);
//        existedLoginAdmUser.setBrokerId(0l);
//        existedLoginAdmUser.setBrokerUserId(0l);
//        existedLoginAdmUser.setCollaboratorId(0l);
//        existedLoginAdmUser.setCollaboratorUserId(0l);
//        
//        SystemFunction systemManagement = new SystemFunction();
//        systemManagement.setId(dbSeqService.generateSequence(SystemFunction.SEQUENCE_NAME));
//        systemManagement.setCode(SystemFunctionCode.SYSTEM_MANAGEMENT);
//        systemManagement.setName("Quan ly he thong");
//
//
//        SystemFunction marginProcess1 = new SystemFunction();
//        marginProcess1.setId(dbSeqService.generateSequence(SystemFunction.SEQUENCE_NAME));
//        marginProcess1.setCode(SystemFunctionCode.MARGIN_PROCESS_METHOD_VIEW);
//        marginProcess1.setName("Xem phuong thuc xu ly ky quy");
//        marginProcess1.setParentCode(SystemFunctionCode.SYSTEM_MANAGEMENT);
//
//        SystemFunction marginProcess2 = new SystemFunction();
//        marginProcess2.setId(dbSeqService.generateSequence(SystemFunction.SEQUENCE_NAME));
//        marginProcess2.setCode(SystemFunctionCode.MARGIN_PROCESS_METHOD_CREATE);
//        marginProcess2.setName("Thiet lap phuong thuc xu ly ky quy");
//        marginProcess2.setParentCode(SystemFunctionCode.SYSTEM_MANAGEMENT);
//
//        SystemFunction marginProcess3 = new SystemFunction();
//        marginProcess3.setId(dbSeqService.generateSequence(SystemFunction.SEQUENCE_NAME));
//        marginProcess3.setCode(SystemFunctionCode.MARGIN_PROCESS_METHOD_HISTORY);
//        marginProcess3.setName("Xem lich su thiet lap phuong thuc xu ly ky quy");
//        marginProcess3.setParentCode(SystemFunctionCode.SYSTEM_MANAGEMENT);
//
//        Collaborator collaborator = new Collaborator();
//        collaborator.setId(dbSeqService.generateSequence(Collaborator.SEQUENCE_NAME));
//        collaborator.setMemberId(15l);
//        collaborator.setBrokerId(15l);
//        collaborator.setMemberCode("001");
//        collaborator.setMemberName("TVKD1");
//        collaborator.setBrokerCode("00001");
//        collaborator.setBrokerName("Nguyen Van A");
//        collaborator.setCode("00100001001");
//        collaborator.setName("Cong tac vien 1");
//        collaborator.setNote("abc");
//        collaborator.setStatus(Constant.STATUS_ACTIVE);
//        
//        collaborator.setIndividualInfo(new Individual());
//        collaborator.getIndividualInfo().setFullName("Cong tac vien 1");
//        collaborator.getIndividualInfo().setAddress("Trung hoa, Cau Giay, HN");
//        collaborator.getIndividualInfo().setBirthday("01-01-1990");
//        collaborator.getIndividualInfo().setEmail("ctv1@gmail.com");
//        collaborator.getIndividualInfo().setPhoneNumber("0912334456");
//        collaborator.getIndividualInfo().setIdentityCard("22345567");
//        collaborator.getIndividualInfo().setIdCreatedDate("01-01-2010");
//        collaborator.getIndividualInfo().setIdCreatedLocation("HN");
//        collaborator.getIndividualInfo().setScannedIdCard("abc");
//        collaborator.getIndividualInfo().setScannedSignature("abc");
//        
//        collaborator.setContact(new Contact());
//        collaborator.getContact().setFullName("Cong tac vien 1");
//        collaborator.getContact().setEmail("ctv1@gmail.com");
//        collaborator.getContact().setPhoneNumber("0912334456");
//        
//        UserRole ctvRole2 = new UserRole();
//        ctvRole2.setName("CTV-MG");
//        ctvRole2.setDescription("Nhom quyen cho Cong tac vien moi gioi");
//        ctvRole2.setStatus(Constant.STATUS_ACTIVE);
//        
//        List<UserRole> collaRoles = new ArrayList<>();
//        collaRoles.add(ctvRole2);
//        collaborator.setRoles(collaRoles);
//        
//        
//                
//        collaborator.setUser(new CollaboratorUser());
//        collaborator.getUser().setId(dbSeqService.generateSequence(CollaboratorUser.SEQUENCE_NAME));
//        collaborator.getUser().setUsername("ctv1");
//        collaborator.getUser().setFullName("Cong tac vien 1");
//        collaborator.getUser().setEmail("ctv1@gmail.com");
//        collaborator.getUser().setCreatedAt(new Date());
//        collaborator.getUser().setExpiryAlertDays(7);
//        collaborator.getUser().setFunctions(collaFuncs);
//        collaborator.getUser().setIsPasswordExpiryCheck(Boolean.FALSE);
//        collaborator.getUser().setNote("abc");
//        collaborator.getUser().setPasswordExpiryDays(30);
//        collaborator.getUser().setPhoneNumber("098776655");
//        collaborator.getUser().setRoles(collaRoles);
//        collaborator.getUser().setStatus(Constant.STATUS_ACTIVE);
//        
//        Department dept = new Department();
//        dept.setId(dbSeqService.generateSequence(Department.SEQUENCE_NAME));
//        dept.setCode("KeToan");
//        dept.setName("Ke toan");
//        dept.setStatus(Constant.STATUS_ACTIVE);
//        dept.setNote("abc");
//        
//        DeptUser deptUser1 = new DeptUser();
//        deptUser1.setUsername("quyetnv");
//        deptUser1.setCreatedAt(new Date());
//        deptUser1.setEmail("quyetnv@gmail.com");
//        deptUser1.setExpiryAlertDays(7);
//        deptUser1.setFullName("nguyen van quyet");
//        deptUser1.setFunctions(collaFuncs);
//        deptUser1.setId(dbSeqService.generateSequence(DeptUser.SEQUENCE_NAME));
//        deptUser1.setIsPasswordExpiryCheck(Boolean.FALSE);
//        deptUser1.setLogonCounts(1);
//        deptUser1.setLogonTime(new Date());
//        deptUser1.setNote("abc");
//        deptUser1.setPasswordExpiryDays(90);
//        deptUser1.setPhoneNumber("0987665544");
//        deptUser1.setRoles(collaRoles);
//        deptUser1.setStatus(Constant.STATUS_ACTIVE);
//        
//        List<DeptUser> deptUsers = new ArrayList<>();
//        deptUsers.add(deptUser1);
//        dept.setUsers(deptUsers);
//        
//        LoginAdminUser loginAdmUser = new LoginAdminUser();
//        loginAdmUser.setId(dbSeqService.generateSequence(LoginAdminUser.SEQUENCE_NAME));
//        loginAdmUser.setAccessToken("abc");
//        loginAdmUser.setCheckPin(Boolean.TRUE);
//        loginAdmUser.setDepartmentId(dept.getId());
//        loginAdmUser.setFontSize(12);
//        loginAdmUser.setLanguage("vn");
//        loginAdmUser.setLayout("layout");
//        loginAdmUser.setLogined(Boolean.FALSE);
//        loginAdmUser.setPassword(passwordEncoder.encode("Quyetnv@123"));
//        loginAdmUser.setPin("123456");
//        loginAdmUser.setTheme("black");
//        loginAdmUser.setTokenExpiration(Long.MIN_VALUE);
//        loginAdmUser.setUserId(deptUser1.getId());
//        loginAdmUser.setUsername("quyetnv");
//        
//        MemberRole mRole1 = new MemberRole();
//        mRole1.setId(dbSeqService.generateSequence(MemberRole.SEQUENCE_NAME));
//        mRole1.setName("TruongPhong");
//        mRole1.setDescription("Truong phong");
//        mRole1.setStatus(Constant.STATUS_ACTIVE);
//        mRole1.setFunctions(collaFuncs);
//        
//        return args -> {
//            sysRoleRepo.deleteAll();
//            sysRoleRepo.save(tvkdRole);
//            sysRoleRepo.save(mgRole);
//            sysRoleRepo.save(ctvRole);
//            sysRoleRepo.save(tkgdRole);
//            
//            memberRepo.save(existedMem);
//            brokerRepo.save(existedBroker);
//            collaboratorRepo.save(existedColla);
//            investorRepo.save(existedInv);
//            loginAdmUserRepo.save(existedLoginAdmUser);
//            sysFuncRepo.deleteAll();
//            sysFuncRepo.save(systemManagement);
//            sysFuncRepo.save(marginProcess1);
//            sysFuncRepo.save(marginProcess2);
//            sysFuncRepo.save(marginProcess3);
//            
//            collaboratorRepo.save(collaborator);
//            deptRepo.save(dept);
//            loginAdmUserRepo.save(loginAdmUser);
//            memberRoleRepo.save(mRole1);
    // test Mongodb Aggregation
//            Investor investor = invUserService.changePassword(15l, 113l, 1l, "nhung", "nhungtt");
//            System.out.println("investor.name: " + investor.getName());
//            String data = invUserService.getData(15l, 113l);
//            System.out.println("data: " + data);
//        };
//    }
//    @Bean
//    public CommandLineRunner startSequence() {
//        // Insert new SystemRole
//        SystemRole ketoanRole = new SystemRole();
//        ketoanRole.setId(dbSeqService.generateSequence(SystemRole.SEQUENCE_NAME));
//        ketoanRole.setName("Ke Toan");
//        ketoanRole.setDescription("Nhom quyen cho phong ban Ke toan");
//        ketoanRole.setStatus("ACTIVE");
//        
//        SystemRole mgRole = new SystemRole();
//        mgRole.setId(dbSeqService.generateSequence(SystemRole.SEQUENCE_NAME));
//        mgRole.setName("MG");
//        mgRole.setDescription("Nhom quyen cho Moi gioi");
//        mgRole.setStatus("ACTIVE");
//        
//        SystemRole tkgdRole = new SystemRole();
//        tkgdRole.setId(dbSeqService.generateSequence(SystemRole.SEQUENCE_NAME));
//        tkgdRole.setName("TKGD");
//        tkgdRole.setDescription("Nhom quyen cho Tai khoan giao dich");
//        tkgdRole.setStatus("ACTIVE");
//        
//        // Insert new Member
//        Member member = new Member();
//        member.setId(dbSeqService.generateSequence(Member.SEQUENCE_NAME));
//        member.setCode("001");
//        member.setName("TVKD1");
//        member.setNote("Testing TVKD1");
//        member.setCompany(new Company());
//        member.getCompany().setName("Newgen");
//        member.getCompany().setRegistrationNumber("123456789");
//        member.getCompany().setTaxCode("8888888");
//        member.getCompany().setPhoneNumber("0912344566");
//        member.getCompany().setFax("03667899000");
//        member.getCompany().setEmail("newgen@gmail.com");
//        member.getCompany().setAddress("1 Trang Tien, Hoan Kiem, Hanoi, VN");
//        member.getCompany().setDelegate(new Delegate());
//        member.getCompany().getDelegate().setFullName("Nguyen Dang Hung");
//        member.getCompany().getDelegate().setBirthDay("01/01/1976");
//        member.getCompany().getDelegate().setIdentityCard("288900055");
//        member.getCompany().getDelegate().setIdCreatedDate("01/01/2010");
//        member.getCompany().getDelegate().setIdCreatedLocation("Hanoi");
//        member.getCompany().getDelegate().setEmail("danghung@gmail.com");
//        member.getCompany().getDelegate().setPhoneNumber("0967383388");
//        member.getCompany().getDelegate().setAddress("1 Trang Tien, Hoan Kiem, HN");
//        member.setContact(new Contact());
//        member.getContact().setFullName("Nguyen Dang Hung");
//        member.getContact().setEmail("danghung@gmail.com");
//        member.getContact().setPhoneNumber("0967383388");
//        
//        // Set roles for member
//        UserRole tvkdRole2 = new UserRole();
//        tvkdRole2.setName("TVKD");
//        tvkdRole2.setDescription("Nhom quyen cho Thanh vien kinh doanh");
//        tvkdRole2.setStatus("ACTIVE");
//        List<UserRole> memberRoles = new ArrayList<>();
//        memberRoles.add(tvkdRole2);
//        member.setRoles(memberRoles);
//        
//        // Set position limit
//        member.setOrderLimit(20);
//        
//        //set user for member
//        MemberUser memberUser = new MemberUser();
//        memberUser.setId(dbSeqService.generateSequence(MemberUser.SEQUENCE_NAME));
//        memberUser.setUsername("nguyendanghung");
//        memberUser.setPassword("Nguyen@123");
//        memberUser.setPin("123456");
//        memberUser.setFullName("Nguyen Dang Hung");
//        memberUser.setEmail("danghung@gmail.com");
//        memberUser.setPhoneNumber("0967383388");
//        memberUser.setStatus("ACTIVE");
//        memberUser.setTitle("CEO");
//        memberUser.setDepartment("CEO");
//        memberUser.setIsPasswordExpiryCheck(Boolean.FALSE);
//        memberUser.setCreatedAt(new Date());
//        memberUser.setRoles(memberRoles);
//        List<MemberUser> memberUsers = new ArrayList<>();
//        memberUsers.add(memberUser);
//        
//        member.setUsers(memberUsers);
//        
//        // Add commodities for member, broker, investor
//        Commodity com1 = new Commodity();
//        com1.setCode("LRC");
//        com1.setName("Ca phe Robusta");
//        com1.setPositionLimit(20);
//        com1.setOrderProcessFee(200000l);
//        
//        Commodity com2 = new Commodity();
//        com2.setCode("KCE");
//        com2.setName("Ca phe Arabica");
//        com2.setPositionLimit(20);
//        com2.setOrderProcessFee(300000l);
//        
//        Commodity com3 = new Commodity();
//        com3.setCode("ZME");
//        com3.setName("Kho dau tuong");
//        com3.setPositionLimit(20);
//        com3.setOrderProcessFee(350000l);
//        
//        
//        Commodity com4 = new Commodity();
//        com4.setCode("ZLE");
//        com4.setName("Dau dau tuong");
//        com4.setPositionLimit(20);
//        com4.setOrderProcessFee(400000l);
//        
//        Commodity com5 = new Commodity();
//        com5.setCode("CLE");
//        com5.setName("Dau WTI");
//        com5.setPositionLimit(20);
//        com5.setOrderProcessFee(500000l);
//        
//        List<Commodity> commodities = new ArrayList<>();
//        commodities.add(com1);
//        commodities.add(com2);
//        commodities.add(com3);
//        commodities.add(com4);
//        commodities.add(com5);
//        
//        member.setComodities(commodities);
//        
//        // Set new broker for member
//        Broker broker = new Broker();
//        broker.setId(dbSeqService.generateSequence(Broker.SEQUENCE_NAME));
//        broker.setMemberId(member.getId());
//        broker.setMemberCode("001");
//        broker.setMemberName("TVKD1");
//        broker.setCode("00001");
//        broker.setName("Nguyen Van A");
//        broker.setStatus("ACTIVE");
//        broker.setType("INDIVIDUAL");
//        broker.setBusinessType("INDIVIDUAL");
//        broker.setIndividual(new Individual());
//        broker.getIndividual().setFullName("Nguyen Van A");
//        broker.getIndividual().setBirthday("01/01/1990");
//        broker.getIndividual().setIdentityCard("22334455");
//        broker.getIndividual().setIdCreatedDate("01/01/2010");
//        broker.getIndividual().setIdCreatedLocation("Hanoi");
//        broker.getIndividual().setEmail("nguyen.van.a@gmail.com");
//        broker.getIndividual().setPhoneNumber("09766554442");
//        broker.getIndividual().setAddress("83B LTK, Hoan Kiem, Hanoi, VN");
//        broker.setContact(new Contact());
//        broker.getContact().setFullName("Nguyen Van A");
//        broker.getContact().setPhoneNumber("09766554442");
//        broker.getContact().setEmail("nguyen.van.a@gmail.com");
//        
//        // Set roles for broker
//        UserRole mgRole2 = new UserRole();
//        mgRole2.setName("MG");
//        mgRole2.setDescription("Nhom quyen cho Moi gioi");
//        mgRole2.setStatus("ACTIVE");
//        List<UserRole> brokerRoles = new ArrayList<>();
//        brokerRoles.add(mgRole2);
//        broker.setRoles(brokerRoles);
//        
//        // Set user for broker
//        broker.setUser(new BrokerUser());
//        broker.getUser().setId(dbSeqService.generateSequence(BrokerUser.SEQUENCE_NAME));
//        broker.getUser().setUsername("nguyenvana");
//        broker.getUser().setPassword("Nguyen@123");
//        broker.getUser().setPin("123456");
//        broker.getUser().setFullName("Nguyen Van A");
//        broker.getUser().setEmail("nguyen.van.a@gmail.com");
//        broker.getUser().setPhoneNumber("09766554442");
//        broker.getUser().setStatus("ACTIVE");
//        broker.getUser().setIsPasswordExpiryCheck(Boolean.FALSE);
//        broker.getUser().setCreatedAt(new Date());
//        broker.getUser().setRoles(brokerRoles);
//        
//        // Set position limit
//        broker.setOrderLimit(20);
//        
//        // Set commodities for broker
//        broker.setCommodities(commodities);
//
//        // Set Investor for broker
//        Investor investor = new Investor();
//        investor.setId(dbSeqService.generateSequence(Investor.SEQUENCE_NAME));
//        investor.setMemberId(member.getId());
//        investor.setBrokerId(broker.getId());
//        investor.setMemberCode("001");
//        investor.setMemberName("TVKD1");
//        investor.setBrokerCode("00001");
//        investor.setBrokerName("Nguyen Van A");
//        investor.setCode("001C000001");
//        investor.setName("Bui Thi Lien Huong");
//        investor.setStatus("ACTIVE");
//        investor.setType("LOCAL_INDIVIDUAL");
//        investor.setIndividual(new Individual());
//        investor.getIndividual().setFullName("Bui Thi Lien Huong");
//        investor.getIndividual().setBirthday("01/01/1990");
//        investor.getIndividual().setIdentityCard("1233444555");
//        investor.getIndividual().setIdCreatedDate("01/01/2010");
//        investor.getIndividual().setIdCreatedLocation("Hanoi");
//        investor.getIndividual().setEmail("lien.huong@gmail.com");
//        investor.getIndividual().setPhoneNumber("09123567778");
//        investor.getIndividual().setAddress("1, Trang tien, Hoan Kiem, Hanoi");
//        investor.setContact(new Contact());
//        investor.getContact().setFullName("Bui Thi Lien Huong");
//        investor.getContact().setPhoneNumber("09123567778");
//        investor.getContact().setEmail("lien.huong@gmail.com");
//        
//         // Set roles for investor
//        UserRole tkgdRole2 = new UserRole();
//        tkgdRole2.setName("MG");
//        tkgdRole2.setDescription("Nhom quyen cho Moi gioi");
//        tkgdRole2.setStatus("ACTIVE");
//        List<UserRole> investorRoles = new ArrayList<>();
//        investorRoles.add(tkgdRole2);
//        investor.setRoles(investorRoles);
//        
//        // Set position limit
//        investor.setOrderLimit(20);
//        
//        // Set margin ratio alert
//        investor.setMarginRatioAlert(new MarginRatioAlert());
//        investor.getMarginRatioAlert().setWarningRatio(80);
//        investor.getMarginRatioAlert().setCancelOrderRatio(70);
//        investor.getMarginRatioAlert().setFinalizationRatio(30);
//        
//        // Set margin multiplier
//        investor.setMarginMultiplier(1.0);
//        
//        investor.setCommodities(commodities);
//        investor.setAccount(new InvestorAccount());
//        investor.getAccount().setCode("001C000001");
//        investor.getAccount().setCurrency("VND");
//        investor.getAccount().setMarginDeficitInterestRate(0.05);
//        investor.getAccount().setMarginSurplusInterestRate(0.05);
//        investor.getAccount().setAmountHold(500000000d);
//        investor.getAccount().setDayBeginBalance(500000000d);
//        investor.getAccount().setGeneralFee(500000d);
//        investor.getAccount().setOtherFee(0d);
//        investor.getAccount().setCreatedAt(new Date());
//        
//        // Set user for investor
//        InvestorUser user = new InvestorUser();
//        user.setId(dbSeqService.generateSequence(InvestorUser.SEQUENCE_NAME));
//        user.setUsername("newgen");
//        user.setPassword(passwordEncoder.encode("Newgen@123"));
//        user.setPin("123456");
//        user.setFullName("Bui Thi Lien Huong");
//        user.setEmail("lien.huong@gmail.com");
//        user.setPhoneNumber("09123567778");
//        user.setStatus("ACTIVE");
//        user.setIsPasswordExpiryCheck(Boolean.FALSE);
//        user.setCreatedAt(new Date());
//        user.setRoles(investorRoles);
//        
//        InvestorUser user2 = new InvestorUser();
//        user2.setId(dbSeqService.generateSequence(InvestorUser.SEQUENCE_NAME));
//        user2.setUsername("thanhdo");
//        user2.setPassword(passwordEncoder.encode("Thanhdo@123"));
//        user2.setPin("123456");
//        user2.setFullName("Do Viet Thanh");
//        user2.setEmail("thanh.do@gmail.com");
//        user2.setPhoneNumber("093356677777");
//        user2.setStatus("ACTIVE");
//        user2.setIsPasswordExpiryCheck(Boolean.FALSE);
//        user2.setCreatedAt(new Date());
//        user2.setRoles(investorRoles);
//        
//        InvestorUser user3 = new InvestorUser();
//        user3.setId(dbSeqService.generateSequence(InvestorUser.SEQUENCE_NAME));
//        user3.setUsername("huongtra");
//        user3.setPassword(passwordEncoder.encode("Huongtra@123"));
//        user3.setPin("123456");
//        user3.setFullName("Vu Huong Tra");
//        user3.setEmail("huong.tra@gmail.com");
//        user3.setPhoneNumber("093383899999");
//        user3.setStatus("ACTIVE");
//        user3.setIsPasswordExpiryCheck(Boolean.FALSE);
//        user3.setCreatedAt(new Date());
//        user3.setRoles(investorRoles);
//        
//        InvestorUser user4 = new InvestorUser();
//        user4.setId(dbSeqService.generateSequence(InvestorUser.SEQUENCE_NAME));
//        user4.setUsername("usertest1");
//        user4.setPassword(passwordEncoder.encode("Usertest@123"));
//        user4.setPin("123456");
//        user4.setFullName("Vu Huong Tra");
//        user4.setEmail("huong.tra@gmail.com");
//        user4.setPhoneNumber("093383899999");
//        user4.setStatus("ACTIVE");
//        user4.setIsPasswordExpiryCheck(Boolean.FALSE);
//        user4.setCreatedAt(new Date());
//        user4.setRoles(investorRoles);
//        
//        InvestorUser user5 = new InvestorUser();
//        user5.setId(dbSeqService.generateSequence(InvestorUser.SEQUENCE_NAME));
//        user5.setUsername("usertest2");
//        user5.setPassword(passwordEncoder.encode("Usertest@123"));
//        user5.setPin("123456");
//        user5.setFullName("Vu Huong Tra");
//        user5.setEmail("huong.tra@gmail.com");
//        user5.setPhoneNumber("093383899999");
//        user5.setStatus("ACTIVE");
//        user5.setIsPasswordExpiryCheck(Boolean.FALSE);
//        user5.setCreatedAt(new Date());
//        user5.setRoles(investorRoles);
//        
//        InvestorUser user6 = new InvestorUser();
//        user6.setId(dbSeqService.generateSequence(InvestorUser.SEQUENCE_NAME));
//        user6.setUsername("usertest3");
//        user6.setPassword(passwordEncoder.encode("Usertest@123"));
//        user6.setPin("123456");
//        user6.setFullName("Vu Huong Tra");
//        user6.setEmail("huong.tra@gmail.com");
//        user6.setPhoneNumber("093383899999");
//        user6.setStatus("ACTIVE");
//        user6.setIsPasswordExpiryCheck(Boolean.FALSE);
//        user6.setCreatedAt(new Date());
//        user6.setRoles(investorRoles);
//        
//        InvestorUser user7 = new InvestorUser();
//        user7.setId(dbSeqService.generateSequence(InvestorUser.SEQUENCE_NAME));
//        user7.setUsername("usertest4");
//        user7.setPassword(passwordEncoder.encode("Usertest@123"));
//        user7.setPin("123456");
//        user7.setFullName("Vu Huong Tra");
//        user7.setEmail("huong.tra@gmail.com");
//        user7.setPhoneNumber("093383899999");
//        user7.setStatus("ACTIVE");
//        user7.setIsPasswordExpiryCheck(Boolean.FALSE);
//        user7.setCreatedAt(new Date());
//        user7.setRoles(investorRoles);
//        
//        InvestorUser user8 = new InvestorUser();
//        user8.setId(dbSeqService.generateSequence(InvestorUser.SEQUENCE_NAME));
//        user8.setUsername("usertest5");
//        user8.setPassword(passwordEncoder.encode("Usertest@123"));
//        user8.setPin("123456");
//        user8.setFullName("Vu Huong Tra");
//        user8.setEmail("huong.tra@gmail.com");
//        user8.setPhoneNumber("093383899999");
//        user8.setStatus("ACTIVE");
//        user8.setIsPasswordExpiryCheck(Boolean.FALSE);
//        user8.setCreatedAt(new Date());
//        user8.setRoles(investorRoles);
//        
//        List<InvestorUser> users = new ArrayList<>();
//        users.add(user);
//        users.add(user2);
//        users.add(user3);
//        users.add(user4);
//        users.add(user5);
//        users.add(user6);
//        users.add(user7);
//        users.add(user8);
//        
//        investor.setUsers(users);
//        
////        List<Investor> investors = new ArrayList<>();
////        investors.add(investor);
////        
////        broker.setInvestors(investors);
//        
////        List<Broker> brokers = new ArrayList<>();
////        brokers.add(broker);
////        member.setBrokers(brokers);
//        
//        LoginInvestorUser loginInvUser = new LoginInvestorUser();
//        loginInvUser.setId(dbSeqService.generateSequence(LoginInvestorUser.SEQUENCE_NAME));
//        loginInvUser.setUsername("newgen1");
//        loginInvUser.setPassword(passwordEncoder.encode("Password@123"));
//        loginInvUser.setCheckPin(Boolean.TRUE);
//        loginInvUser.setPin("123456");
//        loginInvUser.setMemberId(15l);
//        loginInvUser.setBrokerId(15l);
//        loginInvUser.setInvestorId(15l);
//        loginInvUser.setInvestorUserId(15l);
//        
//        // insert into login_investor_users
//        LoginInvestorUser loginInvUser2 = new LoginInvestorUser();
//        loginInvUser2.setId(dbSeqService.generateSequence(LoginInvestorUser.SEQUENCE_NAME));
//        loginInvUser2.setUsername("newgen2");
//        loginInvUser2.setPassword(passwordEncoder.encode("Password@123"));
//        loginInvUser2.setCheckPin(Boolean.TRUE);
//        loginInvUser2.setPin("123456");
//        loginInvUser2.setMemberId(15l);
//        loginInvUser2.setBrokerId(15l);
//        loginInvUser2.setInvestorId(15l);
//        loginInvUser2.setInvestorUserId(15l);
//        
//        LoginInvestorUser loginInvUser3 = new LoginInvestorUser();
//        loginInvUser3.setId(dbSeqService.generateSequence(LoginInvestorUser.SEQUENCE_NAME));
//        loginInvUser3.setUsername("newgen3");
//        loginInvUser3.setPassword(passwordEncoder.encode("Password@123"));
//        loginInvUser3.setCheckPin(Boolean.TRUE);
//        loginInvUser3.setPin("123456");
//        loginInvUser3.setMemberId(15l);
//        loginInvUser3.setBrokerId(15l);
//        loginInvUser3.setInvestorId(15l);
//        loginInvUser3.setInvestorUserId(15l);
//        
//        LoginInvestorUser loginInvUser4 = new LoginInvestorUser();
//        loginInvUser4.setId(dbSeqService.generateSequence(LoginInvestorUser.SEQUENCE_NAME));
//        loginInvUser4.setUsername("newgen4");
//        loginInvUser4.setPassword(passwordEncoder.encode("Password@123"));
//        loginInvUser4.setCheckPin(Boolean.TRUE);
//        loginInvUser4.setPin("123456");
//        loginInvUser4.setMemberId(15l);
//        loginInvUser4.setBrokerId(15l);
//        loginInvUser4.setInvestorId(15l);
//        loginInvUser4.setInvestorUserId(15l);
//        
//        LoginInvestorUser loginInvUser5 = new LoginInvestorUser();
//        loginInvUser5.setId(dbSeqService.generateSequence(LoginInvestorUser.SEQUENCE_NAME));
//        loginInvUser5.setUsername("newgen5");
//        loginInvUser5.setPassword(passwordEncoder.encode("Password@123"));
//        loginInvUser5.setCheckPin(Boolean.TRUE);
//        loginInvUser5.setPin("123456");
//        loginInvUser5.setMemberId(15l);
//        loginInvUser5.setBrokerId(15l);
//        loginInvUser5.setInvestorId(15l);
//        loginInvUser5.setInvestorUserId(15l);
        
//        LoginInvestorUser loginInvUser6 = new LoginInvestorUser();
//        loginInvUser6.setId(dbSeqService.generateSequence(LoginInvestorUser.SEQUENCE_NAME));
//        loginInvUser6.setUsername("newgen6");
//        loginInvUser6.setPassword(passwordEncoder.encode("Password@123"));
//        loginInvUser6.setCheckPin(Boolean.TRUE);
//        loginInvUser6.setPin("123456");
//        loginInvUser6.setMemberId(15l);
//        loginInvUser6.setBrokerId(15l);
//        loginInvUser6.setInvestorId(15l);
//        loginInvUser6.setInvestorUserId(15l);
//        
//        LoginInvestorUser loginInvUser7 = new LoginInvestorUser();
//        loginInvUser7.setId(dbSeqService.generateSequence(LoginInvestorUser.SEQUENCE_NAME));
//        loginInvUser7.setUsername("newgen7");
//        loginInvUser7.setPassword(passwordEncoder.encode("Password@123"));
//        loginInvUser7.setCheckPin(Boolean.TRUE);
//        loginInvUser7.setMemberId(15l);
//        loginInvUser7.setBrokerId(15l);
//        loginInvUser7.setInvestorId(15l);
//        loginInvUser7.setInvestorUserId(15l);
//        
//        LoginInvestorUser loginInvUser8 = new LoginInvestorUser();
//        loginInvUser8.setId(dbSeqService.generateSequence(LoginInvestorUser.SEQUENCE_NAME));
//        loginInvUser8.setUsername("newgen8");
//        loginInvUser8.setPassword(passwordEncoder.encode("Password@123"));
//        loginInvUser8.setCheckPin(Boolean.TRUE);
//        loginInvUser8.setPin("123456");
//        loginInvUser8.setMemberId(15l);
//        loginInvUser8.setBrokerId(15l);
//        loginInvUser8.setInvestorId(15l);
//        loginInvUser8.setInvestorUserId(15l);
//        
//        LoginInvestorUser loginInvUser9 = new LoginInvestorUser();
//        loginInvUser9.setId(dbSeqService.generateSequence(LoginInvestorUser.SEQUENCE_NAME));
//        loginInvUser9.setUsername("newgen9");
//        loginInvUser9.setPassword(passwordEncoder.encode("Password@123"));
//        loginInvUser9.setCheckPin(Boolean.TRUE);
//        loginInvUser9.setPin("123456");
//        loginInvUser9.setMemberId(15l);
//        loginInvUser9.setBrokerId(15l);
//        loginInvUser9.setInvestorId(15l);
//        loginInvUser9.setInvestorUserId(15l);
//        
//        LoginInvestorUser loginInvUser10 = new LoginInvestorUser();
//        loginInvUser10.setId(dbSeqService.generateSequence(LoginInvestorUser.SEQUENCE_NAME));
//        loginInvUser10.setUsername("newgen10");
//        loginInvUser10.setPassword(passwordEncoder.encode("Password@123"));
//        loginInvUser10.setCheckPin(Boolean.TRUE);
//        loginInvUser10.setPin("123456");
//        loginInvUser10.setMemberId(15l);
//        loginInvUser10.setBrokerId(15l);
//        loginInvUser10.setInvestorId(15l);
//        loginInvUser10.setInvestorUserId(15l);
        
//        LoginInvestorUser loginInvUser11 = new LoginInvestorUser();
//        loginInvUser11.setId(dbSeqService.generateSequence(LoginInvestorUser.SEQUENCE_NAME));
//        loginInvUser11.setUsername("newgen11");
//        loginInvUser11.setPassword(passwordEncoder.encode("Password@123"));
//        loginInvUser11.setCheckPin(Boolean.TRUE);
//        loginInvUser11.setPin("123456");
//        loginInvUser11.setMemberId(15l);
//        loginInvUser11.setBrokerId(15l);
//        loginInvUser11.setInvestorId(15l);
//        loginInvUser11.setInvestorUserId(15l);
//        
//        LoginInvestorUser loginInvUser12 = new LoginInvestorUser();
//        loginInvUser12.setId(dbSeqService.generateSequence(LoginInvestorUser.SEQUENCE_NAME));
//        loginInvUser12.setUsername("newgen12");
//        loginInvUser12.setPassword(passwordEncoder.encode("Password@123"));
//        loginInvUser12.setCheckPin(Boolean.TRUE);
//        loginInvUser12.setPin("123456");
//        loginInvUser12.setMemberId(15l);
//        loginInvUser12.setBrokerId(15l);
//        loginInvUser12.setInvestorId(15l);
//        loginInvUser12.setInvestorUserId(15l);
//        
//        return args -> {
//            sysRoleRepo.save(ketoanRole);
//            memberRepo.save(member);
//            brokerRepo.save(broker);
//            investorRepo.save(investor);
//            
//            loginInvUserRepo.save(loginInvUser);
//            loginInvUserRepo.save(loginInvUser2);
//            loginInvUserRepo.save(loginInvUser3);
//            loginInvUserRepo.save(loginInvUser4);
//            loginInvUserRepo.save(loginInvUser5);
//            loginInvUserRepo.save(loginInvUser6);
//            loginInvUserRepo.save(loginInvUser7);
//            loginInvUserRepo.save(loginInvUser8);
//            loginInvUserRepo.save(loginInvUser9);
//            loginInvUserRepo.save(loginInvUser10);
//            dbSequenceRepo.deleteAll();
//            // init for all sequences
//            dbSequenceRepo.save(new DBSequence("pending_approval_seq", 0));
//            dbSequenceRepo.save(new DBSequence("broker_seq", 0));
//            dbSequenceRepo.save(new DBSequence("broker_user_seq", 0));
//            dbSequenceRepo.save(new DBSequence("collaborator_seq", 0));
//            dbSequenceRepo.save(new DBSequence("collaborator_user_seq", 0));
//            dbSequenceRepo.save(new DBSequence("department_seq", 0));
//            dbSequenceRepo.save(new DBSequence("department_user_seq", 0));
//            dbSequenceRepo.save(new DBSequence("investor_seq", 0));
//            dbSequenceRepo.save(new DBSequence("investor_user_seq", 0));
//            dbSequenceRepo.save(new DBSequence("login_admin_user_seq", 0));
//            dbSequenceRepo.save(new DBSequence("member_seq", 0));
//            dbSequenceRepo.save(new DBSequence("member_role_seq", 0));
//            dbSequenceRepo.save(new DBSequence("member_user_seq", 0));
//            dbSequenceRepo.save(new DBSequence("system_function_seq", 0));
//            dbSequenceRepo.save(new DBSequence("system_role_seq", 0));
            // test Mongodb Aggregation
//            InvestorUser nestedUser = invUserService.changePassword(1l, 1l, 1l, 1l, 1l, "nhung", "nhung");
//            String data = invUserService.getData(1l, 1l);
//            System.out.println("data: " + data);
//        };
//    }
}
