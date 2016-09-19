package com.techdegree.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.validation.Validator;

@Configuration
// we extend this REST configuration class to use our validators on
// @Entity marked classes
public class RestConfig extends RepositoryRestConfigurerAdapter {
    // lazy is added here because this is bug, see here
    //  https://jira.spring.io/browse/DATAREST-807
    @Autowired
    @Lazy
    private Validator validator;

    @Override
    public void configureValidatingRepositoryEventListener(
            ValidatingRepositoryEventListener validatingListener) {
        // validation occurs before creation of new resource
        // and before update
        validatingListener.addValidator("beforeCreate", validator);
        validatingListener.addValidator("beforeSave", validator);
    }
}
