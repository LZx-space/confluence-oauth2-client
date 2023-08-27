package org.lzx.confluence.oauth2.util;

import org.lzx.confluence.oauth2.AuthenticationException;
import org.lzx.confluence.oauth2.oauth2.OAuth2ClientProperties;

import java.util.Map;

/**
 * @author LZx
 * @since 2021/09/01
 */
public class AuthorizationRequestUtils {

    public static OAuth2ClientProperties.Registration findClientRegistration(
            Map<String, OAuth2ClientProperties.Registration> clientRegistrations, String clientRegistrationId) {
        OAuth2ClientProperties.Registration registration = clientRegistrations.get(clientRegistrationId);
        if (registration == null) {
            throw new AuthenticationException("未找到名为" + clientRegistrationId + "的本地注册的客户端配置");
        }
        return registration;
    }

    public static OAuth2ClientProperties.Provider findAuthorizationServerProvider(
            Map<String, OAuth2ClientProperties.Provider> authorizationServerProviders, String provider) {
        OAuth2ClientProperties.Provider serverProvider = authorizationServerProviders.get(provider);
        if (serverProvider == null) {
            throw new AuthenticationException("未找到名为" + provider + "的本地注册的授权服务器配置");
        }
        return serverProvider;
    }

}
