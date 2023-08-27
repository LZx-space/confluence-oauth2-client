package org.lzx.confluence.oauth2.servlet;

import org.lzx.confluence.oauth2.AuthenticationException;
import org.lzx.confluence.oauth2.AuthenticationFailureHandler;
import org.lzx.confluence.oauth2.AuthenticationSuccessHandler;
import org.lzx.confluence.oauth2.oauth2.response.AccessTokenResponse;
import org.lzx.confluence.oauth2.oauth2.OAuth2ClientProperties;
import org.lzx.confluence.oauth2.oauth2.response.UserInfo;
import org.lzx.confluence.oauth2.oauth2.request.TokenEndpointRequestAggregation;
import org.lzx.confluence.oauth2.oauth2.request.resolver.TokenEndpointRequestResolver;
import org.lzx.confluence.oauth2.oauth2.response.converter.UserInfoConverter;
import org.lzx.confluence.oauth2.oauth2.response.handler.AccessTokenResponseHandler;
import org.lzx.confluence.oauth2.oauth2.response.handler.UserInfoResponseHandler;
import org.lzx.confluence.oauth2.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.web.servlet.HttpServletBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

/**
 * OAuth2客户端登录Servlet
 *
 * @author LZx
 * @since 2021/08/31
 */
@Slf4j
public class OAuth2LoginServlet extends HttpServletBean {

    private final AuthenticationSuccessHandler successHandler;

    private final AuthenticationFailureHandler failureHandler;

    private final TokenEndpointRequestResolver tokenEndpointRequestResolver;

    public OAuth2LoginServlet(
            OAuth2ClientProperties oauth2Clientproperties,
            AuthenticationSuccessHandler successHandler,
            AuthenticationFailureHandler failureHandler) {
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.tokenEndpointRequestResolver = new TokenEndpointRequestResolver(oauth2Clientproperties);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            TokenEndpointRequestAggregation requestAggregation = tokenEndpointRequestResolver.resolve(request);
            AccessTokenResponse accessTokenResponse = applyAccessToken(requestAggregation.getRequest());

            OAuth2ClientProperties.Provider provider = requestAggregation.getProvider();
            Map<String, Object> userInfoMap = applyUserInfo(accessTokenResponse, provider);
            UserInfo userInfo = new UserInfoConverter(provider.getUserNameAttribute())
                    .convert(userInfoMap);
            successHandler.onSuccess(request, response, userInfo);
        } catch (AuthenticationException exception) {
            log.error("OAuth2授权用户信息登录失败", exception);
            failureHandler.onFailure(request, response, exception);
        } catch (Exception exception) {
            log.error("OAuth2授权用户信息登录失败", exception);
            failureHandler.onFailure(request, response, new AuthenticationException(exception));
        }
    }

    /**
     * 申请访问令牌
     *
     * @param request 请求
     * @return 访问令牌
     * @throws IOException 请求的网络异常
     */
    private AccessTokenResponse applyAccessToken(
            TokenEndpointRequestAggregation.TokenEndpointRequest request) throws IOException {
        HttpEntity httpEntity = new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("code", request.getCode()),
                new BasicNameValuePair("client_id", request.getClientId()),
                new BasicNameValuePair("client_secret", request.getClientSecret()),
                new BasicNameValuePair("grant_type", request.getAuthorizationGrantType()),
                new BasicNameValuePair("redirect_uri", request.getRedirectUri())
        ), StandardCharsets.UTF_8);
        HttpPost httpPost = new HttpPost(request.getEndpointUri());
        httpPost.setEntity(httpEntity);
        return HttpClientUtils.execute(httpPost, new AccessTokenResponseHandler());
    }

    /**
     * 申请用户信息
     *
     * @param accessTokenResponse 申请的令牌的信息
     * @return 用户信息
     * @throws IOException 请求的网络异常
     */
    private Map<String, Object> applyUserInfo(
            AccessTokenResponse accessTokenResponse,
            OAuth2ClientProperties.Provider provider) throws IOException {
        String accessToken = accessTokenResponse.getAccessToken();
        HttpGet httpGet = new HttpGet(provider.getUserInfoUri());
        httpGet.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        return HttpClientUtils.execute(httpGet, new UserInfoResponseHandler());
    }

}
