package org.lzx.confluence.oauth2.oauth2.request.resolver;

import org.lzx.confluence.oauth2.AuthenticationException;
import org.lzx.confluence.oauth2.oauth2.OAuth2ClientProperties;
import org.lzx.confluence.oauth2.oauth2.AuthorizationConstants;
import org.lzx.confluence.oauth2.oauth2.request.TokenEndpointRequestAggregation;
import org.lzx.confluence.oauth2.util.AuthorizationRequestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 申请令牌（仅使用授权码）请求解析器
 *
 * @author LZx
 * @since 2021/09/02
 */
public class TokenEndpointRequestResolver {

    private static final String AUTHORIZATION_CODE_PARAM_NAME = "code";

    private static final String AUTHORIZATION_STATE_PARAM_NAME = "state";

    private static final String CLIENT_REGISTRATION_ID_REGEX_GROUP = "registrationId";

    private static final Pattern AUTHORIZATION_CODE_REQUEST_URI_PATTERN = Pattern
            .compile("^/confluence/plugins/servlet/login/oauth2/code/(?<" + CLIENT_REGISTRATION_ID_REGEX_GROUP + ">[a-z]+)$");

    private final OAuth2ClientProperties oauth2Clientproperties;

    public TokenEndpointRequestResolver(OAuth2ClientProperties oauth2Clientproperties) {
        this.oauth2Clientproperties = oauth2Clientproperties;
    }

    public TokenEndpointRequestAggregation resolve(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        Matcher matcher = AUTHORIZATION_CODE_REQUEST_URI_PATTERN.matcher(requestURI);
        if (!matcher.matches()) {
            throw new AuthenticationException("OAuth2_login_error_uri");
        }
        String authorizationCode = request.getParameter(AUTHORIZATION_CODE_PARAM_NAME);
        if (!StringUtils.hasText(authorizationCode)) {
            throw new AuthenticationException("invalid_request");
        }
        String state = request.getParameter(AUTHORIZATION_STATE_PARAM_NAME);
        validAuthorizationState(request, state);

        String registrationId = matcher.group(CLIENT_REGISTRATION_ID_REGEX_GROUP);
        OAuth2ClientProperties.Registration registration = AuthorizationRequestUtils.findClientRegistration(
                this.oauth2Clientproperties.getRegistration(), registrationId);
        OAuth2ClientProperties.Provider serverProvider = AuthorizationRequestUtils.findAuthorizationServerProvider(
                this.oauth2Clientproperties.getProvider(), registration.getProvider());
        return new TokenEndpointRequestAggregation()
                .setRequest(new TokenEndpointRequestAggregation.TokenEndpointRequest()
                        .setEndpointUri(serverProvider.getTokenUri())
                        .setCode(authorizationCode)
                        .setClientId(registration.getClientId())
                        .setClientSecret(registration.getClientSecret())
                        .setRedirectUri(registration.getRedirectUri())
                        .setAuthorizationGrantType(registration.getAuthorizationGrantType()))
                .setRegistration(registration)
                .setProvider(serverProvider);
    }

    private void validAuthorizationState(HttpServletRequest request, String submittedState) {
        if (submittedState == null) {
            throw new AuthenticationException("with_no_state_param");
        }
        if (!submittedState.equals(request.getSession().getAttribute(AuthorizationConstants.AUTHORIZATION_STATE_SESSION_ATTRIBUTE_NAME))) {
            throw new AuthenticationException("invalid_state_param");
        }
    }

}
