package com.newgen.am;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import com.newgen.am.common.Constant;
import com.newgen.am.model.AuditorAwareImpl;
import com.newgen.am.model.Investor;
import com.newgen.am.model.InvestorMarginInfo;
import com.newgen.am.model.InvestorMarginTransaction;
import com.newgen.am.repository.InvestorMarginTransactionRepository;
import com.newgen.am.service.InvestorMarginInfoService;

@EnableMongoAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
public class NewgenAmApplication {
	private static ApplicationContext applicationContext;

	@Autowired
	private InvestorMarginTransactionRepository marginTransRepo;
	
	@Autowired
	private InvestorMarginInfoService marginInfoService;
	
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

//    public static void displayAllBeans() {
//        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
//        for(String beanName : allBeanNames) {
//            System.out.println(beanName);
//        }
//    }
    
    @Bean
    public CommandLineRunner dummyData() {
    	return (args) -> {
    		List<InvestorMarginTransaction> marginTransList = marginTransRepo.findAll();
    		for (InvestorMarginTransaction marginTrans : marginTransList) {
    			InvestorMarginInfo marginInfo = marginInfoService.getInvestorMarginInfo(marginTrans.getInvestorCode(), 1);
    			
    			marginTrans.setMemberCode(marginInfo.getMemberCode());
				marginTrans.setMemberName(marginInfo.getMemberName());
				marginTrans.setBrokerCode(marginInfo.getBrokerCode());
				marginTrans.setBrokerName(marginInfo.getBrokerName());
				marginTrans.setCollaboratorCode(marginInfo.getCollaboratorCode());
				marginTrans.setCollaboratorName(marginInfo.getCollaboratorName());
				
				marginTransRepo.save(marginTrans);
    		}
    	};
    }
}
