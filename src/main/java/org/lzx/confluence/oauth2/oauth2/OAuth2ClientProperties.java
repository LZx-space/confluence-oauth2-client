package org.lzx.confluence.oauth2.oauth2;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 配置属性绑定类
 *
 * @author LZx
 * @since 2021/09/06
 */
@Data
public class OAuth2ClientProperties implements InitializingBean {

    private Login login = new Login();

    private CreateUser createUser = new CreateUser();

    private final Map<String, Provider> provider = new HashMap<>();

    private final Map<String, Registration> registration = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        registration.forEach((key, value) -> {
            if (!provider.containsKey(value.getProvider())) {
                throw new InvalidPropertyException(OAuth2ClientProperties.class, key, "不存在对应的provider");
            }
        });
    }

    /**
     * 登录配置
     *
     * @author LZx
     * @since 2021/09/01
     */
    @Data
    @Accessors(chain = true)
    public static class Login {

        private String successUrl;

        private String failureUrl;

    }

    /**
     * 创建用户的配置
     *
     * @author LZx
     * @since 2021/09/10
     */
    @Data
    @Accessors(chain = true)
    public static class CreateUser {

        private String adminGroup;

        private String defaultUserGroup;

        private String defaultPassword;

    }

    /**
     * 授权服务器提供者
     *
     * @author LZx
     * @since 2021/09/01
     */
    @Data
    @Accessors(chain = true)
    public static class Provider {

        private String authorizationUri;

        private String tokenUri;

        private String userInfoUri;

        private String userNameAttribute;

    }

    /**
     * 注册的客户端
     *
     * @author LZx
     * @since 2021/08/31
     */
    @Data
    @Accessors(chain = true)
    public static class Registration {

        /**
         * 认证服务器信息提供者的注册名
         */
        private String provider;

        private String clientId;

        private String clientSecret;

        private String redirectUri;

        private Set<String> scopes = new HashSet<>();

        public final String getAuthorizationGrantType() {
            return "authorization_code";
        }

    }

}
