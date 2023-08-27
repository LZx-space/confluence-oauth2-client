package org.lzx.confluence.oauth2.servlet;

import org.lzx.confluence.oauth2.AuthenticationException;
import org.lzx.confluence.oauth2.AuthenticationFailureHandler;
import org.lzx.confluence.oauth2.oauth2.OAuth2ClientProperties;
import org.lzx.confluence.oauth2.oauth2.request.AuthorizeEndpointRequest;
import org.lzx.confluence.oauth2.oauth2.request.resolver.AuthorizeEndpointRequestResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HttpServletBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * OAuth2授权请求重定向Servlet
 * <ul>
 *     <li>获取意图使用的本地注册的客户端信息</li>
 *     <li>获取意图请求授权的授权服务器信息</li>
 *     <li>构造请求URI</li>
 *     <li>上述URI做302响应</li>
 * </ul>
 *
 * @author LZx
 * @since 2021/09/01
 */
@Slf4j
public class OAuth2AuthorizationRequestRedirectServlet extends HttpServletBean {

    private final AuthorizeEndpointRequestResolver authorizeEndpointRequestResolver;

    private final AuthenticationFailureHandler failureHandler;

    public OAuth2AuthorizationRequestRedirectServlet(
            OAuth2ClientProperties oAuth2ClientProperties,
            AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
        this.authorizeEndpointRequestResolver = new AuthorizeEndpointRequestResolver(
                oAuth2ClientProperties);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            AuthorizeEndpointRequest authorizeEndpointRequest = authorizeEndpointRequestResolver.resolve(request);
            response.sendRedirect(authorizeEndpointRequest.getEndpointRequestUri());
        } catch (AuthenticationException exception) {
            log.error("OAuth2构造授权请求失败", exception);
            failureHandler.onFailure(request, response, exception);
        } catch (Exception exception) {
            log.error("OAuth2构造授权请求失败", exception);
            failureHandler.onFailure(request, response, new AuthenticationException(exception));
        }
    }

}
