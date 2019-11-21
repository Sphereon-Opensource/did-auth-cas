package com.sphereon.cas.did.auth.callback;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration("testControllerConfig")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@EnableScheduling
public class CallbackEndpointControllerConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Bean
    public CallbackEndpointController getCallbackEndpointController(){
        return new CallbackEndpointController();
    }
}
