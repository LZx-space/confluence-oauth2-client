package org.lzx.confluence.oauth2.config;

import org.lzx.confluence.oauth2.oauth2.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 属性配置
 *
 * @author LZx
 * @since 2021/09/06
 */
@Configuration
@PropertySource("classpath:confluence-oauth2-client.properties")
public class PropertiesConfig {

    // todo 使用YAML抽象将属性值自动将赋值于对应绑定类对象的属性，see YamlPropertiesFactoryBean

    @Bean
    public OAuth2ClientProperties oauth2ClientProperties(Environment env) {
        OAuth2ClientProperties oauth2ClientProperties = new OAuth2ClientProperties();
        oauth2ClientProperties.setLogin(new OAuth2ClientProperties.Login()
                .setSuccessUrl(env.getRequiredProperty("oauth2.client.login.success-url"))
                .setFailureUrl(env.getRequiredProperty("oauth2.client.login.failure-url"))
        );
        oauth2ClientProperties.setCreateUser(new OAuth2ClientProperties.CreateUser()
                .setAdminGroup(env.getRequiredProperty("oauth2.client.create-user.admin-group"))
                .setDefaultUserGroup(env.getRequiredProperty("oauth2.client.create-user.default-user-group"))
                .setDefaultPassword(env.getRequiredProperty("oauth2.client.create-user.default-password"))
        );
        oauth2ClientProperties.getProvider().put("github", new OAuth2ClientProperties.Provider()
                .setAuthorizationUri(env.getRequiredProperty("oauth2.client.provider.github.authorization-uri"))
                .setTokenUri(env.getRequiredProperty("oauth2.client.provider.github.token-uri"))
                .setUserInfoUri(env.getRequiredProperty("oauth2.client.provider.github.user-info-uri"))
                .setUserNameAttribute(env.getRequiredProperty("oauth2.client.provider.github.user-name-attribute"))
        );
        oauth2ClientProperties.getRegistration().put("github", new OAuth2ClientProperties.Registration()
                .setProvider("github")
                .setClientId(env.getRequiredProperty("oauth2.client.registration.github.client-id"))
                .setClientSecret(env.getRequiredProperty("oauth2.client.registration.github.client-secret"))
                .setRedirectUri(env.getRequiredProperty("oauth2.client.registration.github.redirect-uri"))
                .setScopes(new HashSet<>(findList(env, "oauth2.client.registration.github.scopes")))
        );
        return oauth2ClientProperties;
    }

    /**
     * 获取数组或者集合属性
     *
     * @param env            当前运行的环境的各类变量
     * @param baseElementKey 除下标外Key的基础构成
     * @return 关联Key的属性集合
     */
    private static List<String> findList(Environment env, String baseElementKey) {
        List<String> result = new ArrayList<>();
        int idx = 0;
        String itemProperty;
        String itemKey = baseElementKey + "[" + idx + "]";
        while ((itemProperty = env.getProperty(itemKey)) != null) {
            result.add(itemProperty);
            itemKey = baseElementKey + "[" + ++idx + "]";
        }
        return result;
    }

}
