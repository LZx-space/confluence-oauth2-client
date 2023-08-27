package org.lzx.confluence.oauth2.oauth2.response.converter;

import org.lzx.confluence.oauth2.AuthenticationException;
import org.lzx.confluence.oauth2.oauth2.response.UserInfo;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

/**
 * 用户信息转换器
 *
 * @author LZx
 * @since 2021/09/01
 */
public class UserInfoConverter implements Converter<Map<String, Object>, UserInfo> {

    private final String usernameAttribute;

    public UserInfoConverter(String usernameAttribute) {
        this.usernameAttribute = usernameAttribute;
    }

    @Override
    public UserInfo convert(Map<String, Object> userInfoMap) {
        Object username = userInfoMap.get(usernameAttribute);
        if (username == null) {
            throw new AuthenticationException("no_username_attribute");
        }
        return new UserInfo()
                .setUsername(username.toString())
                .setName(userInfoMap.get("name").toString())
                .setEmail(userInfoMap.get("email").toString());
    }

}
