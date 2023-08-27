package org.lzx.confluence.oauth2.oauth2.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * OAuth2授权端点的请求
 *
 * @author LZx
 * @since 2021/09/02
 */
@Data
@Accessors(chain = true)
public class AuthorizeEndpointRequest implements Serializable {

    private final String authorizationGrantType = "authorization_code";

    private final String authorizationResponseType = "code";

    private String endpointUri;

    private String endpointRequestUri;

    private String clientId;

    private String redirectUri;

    private Set<String> scopes = new HashSet<>();

    /**
     * OAuth2协议推荐使用的参数
     */
    private String state;

}
