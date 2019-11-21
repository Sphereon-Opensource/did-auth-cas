package com.sphereon.cas.did.auth.passwordless.web.flow;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.configurer.AbstractCasWebflowConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.action.EvaluateAction;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

public class PasswordlessDidAuthenticationWebflowConfigurer extends AbstractCasWebflowConfigurer {
    public static final String TRANSITION_ID_PASSWORDLESS_GET_USERID = "passwordlessGetUserId";

    private static final String STATE_ID_PASSWORDLESS_DISPLAY = "passwordlessDisplayUser";
    private static final String STATE_ID_PASSWORDLESS_VERIFY_ACCOUNT = "passwordlessVerifyAccount";

    private static final String STATE_ID_ACCEPT_PASSWORDLESS_AUTHENTICATION = "acceptPasswordlessAuthentication";
    private static final String STATE_ID_PASSWORDLESS_GET_USERID = "passwordlessGetUserIdView";

    public PasswordlessDidAuthenticationWebflowConfigurer(final FlowBuilderServices flowBuilderServices,
                                                       final FlowDefinitionRegistry loginFlowDefinitionRegistry,
                                                       final ApplicationContext applicationContext,
                                                       final CasConfigurationProperties casProperties) {
        super(flowBuilderServices, loginFlowDefinitionRegistry, applicationContext, casProperties);
    }

    @Override
    protected void doInitialize() {
        Flow flow = getLoginFlow();
        if (flow != null){
            ActionState state = getState(flow, CasWebflowConstants.STATE_ID_INIT_LOGIN_FORM, ActionState.class);
            createTransitionForState(state, TRANSITION_ID_PASSWORDLESS_GET_USERID, STATE_ID_PASSWORDLESS_GET_USERID);

            ViewState viewState = createViewState(flow, STATE_ID_PASSWORDLESS_GET_USERID, "casPasswordlessGetUserIdView");
            createTransitionForState(viewState, CasWebflowConstants.TRANSITION_ID_SUBMIT, STATE_ID_PASSWORDLESS_VERIFY_ACCOUNT);

            ActionState verifyAccountState = createActionState(flow, STATE_ID_PASSWORDLESS_VERIFY_ACCOUNT, "verifyPasswordlessAccountAuthenticationAction");
            createTransitionForState(verifyAccountState, CasWebflowConstants.TRANSITION_ID_ERROR, STATE_ID_PASSWORDLESS_GET_USERID);
            createTransitionForState(verifyAccountState, CasWebflowConstants.TRANSITION_ID_SUCCESS, STATE_ID_PASSWORDLESS_DISPLAY);

            ViewState viewStateDisplay = createViewState(flow, STATE_ID_PASSWORDLESS_DISPLAY, "casPasswordlessDisplayView");
            viewStateDisplay.getEntryActionList().add(createEvaluateAction("displayBeforePasswordlessAuthenticationAction"));
            createTransitionForState(viewStateDisplay, CasWebflowConstants.TRANSITION_ID_SUBMIT, STATE_ID_ACCEPT_PASSWORDLESS_AUTHENTICATION);

            EvaluateAction acceptAction = createEvaluateAction("acceptPasswordlessAuthenticationAction");
            ActionState acceptState = createActionState(flow, STATE_ID_ACCEPT_PASSWORDLESS_AUTHENTICATION, acceptAction);
            createTransitionForState(acceptState, CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE, STATE_ID_PASSWORDLESS_DISPLAY);

            ActionState submission = getState(flow, CasWebflowConstants.STATE_ID_REAL_SUBMIT, ActionState.class);
            Transition transition = (Transition) submission.getTransition(CasWebflowConstants.TRANSITION_ID_SUCCESS);
            String targetStateId = transition.getTargetStateId();
            createTransitionForState(acceptState, CasWebflowConstants.TRANSITION_ID_SUCCESS, targetStateId);

        }
    }
}
