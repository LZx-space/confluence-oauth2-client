package org.lzx.confluence.oauth2;

/**
 * 认证异常，整个过程包含授权，最后的目标是认证
 *
 * @author LZx
 * @since 2021/08/31
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(Throwable throwable) {
        super(throwable);
    }

    public AuthenticationException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
