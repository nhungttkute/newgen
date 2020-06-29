package com.newgen.am;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import com.newgen.am.model.AuditorAwareImpl;

@EnableMongoAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
public class NewgenAmApplication {
	private static ApplicationContext applicationContext;

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

    public static void displayAllBeans() {
        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
        for(String beanName : allBeanNames) {
            System.out.println(beanName);
        }
    }
    
//    @Bean
//    public CommandLineRunner dummyData() {
//    	return (args) -> {
//    		for (int i = 0; i <= 50; i++) {
//    			InvestorMarginTransaction trans = new InvestorMarginTransaction();
//    			trans.setMemberCode("001");
//    			trans.setMemberCode("TVKD1");
//    			trans.setBrokerCode("00100001");
//    			trans.setBrokerName("Nguyen Van A");
//    			trans.setCollaboratorCode("001001");
//    			trans.setCollaboratorName("Cong tac vien 1");
//    			trans.setInvestorCode("001C000001");
//    			trans.setInvestorName("Bui Thi Lien Huong");
//    			if (i%2 == 0) {
//    				trans.setTransactionType(Constant.MARGIN_TRANS_TYPE_DEPOSIT);
//    			} else {
//    				trans.setTransactionType(Constant.MARGIN_TRANS_TYPE_WITHDRAW);
//    			}
//    			trans.setAmount(500000000);
//    			trans.setCurrency(Constant.CURRENCY_VND);
//    			trans.setApprovalUser("nhungtran");
//    			trans.setApprovalDate(System.currentTimeMillis());
//    			trans.setNote("test");
//    			marginTransRepo.save(trans);
//    		}
//    		
//    		List<Investor> investorList = investorRepo.findAll();
//    		for (Investor inv : investorList) {
//    			InvestorMarginInfo marginInfo = new InvestorMarginInfo();
//    			marginInfo.setInvestorCode(inv.getInvestorCode());
//    			if (inv.getAccount() != null) {
//    				marginInfo.setMarginSurplusInterestRate(inv.getAccount().getMarginSurplusInterestRate());
//        			marginInfo.setMarginDeficitInterestRate(inv.getAccount().getMarginDeficitInterestRate());
//        			marginInfo.setSodBalance(inv.getAccount().getSodBalance());
//        			marginInfo.setChangedAmount(inv.getAccount().getChangedAmount());
//    			}
//    			
//    			marginInfo.setGeneralFee(inv.getGeneralFee());
//    			marginInfo.setOtherFee(inv.getOtherFee());
//    			invMarginInfoRepo.save(marginInfo);
//    		}
//    	};
//    }
}
