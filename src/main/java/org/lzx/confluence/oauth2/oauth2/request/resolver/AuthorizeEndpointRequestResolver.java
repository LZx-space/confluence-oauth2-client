package org.lzx.confluence.oauth2.oauth2.request.resolver;

import org.lzx.confluence.oauth2.AuthenticationException;
import org.lzx.confluence.oauth2.oauth2.OAuth2ClientProperties;
import org.lzx.confluence.oauth2.oauth2.AuthorizationConstants;
import org.lzx.confluence.oauth2.oauth2.request.AuthorizeEndpointRequest;
import org.lzx.confluence.oauth2.util.AuthorizationRequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 授权请求解析器
 *
 * @author LZx
 * @since 2021/09/02
 */
public class AuthorizeEndpointRequestResolver {

    private static final String SERVER_REGISTRATION_ID_REGEX_GROUP = "registrationId";

    private static final Pattern AUTHORIZATION_REQUEST_URI_PATTERN = Pattern
            .compile("^/confluence/plugins/servlet/oauth2/authorization/(?<" + SERVER_REGISTRATION_ID_REGEX_GROUP + ">[a-z]+)$");

    private final OAuth2ClientProperties oAuth2ClientProperties;

    public AuthorizeEndpointRequestResolver(OAuth2ClientProperties oAuth2ClientProperties) {
        this.oAuth2ClientProperties = oAuth2ClientProperties;
    }

    public AuthorizeEndpointRequest resolve(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        Matcher matcher = AUTHORIZATION_REQUEST_URI_PATTERN.matcher(requestURI);
        if (!matcher.matches()) {
            throw new AuthenticationException("OAuth2_authorize_error_uri");
        }
        String registrationId = matcher.group(SERVER_REGISTRATION_ID_REGEX_GROUP);
        OAuth2ClientProperties.Registration registration = AuthorizationRequestUtils.findClientRegistration(
                this.oAuth2ClientProperties.getRegistration(), registrationId);
        OAuth2ClientProperties.Provider serverProvider = AuthorizationRequestUtils.findAuthorizationServerProvider(
                this.oAuth2ClientProperties.getProvider(), registration.getProvider());

        String authorizationUri = serverProvider.getAuthorizationUri();
        String state = UUID.randomUUID().toString().replace("-", "");
        storeAuthorizationState(request, state);
        return new AuthorizeEndpointRequest()
                .setEndpointUri(authorizationUri)
                .setEndpointRequestUri(buildEndpointRequestUri(registration, authorizationUri, state))
                .setClientId(registration.getClientId())
                .setRedirectUri(registration.getRedirectUri())
                .setScopes(registration.getScopes())
                .setState(state);
    }

    /**
     * 保存授权请求的state，该state参数的作用参见OAuth2协议
     *
     * @param request 请求
     * @param state   响应
     */
    private void storeAuthorizationState(HttpServletRequest request, String state) {
        request.getSession().setAttribute(AuthorizationConstants.AUTHORIZATION_STATE_SESSION_ATTRIBUTE_NAME, state);
    }

    private String buildEndpointRequestUri(
            OAuth2ClientProperties.Registration registration,
            String authorizeEndpointUri,
            String state) {
        return authorizeEndpointUri +
                "?response_type=code" +
                "&client_id=" + registration.getClientId() +
                "&redirect_uri=" + registration.getRedirectUri() +
                "&scope=" + String.join(",", registration.getScopes()) +
                "&state=" + state;
    }

}
