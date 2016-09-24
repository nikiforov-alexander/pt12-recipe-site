package com.techdegree.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class AppConfig {
    // adding validator @Bean, first because
    // Intellijidea is complaining, and second
    // because I think it is good idea to define beans
    // that we use
    // taken from here:
    // http://stackoverflow.com/questions/23604540/spring-boot-how-to-properly-inject-javax-validation-validator
    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }
}
