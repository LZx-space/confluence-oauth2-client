package org.lzx.confluence.oauth2;

import com.atlassian.seraph.auth.AuthenticatorException;
import org.lzx.confluence.oauth2.oauth2.response.UserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * 登录成功处理器
 *
 * @author LZx
 * @since 2021/08/31
 */
public interface AuthenticationSuccessHandler {

    /**
     * 认证成功处理器
     *
     * @param request  请求
     * @param response 响应
     * @param userInfo 用户信息
     */
    void onSuccess(HttpServletRequest request, HttpServletResponse response, UserInfo userInfo) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, AuthenticatorException;

}
