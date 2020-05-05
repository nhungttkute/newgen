package com.newgen.am;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.newgen.am.common.Constant;
import com.newgen.am.model.AuditorAwareImpl;
import com.newgen.am.model.Department;
import com.newgen.am.model.DeptUser;
import com.newgen.am.model.Investor;
import com.newgen.am.model.InvestorUser;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.model.LoginInvestorUser;
import com.newgen.am.model.SystemRole;
import com.newgen.am.repository.DepartmentRepository;
import com.newgen.am.repository.InvestorRepository;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.LoginInvestorUserRepository;
import com.newgen.am.repository.SystemRoleRepository;
import com.newgen.am.service.DBSequenceService;
import com.newgen.am.service.DepartmentService;
import com.newgen.am.service.InvestorService;
import com.newgen.am.service.LoginAdminUserService;
import com.newgen.am.service.RedisMessagePublisher;

@EnableMongoAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
public class NewgenAmApplication {
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private LoginInvestorUserRepository loginInvUserRepo;
    
    @Autowired
    private InvestorRepository investorRepo;

    public static void main(String[] args) {
        SpringApplication.run(NewgenAmApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    
    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }

//    @Bean
//    public CommandLineRunner testRedis() {
//        
//        return args -> {
//        	Investor investor = investorRepo.findById("5eb00c8ae063150b87767eb4").get();
//        	List<InvestorUser> investorUsers = new ArrayList<InvestorUser>();
//        	
//        	InvestorUser user1 = new InvestorUser();
//        	user1.set_id(new ObjectId().toString());
//        	user1.setUsername("newgen");
//        	user1.setFullName("User Test");
//        	user1.setPhoneNumber("0912345678");
//        	user1.setEmail("abc@gmail.com");
//        	user1.setStatus(Constant.STATUS_ACTIVE);
//        	investorUsers.add(user1);
//        	
//        	LoginInvestorUser loginUser1 = new LoginInvestorUser();
//        	loginUser1.setUsername(user1.getUsername());
//        	loginUser1.setPassword(passwordEncoder.encode("Password@123"));
//        	loginUser1.setPin(passwordEncoder.encode("123456"));
//        	loginUser1.setStatus(Constant.STATUS_ACTIVE);
//        	loginUser1.setMemberId("5eb00ab132dd6763e4146c07");
//        	loginUser1.setBrokerId("5eb00bb8e063150b87767ea4");
//        	loginUser1.setCollaboratorId("5eb00c13e063150b87767eac");
//        	loginUser1.setInvestorId("5eb00c8ae063150b87767eb4");
//        	loginUser1.setInvestorUserId(user1.get_id());
//        	
//        	InvestorUser user2 = new InvestorUser();
//        	user2.set_id(new ObjectId().toString());
//        	user2.setUsername("thanhdo");
//        	user2.setFullName("User Test");
//        	user2.setPhoneNumber("0912345678");
//        	user2.setEmail("abc@gmail.com");
//        	user2.setStatus(Constant.STATUS_ACTIVE);
//        	investorUsers.add(user2);
//        	
//        	LoginInvestorUser loginUser2 = new LoginInvestorUser();
//        	loginUser2.setUsername(user2.getUsername());
//        	loginUser2.setPassword(passwordEncoder.encode("Password@123"));
//        	loginUser2.setPin(passwordEncoder.encode("123456"));
//        	loginUser2.setStatus(Constant.STATUS_ACTIVE);
//        	loginUser2.setMemberId("5eb00ab132dd6763e4146c07");
//        	loginUser2.setBrokerId("5eb00bb8e063150b87767ea4");
//        	loginUser2.setCollaboratorId("5eb00c13e063150b87767eac");
//        	loginUser2.setInvestorId("5eb00c8ae063150b87767eb4");
//        	loginUser2.setInvestorUserId(user2.get_id());
//        	
//        	InvestorUser user3 = new InvestorUser();
//        	user3.set_id(new ObjectId().toString());
//        	user3.setUsername("huongtra");
//        	user3.setFullName("User Test");
//        	user3.setPhoneNumber("0912345678");
//        	user3.setEmail("abc@gmail.com");
//        	user3.setStatus(Constant.STATUS_ACTIVE);
//        	investorUsers.add(user3);
//        	
//        	LoginInvestorUser loginUser3 = new LoginInvestorUser();
//        	loginUser3.setUsername(user3.getUsername());
//        	loginUser3.setPassword(passwordEncoder.encode("Password@123"));
//        	loginUser3.setPin(passwordEncoder.encode("123456"));
//        	loginUser3.setStatus(Constant.STATUS_ACTIVE);
//        	loginUser3.setMemberId("5eb00ab132dd6763e4146c07");
//        	loginUser3.setBrokerId("5eb00bb8e063150b87767ea4");
//        	loginUser3.setCollaboratorId("5eb00c13e063150b87767eac");
//        	loginUser3.setInvestorId("5eb00c8ae063150b87767eb4");
//        	loginUser3.setInvestorUserId(user3.get_id());
//        	
//        	InvestorUser user4 = new InvestorUser();
//        	user4.set_id(new ObjectId().toString());
//        	user4.setUsername("nhungtran");
//        	user4.setFullName("User Test");
//        	user4.setPhoneNumber("0912345678");
//        	user4.setEmail("abc@gmail.com");
//        	user4.setStatus(Constant.STATUS_ACTIVE);
//        	investorUsers.add(user4);
//        	
//        	LoginInvestorUser loginUser4 = new LoginInvestorUser();
//        	loginUser4.setUsername(user4.getUsername());
//        	loginUser4.setPassword(passwordEncoder.encode("Password@123"));
//        	loginUser4.setPin(passwordEncoder.encode("123456"));
//        	loginUser4.setStatus(Constant.STATUS_ACTIVE);
//        	loginUser4.setMemberId("5eb00ab132dd6763e4146c07");
//        	loginUser4.setBrokerId("5eb00bb8e063150b87767ea4");
//        	loginUser4.setCollaboratorId("5eb00c13e063150b87767eac");
//        	loginUser4.setInvestorId("5eb00c8ae063150b87767eb4");
//        	loginUser4.setInvestorUserId(user4.get_id());
//        	
//        	loginInvUserRepo.save(loginUser1);
//        	loginInvUserRepo.save(loginUser2);
//        	loginInvUserRepo.save(loginUser3);
//        	loginInvUserRepo.save(loginUser4);
//        	
//        	for (int i = 1; i <= 5; i++) {
//        		InvestorUser user = new InvestorUser();
//        		user.set_id(new ObjectId().toString());
//        		user.setUsername("usertest" + i);
//        		user.setFullName("User Test");
//        		user.setPhoneNumber("0912345678");
//        		user.setEmail("abc@gmail.com");
//        		user.setStatus(Constant.STATUS_ACTIVE);
//            	investorUsers.add(user);
//            	
//            	LoginInvestorUser loginUser = new LoginInvestorUser();
//            	loginUser.setUsername(user.getUsername());
//            	loginUser.setPassword(passwordEncoder.encode("Password@123"));
//            	loginUser.setPin(passwordEncoder.encode("123456"));
//            	loginUser.setStatus(Constant.STATUS_ACTIVE);
//            	loginUser.setMemberId("5eb00ab132dd6763e4146c07");
//            	loginUser.setBrokerId("5eb00bb8e063150b87767ea4");
//            	loginUser.setCollaboratorId("5eb00c13e063150b87767eac");
//            	loginUser.setInvestorId("5eb00c8ae063150b87767eb4");
//            	loginUser.setInvestorUserId(user.get_id());
//            	loginInvUserRepo.save(loginUser);
//        	}
//        	
//        	for (int i = 1; i <= 10; i++) {
//        		InvestorUser user = new InvestorUser();
//        		user.set_id(new ObjectId().toString());
//        		user.setUsername("newgen" + i);
//        		user.setFullName("User Test");
//        		user.setPhoneNumber("0912345678");
//        		user.setEmail("abc@gmail.com");
//        		user.setStatus(Constant.STATUS_ACTIVE);
//            	investorUsers.add(user);
//            	
//            	LoginInvestorUser loginUser = new LoginInvestorUser();
//            	loginUser.setUsername(user.getUsername());
//            	loginUser.setPassword(passwordEncoder.encode("Password@123"));
//            	loginUser.setPin(passwordEncoder.encode("123456"));
//            	loginUser.setStatus(Constant.STATUS_ACTIVE);
//            	loginUser.setMemberId("5eb00ab132dd6763e4146c07");
//            	loginUser.setBrokerId("5eb00bb8e063150b87767ea4");
//            	loginUser.setCollaboratorId("5eb00c13e063150b87767eac");
//            	loginUser.setInvestorId("5eb00c8ae063150b87767eb4");
//            	loginUser.setInvestorUserId(user.get_id());
//            	loginInvUserRepo.save(loginUser);
//        	}
//        	
//        	investor.setUsers(investorUsers);
//        	investorRepo.save(investor);
//        };
//    }

}
