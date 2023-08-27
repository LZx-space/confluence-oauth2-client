package org.lzx.confluence.oauth2.oauth2;

/**
 * 授权过程的常量
 *
 * @author LZx
 * @since 2021/09/02
 */
public interface AuthorizationConstants {

    /**
     * 授权请求state参数，保存在session中的属性名
     */
    String AUTHORIZATION_STATE_SESSION_ATTRIBUTE_NAME = "oauth2.authorization.state";

}
