package com.sphereon.cas.did.auth.simpleexample;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("ExampleAuthenticationExecutionPlanConfiguration")
public class ExampleAuthenticationExecutionPlanConfiguration implements AuthenticationEventExecutionPlanConfigurer {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    private ServicesManager servicesManager;

    @Autowired
    PrincipalFactory principalFactory;

    @Bean
    public AuthenticationHandler myAuthenticationHandler() {
        final ExampleAuthenticationHandler handler = new ExampleAuthenticationHandler("Example", servicesManager,
                principalFactory, 0);
        /*
            Configure the handler by invoking various setter methods.
            Note that you also have full access to the collection of resolved CAS settings.
            Note that each authentication handler may optionally qualify for an 'order`
            as well as a unique name.
        */
        return handler;
    }

    @Override
    public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
        if ( true ) {
            plan.registerAuthenticationHandler(myAuthenticationHandler());
        }
    }
}

