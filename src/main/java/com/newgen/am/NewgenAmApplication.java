package com.newgen.am;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import com.newgen.am.model.AuditorAwareImpl;

@EnableMongoAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
public class NewgenAmApplication {

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

}
