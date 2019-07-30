package com.cas.server.config;

import com.cas.server.handler.CustomerHandlerAuthentication;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaocxu
 */
@Configuration("CustomAuthenticationConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CustomAuthenticationConfiguration implements AuthenticationEventExecutionPlanConfigurer {
    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Bean
    public AuthenticationHandler myAuthenticationHandler(){
        CustomerHandlerAuthentication handler = new CustomerHandlerAuthentication(CustomerHandlerAuthentication.class.getName(), servicesManager, new DefaultPrincipalFactory(), 1);
        return handler;
    }

    @Override
    public void configureAuthenticationExecutionPlan( AuthenticationEventExecutionPlan plan){
        plan.registerAuthenticationHandler(myAuthenticationHandler());
    }
}
