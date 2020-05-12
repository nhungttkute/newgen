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
import com.newgen.am.common.Utility;
import com.newgen.am.model.AuditorAwareImpl;
import com.newgen.am.model.Department;
import com.newgen.am.model.DeptUser;
import com.newgen.am.model.Investor;
import com.newgen.am.model.InvestorUser;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.model.LoginInvestorUser;
import com.newgen.am.model.Member;
import com.newgen.am.model.MemberRole;
import com.newgen.am.model.MemberUser;
import com.newgen.am.model.SystemRole;
import com.newgen.am.repository.DepartmentRepository;
import com.newgen.am.repository.InvestorRepository;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.LoginInvestorUserRepository;
import com.newgen.am.repository.MemberRepository;
import com.newgen.am.repository.MemberRoleRepository;
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
    private LoginAdminUserRepository loginAdmUserRepo;
    
    @Autowired
    private InvestorRepository investorRepo;
    
    @Autowired
    private MemberRepository memberRepo;
    
    @Autowired
    private MemberRoleRepository memberRoleRepo;

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
//        	
//        	for (int i = 51; i <= 60; i++) {
//        		Investor investor = new Investor();
//        		investor.setInvestorCode("001C00000" + i);
//        		investor.setInvestorName("TKGD" + i);
//        		investor.setBrokerCode("00001");
//        		investor.setBrokerName("Nguyen Van A");
//        		investor.setCollaboratorCode("00100001001");
//        		investor.setCollaboratorName("Cong tac vien 1");
//        		investor.setMemberCode("004");
//        		investor.setMemberName("TVKD4");
//        		investor.setMemberId("5eb10bb282d7254fb02f063e");
//        		investor.setBrokerId("5eb00bb8e063150b87767ea4");
//        		investor.setCollaboratorId("5eb00c13e063150b87767eac");
//        		investorRepo.save(investor);
//        	}
//        };
//    }

}
