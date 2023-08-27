package org.lzx.confluence.oauth2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录失败处理器<br>
 * 该类耦合了一段JS{@link js/confluence-oauth2-client.js}
 *
 * @author LZx
 * @since 2021/09/10
 */
public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final String OAUTH2_LOGIN_ERROR_PARAM_NAME = "oauth2_login_error";

    private final String loginFailureRedirectUrl;

    public DefaultAuthenticationFailureHandler(String loginFailureRedirectUrl) {
        this.loginFailureRedirectUrl = loginFailureRedirectUrl;
    }

    @Override
    public void onFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.sendRedirect(loginFailureRedirectUrl +
                "?" + OAUTH2_LOGIN_ERROR_PARAM_NAME + "=" + exception.getLocalizedMessage());
    }

}
