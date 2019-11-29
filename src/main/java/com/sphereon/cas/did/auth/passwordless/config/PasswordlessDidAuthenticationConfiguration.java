package com.sphereon.cas.did.auth.passwordless.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sphereon.cas.did.auth.passwordless.authentication.PasswordlessDidAuthenticationHandler;
import com.sphereon.cas.did.auth.passwordless.callback.CallbackEndpointController;
import com.sphereon.cas.did.auth.passwordless.token.DidTokenRepository;
import com.sphereon.cas.did.auth.passwordless.token.InMemoryDidTokenRepository;
import com.sphereon.cas.did.auth.passwordless.web.flow.AcceptPasswordlessDidAuthenticationAction;
import com.sphereon.cas.did.auth.passwordless.web.flow.CheckResponseExistencePasswordlessDidAuthenticationAction;
import com.sphereon.cas.did.auth.passwordless.web.flow.DisplayBeforePasswordlessDidAuthentication;
import com.sphereon.cas.did.auth.passwordless.web.flow.PasswordlessDidAuthenticationWebflowConfigurer;
import com.sphereon.cas.did.auth.passwordless.web.flow.PrepareForPasswordlessDidAuthentication;
import com.sphereon.cas.did.auth.passwordless.web.flow.VerifyPasswordlessDidAuthenticationAction;
import com.sphereon.libs.did.auth.client.DidAuthFlow;
import com.sphereon.libs.did.auth.client.DidMappingService;
import com.sphereon.libs.did.auth.client.DisclosureRequestService;
import com.sphereon.libs.did.auth.client.api.DidTransportsControllerApi;
import com.sphereon.sdk.did.mapping.api.DidMapControllerApi;
import com.sphereon.sdk.did.mapping.handler.ApiClient;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.passwordless.PasswordlessAuthenticationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowExecutionPlan;
import org.apereo.cas.web.flow.CasWebflowExecutionPlanConfigurer;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

import java.net.http.HttpClient;

@Configuration("passwordlessAuthenticationConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class PasswordlessDidAuthenticationConfiguration implements CasWebflowExecutionPlanConfigurer {

    private final int didMapPort;
    private final String didTransportsUrl;
    private final String didMapHost;
    private final String appDid;
    private final String appSecret;
    private final String appId;
    private final String baseCasUrl;

    private final CasConfigurationProperties casProperties;
    private final ObjectProvider<CasDelegatingWebflowEventResolver> initialAuthenticationAttemptWebflowEventResolver;
    private final ObjectProvider<CasWebflowEventResolver> serviceTicketRequestWebflowEventResolver;
    private final ObjectProvider<AdaptiveAuthenticationPolicy> adaptiveAuthenticationPolicy;
    private final ObjectProvider<AuthenticationSystemSupport> authenticationSystemSupport;
    private final ObjectProvider<ServicesManager> servicesManager;
    private final ObjectProvider<PrincipalResolver> defaultPrincipalResolver;
    private final ApplicationContext applicationContext;
    private final FlowBuilderServices flowBuilderServices;
    private final ObjectProvider<FlowDefinitionRegistry> loginFlowDefinitionRegistry;

    public PasswordlessDidAuthenticationConfiguration(@Value("${sphereon.cas.did.auth.appId}") String appId,
                                                      CasConfigurationProperties casProperties,
                                                      @Qualifier("initialAuthenticationAttemptWebflowEventResolver") ObjectProvider<CasDelegatingWebflowEventResolver> initialAuthenticationAttemptWebflowEventResolver,
                                                      @Qualifier("serviceTicketRequestWebflowEventResolver") ObjectProvider<CasWebflowEventResolver> serviceTicketRequestWebflowEventResolver,
                                                      @Qualifier("adaptiveAuthenticationPolicy") ObjectProvider<AdaptiveAuthenticationPolicy> adaptiveAuthenticationPolicy,
                                                      @Qualifier("defaultAuthenticationSystemSupport") ObjectProvider<AuthenticationSystemSupport> authenticationSystemSupport,
                                                      @Qualifier("servicesManager") ObjectProvider<ServicesManager> servicesManager,
                                                      @Qualifier("defaultPrincipalResolver") ObjectProvider<PrincipalResolver> defaultPrincipalResolver,
                                                      @Value("${sphereon.cas.did.auth.didMapPort}") final int didMapPort,
                                                      @Value("${sphereon.cas.did.auth.didMapHost}") final String didMapHost,
                                                      @Value("${sphereon.cas.did.auth.didTransportsUrl}") final String didTransportsUrl,
                                                      @Value("${sphereon.cas.did.auth.appDid}") final String appDid,
                                                      @Value("${sphereon.cas.did.auth.appSecret}") final String appSecret,
                                                      @Value("${sphereon.cas.did.auth.baseCasUrl}") final String baseCasUrl,
                                                      ApplicationContext applicationContext,
                                                      FlowBuilderServices flowBuilderServices,
                                                      @Qualifier("loginFlowRegistry") ObjectProvider<FlowDefinitionRegistry> loginFlowDefinitionRegistry) {
        this.didMapPort = didMapPort;
        this.didMapHost = didMapHost;
        this.didTransportsUrl = didTransportsUrl;
        this.appDid = appDid;
        this.appSecret = appSecret;
        this.appId = appId;
        this.casProperties = casProperties;
        this.initialAuthenticationAttemptWebflowEventResolver = initialAuthenticationAttemptWebflowEventResolver;
        this.serviceTicketRequestWebflowEventResolver = serviceTicketRequestWebflowEventResolver;
        this.adaptiveAuthenticationPolicy = adaptiveAuthenticationPolicy;
        this.authenticationSystemSupport = authenticationSystemSupport;
        this.servicesManager = servicesManager;
        this.defaultPrincipalResolver = defaultPrincipalResolver;
        this.applicationContext = applicationContext;
        this.flowBuilderServices = flowBuilderServices;
        this.loginFlowDefinitionRegistry = loginFlowDefinitionRegistry;
        this.baseCasUrl = baseCasUrl;
    }

    /*
    ##################################
    ##  Did Auth Client Configuration
    ##################################
     */

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
        var httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        return new DidTransportsControllerApi(httpClient,
                didTransportsUrl,
                new ObjectMapper());
    }

    @Bean
    public DisclosureRequestService disclosureRequestService() {
        return new DisclosureRequestService(appDid, appSecret);
    }

    @Bean
    public DidAuthFlow didAuthFlow() {
        return new DidAuthFlow(didMappingService(), didTransportsControllerApi(), disclosureRequestService());
    }

    @Bean
    public PrincipalFactory passwordlessPrincipalFactory() {
        return PrincipalFactoryUtils.newPrincipalFactory();
    }

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean(name = "passwordlessTokenRepository")
    public DidTokenRepository passwordlessTokenRepository() {
        PasswordlessAuthenticationProperties.Tokens tokens = casProperties.getAuthn().getPasswordless().getTokens();
        return new InMemoryDidTokenRepository(tokens.getExpireInSeconds());
    }

    @Bean
    public CallbackEndpointController getCallbackEndpointController() {
        return new CallbackEndpointController(passwordlessTokenRepository());
    }

    /*
    ####################
    # Webflow Bean Configuration
    #####################
     */

    @Bean
    public Action initializeLoginAction() {
        return new PrepareForPasswordlessDidAuthentication(servicesManager.getObject());
    }

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean(name = "displayBeforePasswordlessAuthenticationAction")
    public Action displayBeforePasswordlessAuthenticationAction() {
        return new DisplayBeforePasswordlessDidAuthentication();
    }

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean(name = "verifyPasswordlessAccountAuthenticationAction")
    public Action verifyPasswordlessAccountAuthenticationAction() {
        return new VerifyPasswordlessDidAuthenticationAction(passwordlessTokenRepository(), didAuthFlow(), appId, baseCasUrl);
    }

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean(name = "checkResponseExistencePasswordlessDidAuthenticationAction")
    public Action checkResponseExistencePasswordlessDidAuthenticationAction() {
        return new CheckResponseExistencePasswordlessDidAuthenticationAction(passwordlessTokenRepository());
    }

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean(name = "acceptPasswordlessAuthenticationAction")
    public Action acceptPasswordlessAuthenticationAction() {
        return new AcceptPasswordlessDidAuthenticationAction(initialAuthenticationAttemptWebflowEventResolver.getObject(),
                serviceTicketRequestWebflowEventResolver.getObject(),
                adaptiveAuthenticationPolicy.getObject(),
                passwordlessTokenRepository(),
                authenticationSystemSupport.getObject());
    }

    @RefreshScope
    @Bean
    @ConditionalOnMissingBean(name = "passwordlessTokenAuthenticationHandler")
    public AuthenticationHandler passwordlessTokenAuthenticationHandler() {
        return new PasswordlessDidAuthenticationHandler(null,
                servicesManager.getObject(),
                passwordlessPrincipalFactory(),
                null,
                didAuthFlow());
    }

    @Bean
    @ConditionalOnMissingBean(name = "passwordlessAuthenticationEventExecutionPlanConfigurer")
    public AuthenticationEventExecutionPlanConfigurer passwordlessAuthenticationEventExecutionPlanConfigurer() {
        return plan -> plan.registerAuthenticationHandlerWithPrincipalResolver(
                passwordlessTokenAuthenticationHandler(), defaultPrincipalResolver.getObject());
    }

    @ConditionalOnMissingBean(name = "passwordlessAuthenticationWebflowConfigurer")
    @Bean
    @DependsOn("defaultWebflowConfigurer")
    public CasWebflowConfigurer passwordlessAuthenticationWebflowConfigurer() {
        return new PasswordlessDidAuthenticationWebflowConfigurer(flowBuilderServices,
                loginFlowDefinitionRegistry.getIfAvailable(), applicationContext, casProperties);
    }

    @Override
    public void configureWebflowExecutionPlan(final CasWebflowExecutionPlan plan) {
        plan.registerWebflowConfigurer(passwordlessAuthenticationWebflowConfigurer());
    }
}
