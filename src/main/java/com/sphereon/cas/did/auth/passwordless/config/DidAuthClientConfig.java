package com.sphereon.cas.did.auth.passwordless.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sphereon.libs.did.auth.client.DidAuthFlow;
import com.sphereon.libs.did.auth.client.DidMappingService;
import com.sphereon.libs.did.auth.client.DisclosureRequestService;
import com.sphereon.libs.did.auth.client.api.DidTransportsControllerApi;
import com.sphereon.sdk.did.mapping.api.DidMapControllerApi;
import com.sphereon.sdk.did.mapping.handler.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class DidAuthClientConfig {


    private final int didMapPort;
    private final String didTransportsUrl;
    private final String didMapHost;
    private final String appDid;
    private final String appSecret;

    public DidAuthClientConfig(@Value("${sphereon.cas.did.auth.didMapPort}") final int didMapPort,
                               @Value("${sphereon.cas.did.auth.didMapHost}") final String didMapHost,
                               @Value("${sphereon.cas.did.auth.didTransportsUrl}") final String didTransportsUrl,
                               @Value("${sphereon.cas.did.auth.appDid}") final String appDid,
                               @Value("${sphereon.cas.did.auth.appSecret}") final String appSecret) {
        this.didMapPort = didMapPort;
        this.didMapHost = didMapHost;
        this.didTransportsUrl = didTransportsUrl;
        this.appDid = appDid;
        this.appSecret = appSecret;
    }

    @Bean
    public DidMapControllerApi didMapControllerApi() {
        ApiClient defaultClient = com.sphereon.sdk.did.mapping.handler.Configuration.getDefaultApiClient();
        defaultClient.setHost(didMapHost);
        defaultClient.setPort(didMapPort);
        return new DidMapControllerApi(defaultClient);
    }

    @Bean
    public DidMappingService didMappingService() {
        return new DidMappingService(didMapControllerApi());
    }

    @Bean
    public DidTransportsControllerApi didTransportsControllerApi() {
        var httpClient = HttpClient.newHttpClient();
        return new DidTransportsControllerApi(httpClient,
                didTransportsUrl,
                new ObjectMapper());
    }

    @Bean
    public DisclosureRequestService disclosureRequestService(){
        return new DisclosureRequestService(appDid, appSecret);
    }

    @Bean
    public DidAuthFlow didAuthFlow(){
        return new DidAuthFlow(didMappingService(), didTransportsControllerApi(), disclosureRequestService());
    }
}
