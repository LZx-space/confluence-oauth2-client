package org.lzx.confluence.oauth2.confluence;

import com.atlassian.confluence.user.ConfluenceAuthenticator;
import com.atlassian.seraph.auth.AuthenticatorException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

/**
 * 自定义Confluence认证器，为了可以直接调用{@link this#authoriseUserAndEstablishSession(HttpServletRequest, HttpServletResponse, Principal)}
 *
 * @author LZx
 * @since 2021/09/01
 */
@Slf4j
public class ConfluenceOAuth2Authenticator extends ConfluenceAuthenticator {

    @Override
    public boolean login(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            String username, String password, boolean setRememberMeCookie) throws AuthenticatorException {
        return super.login(httpServletRequest, httpServletResponse, username, password, setRememberMeCookie);
    }

    @Override
    public boolean authoriseUserAndEstablishSession(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse, Principal principal) {
        return super.authoriseUserAndEstablishSession(httpServletRequest, httpServletResponse, principal);
    }

}
