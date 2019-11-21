package com.sphereon.cas.did.auth.passwordless.config;

import com.sphereon.cas.did.auth.passwordless.authentication.PasswordlessDidAuthenticationHandler;
import com.sphereon.cas.did.auth.passwordless.callback.CallbackEndpointController;
import com.sphereon.cas.did.auth.passwordless.token.DidTokenRepository;
import com.sphereon.cas.did.auth.passwordless.token.InMemoryDidTokenRepository;
import com.sphereon.cas.did.auth.passwordless.web.flow.AcceptPasswordlessDidAuthenticationAction;
import com.sphereon.cas.did.auth.passwordless.web.flow.DisplayBeforePasswordlessDidAuthentication;
import com.sphereon.cas.did.auth.passwordless.web.flow.PrepareForPasswordlessDidAuthentication;
import com.sphereon.cas.did.auth.passwordless.web.flow.VerifyPasswordlessDidAuthenticationAction;
import com.sphereon.libs.did.auth.client.DidAuthFlow;
import com.sphereon.libs.did.auth.client.DidMappingService;
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
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.execution.Action;

@Configuration("passwordlessAuthenticationConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class PasswordlessDidAuthenticationConfiguration {

    private final String appId;
    private final DidMappingService didMappingService;
    private final DidAuthFlow didAuthFlow;

    private final CasConfigurationProperties casProperties;
    private final ObjectProvider<CasDelegatingWebflowEventResolver> initialAuthenticationAttemptWebflowEventResolver;
    private final ObjectProvider<CasWebflowEventResolver> serviceTicketRequestWebflowEventResolver;
    private final ObjectProvider<AdaptiveAuthenticationPolicy> adaptiveAuthenticationPolicy;
    private final ObjectProvider<AuthenticationSystemSupport> authenticationSystemSupport;
    private final ObjectProvider<ServicesManager> servicesManager;
    private final ObjectProvider<PrincipalResolver> defaultPrincipalResolver;

    public PasswordlessDidAuthenticationConfiguration(@Value("${sphereon.cas.did.auth.appId}") String appId,
                                                      DidMappingService didMappingService,
                                                      final DidAuthFlow didAuthFlow, CasConfigurationProperties casProperties,
                                                      @Qualifier("initialAuthenticationAttemptWebflowEventResolver") ObjectProvider<CasDelegatingWebflowEventResolver> initialAuthenticationAttemptWebflowEventResolver,
                                                      @Qualifier("serviceTicketRequestWebflowEventResolver") ObjectProvider<CasWebflowEventResolver> serviceTicketRequestWebflowEventResolver,
                                                      @Qualifier("adaptiveAuthenticationPolicy") ObjectProvider<AdaptiveAuthenticationPolicy> adaptiveAuthenticationPolicy,
                                                      @Qualifier("defaultAuthenticationSystemSupport") ObjectProvider<AuthenticationSystemSupport> authenticationSystemSupport,
                                                      @Qualifier("servicesManager") ObjectProvider<ServicesManager> servicesManager,
                                                      @Qualifier("defaultPrincipalResolver") ObjectProvider<PrincipalResolver> defaultPrincipalResolver) {
        this.appId = appId;
        this.didMappingService = didMappingService;
        this.didAuthFlow = didAuthFlow;
        this.casProperties = casProperties;
        this.initialAuthenticationAttemptWebflowEventResolver = initialAuthenticationAttemptWebflowEventResolver;
        this.serviceTicketRequestWebflowEventResolver = serviceTicketRequestWebflowEventResolver;
        this.adaptiveAuthenticationPolicy = adaptiveAuthenticationPolicy;
        this.authenticationSystemSupport = authenticationSystemSupport;
        this.servicesManager = servicesManager;
        this.defaultPrincipalResolver = defaultPrincipalResolver;
    }

    @Bean
    public CallbackEndpointController getCallbackEndpointController() {
        return new CallbackEndpointController();
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
        return new DisplayBeforePasswordlessDidAuthentication(didAuthFlow, didTokenRepository, appId);
    }

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean(name = "verifyPasswordlessAccountAuthenticationAction")
    public Action verifyPasswordlessAccountAuthenticationAction() {
        return new VerifyPasswordlessDidAuthenticationAction(didMappingService, appId);
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
                didAuthFlow,
                passwordlessTokenRepository());
    }

    @ConditionalOnMissingBean(name = "passwordlessAuthenticationEventExecutionPlanConfigurer")
    @Bean
    public AuthenticationEventExecutionPlanConfigurer passwordlessAuthenticationEventExecutionPlanConfigurer() {
        return plan -> plan.registerAuthenticationHandlerWithPrincipalResolver(
                passwordlessTokenAuthenticationHandler(), defaultPrincipalResolver.getObject());
    }


}
