package com.newgen.am;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import com.newgen.am.model.AuditorAwareImpl;
import com.newgen.am.repository.BrokerRepository;
import com.newgen.am.repository.InvestorRepository;
import com.newgen.am.service.InvestorService;
import com.newgen.am.service.LoginAdminUserService;
import com.newgen.am.service.RedisTestService;

@EnableMongoAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
public class NewgenAmApplication {
	private static ApplicationContext applicationContext;

	@Autowired
	private BrokerRepository brokerRepo;
	
	@Autowired
	private InvestorRepository investorRepo;
	
	@Autowired
	private InvestorService investorService;
	
	@Autowired
	private LoginAdminUserService loginAdmUserService;
	
	@Autowired
	private RedisTestService redisService;
	
    public static void main(String[] args) {
    	applicationContext = SpringApplication.run(NewgenAmApplication.class, args);
//        displayAllBeans();
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
//    public CommandLineRunner sendResetPasswordBroker() {
//    	return (args) -> {
//    		List<Broker> brokerList = brokerRepo.findAll();
//    		for (Broker broker : brokerList) {
//    			System.out.println("Reset password for: " + broker.getCode());
//    			LoginUserDataInputDTO input = new LoginUserDataInputDTO();
//    			input.setUsername(Constant.BROKER_USER_PREFIX + broker.getCode());
//    			input.setEmail(broker.getContact().getEmail());
//    			
//    			loginAdmUserService.resetAdminUserPassword2(input, 202101202l);
//    		}
//    		
//    	};
//    }
    
//    @Bean
//    public CommandLineRunner sendResetPasswordInvestor() {
//    	return (args) -> {
//    		List<InvestorDTO> investorList = investorService.listInvestors();
//    		
//    		System.out.println("Counts: " + investorList.size());
//    		for (InvestorDTO investor : investorList) {
//    			System.out.println("Reset password for: " + investor.getInvestorCode());
//    			LoginUserDataInputDTO input = new LoginUserDataInputDTO();
//    			input.setUsername(investor.getInvestorCode());
//    			input.setEmail(investor.getContact().getEmail());
//    			
//    			loginAdmUserService.resetInvestorUserPassword2(input, 20210127l);
//    		}
//    		
//    	};
//    }
}
