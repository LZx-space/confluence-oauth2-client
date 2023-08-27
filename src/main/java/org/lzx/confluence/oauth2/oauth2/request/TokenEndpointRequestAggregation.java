package org.lzx.confluence.oauth2.oauth2.request;

import org.lzx.confluence.oauth2.oauth2.OAuth2ClientProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * OAuth2令牌端点的请求
 *
 * @author LZx
 * @since 2021/09/02
 */
@Data
@Accessors(chain = true)
public class TokenEndpointRequestAggregation {

    private TokenEndpointRequest request;

    private OAuth2ClientProperties.Provider provider;

    private OAuth2ClientProperties.Registration registration;

    @Data
    @Accessors(chain = true)
    public static class TokenEndpointRequest implements Serializable {

        private String endpointUri;

        private String clientId;

        private String clientSecret;

        private String code;

        private String authorizationGrantType;

        private String redirectUri;

    }

}
