package org.lzx.confluence.oauth2.config;

import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugins.osgi.javaconfig.configs.beans.ModuleFactoryBean;
import com.atlassian.plugins.osgi.javaconfig.configs.beans.PluginAccessorBean;
import org.lzx.confluence.oauth2.AuthenticationFailureHandler;
import org.lzx.confluence.oauth2.AuthenticationSuccessHandler;
import org.lzx.confluence.oauth2.DefaultAuthenticationFailureHandler;
import org.lzx.confluence.oauth2.DefaultAuthenticationSuccessHandler;
import org.lzx.confluence.oauth2.confluence.ConfluenceOAuth2Authenticator;
import org.lzx.confluence.oauth2.oauth2.OAuth2ClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.atlassian.plugins.osgi.javaconfig.OsgiServices.importOsgiService;

/**
 * OAuth2客户端配置
 *
 * @author LZx
 * @since 2021/08/31
 */
@Slf4j
@Configuration
@Import({
        ModuleFactoryBean.class,
        PluginAccessorBean.class
})
public class OAuth2ClientConfig {

    @Bean
    public UserAccessor userAccessor() {
        return importOsgiService(UserAccessor.class);
    }

    @Bean
    public PermissionManager permissionManager() {
        return importOsgiService(PermissionManager.class);
    }

    @Bean
    public ConfluenceOAuth2Authenticator authenticator() {
        return new ConfluenceOAuth2Authenticator();
    }

    @Configuration
    static class OAuth2LoginConfig {

        private final OAuth2ClientProperties oauth2ClientProperties;

        public OAuth2LoginConfig(OAuth2ClientProperties oauth2ClientProperties) {
            this.oauth2ClientProperties = oauth2ClientProperties;
        }

        @Bean
        public AuthenticationSuccessHandler authenticationSuccessHandler(
                UserAccessor userAccessor,
                PermissionManager permissionManager,
                ConfluenceOAuth2Authenticator authenticator) {
            return new DefaultAuthenticationSuccessHandler(
                    userAccessor, permissionManager, authenticator, oauth2ClientProperties);
        }

        @Bean
        public AuthenticationFailureHandler authenticationFailureHandler() {
            String failureUrl = oauth2ClientProperties.getLogin().getFailureUrl();
            return new DefaultAuthenticationFailureHandler(failureUrl);
        }

    }

}