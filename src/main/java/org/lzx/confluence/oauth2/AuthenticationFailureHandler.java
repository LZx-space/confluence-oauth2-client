package org.lzx.confluence.oauth2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录失败处理器
 *
 * @author LZx
 * @since 2021/08/31
 */
public interface AuthenticationFailureHandler {

    /**
     * 认证成功处理器
     *
     * @param request   请求
     * @param response  响应
     * @param exception 用户信息
     */
    void onFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException;

}
