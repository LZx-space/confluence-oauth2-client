package org.lzx.confluence.oauth2;

import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.Group;
import com.atlassian.user.impl.DefaultUser;
import com.atlassian.user.security.password.Credential;
import org.lzx.confluence.oauth2.confluence.ConfluenceOAuth2Authenticator;
import org.lzx.confluence.oauth2.oauth2.OAuth2ClientProperties;
import org.lzx.confluence.oauth2.oauth2.response.UserInfo;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录成功处理器
 *
 * @author LZx
 * @since 2021/09/10
 */
@Slf4j
public class DefaultAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserAccessor userAccessor;

    private final PermissionManager permissionManager;

    private final ConfluenceOAuth2Authenticator authenticator;

    private final String loginSuccessUrl;

    private final String adminGroupName;

    private final String defaultUserGroupName;

    private final String defaultPassword;

    public DefaultAuthenticationSuccessHandler(
            UserAccessor userAccessor,
            PermissionManager permissionManager,
            ConfluenceOAuth2Authenticator authenticator,
            OAuth2ClientProperties oauth2ClientProperties) {
        this.userAccessor = userAccessor;
        this.permissionManager = permissionManager;
        this.authenticator = authenticator;
        this.loginSuccessUrl = oauth2ClientProperties.getLogin().getSuccessUrl();
        OAuth2ClientProperties.CreateUser createUser = oauth2ClientProperties.getCreateUser();
        this.defaultUserGroupName = createUser.getDefaultUserGroup();
        this.adminGroupName = createUser.getAdminGroup();
        this.defaultPassword = createUser.getDefaultPassword();
    }

    @Override
    public void onSuccess(HttpServletRequest request, HttpServletResponse response, UserInfo userInfo) throws IOException {
        ConfluenceUser user = userAccessor.getUserByName(userInfo.getUsername());
        if (user == null) {
            user = createUser(userInfo);
        }
        try {
            authenticator.authoriseUserAndEstablishSession(request, response, user);
        } catch (Exception ex) {
            throw new AuthenticationException("establish_session_error", ex);
        }
        response.sendRedirect(this.loginSuccessUrl);
    }

    /**
     * 新建用户并赋予默认用户组
     *
     * @param userInfo 授权服务器返回的用户信息
     */
    private ConfluenceUser createUser(UserInfo userInfo) {
        Group defaultUserGroup = userAccessor.getGroup(defaultUserGroupName);
        if (defaultUserGroup == null) {
            throw new AuthenticationException("default_user_group_not_exist");
        }
        ConfluenceUser admin = findAnyAdmin();
        log.info("成功获取一个管理员用于获取权限以设置新用户的用户组");
        // 这种代码其实很丑
        AuthenticatedUserThreadLocal.set(admin);
        try {
            ConfluenceUser newUser = userAccessor.createUser(new DefaultUser(userInfo.getUsername(), userInfo.getName(),
                    userInfo.getEmail()), Credential.unencrypted(defaultPassword));
            log.info("成功创建用户[{}]", newUser.getEmail());
            userAccessor.addMembership(defaultUserGroup, newUser);
            log.info("成功为用户[{}]添加用户组[{}]", newUser.getEmail(), defaultUserGroupName);
            return newUser;
        } finally {
            AuthenticatedUserThreadLocal.reset();
        }
    }

    /**
     * 获取任意一个管理员，以获取后续将新用户添加用户组的权限
     *
     * @return 管理员
     */
    private ConfluenceUser findAnyAdmin() {
        Iterable<ConfluenceUser> confluenceUsers = userAccessor.getMembers(userAccessor.getGroup(adminGroupName));
        for (ConfluenceUser confluenceUser : confluenceUsers) {
            if (permissionManager.isConfluenceAdministrator(confluenceUser)) {
                return confluenceUser;
            }
        }
        throw new AuthenticationException("cannot_find_any_admin");
    }

}
