package org.lzx.confluence.oauth2.oauth2.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author LZx
 * @since 2021/09/01
 */
@Data
@Accessors(chain = true)
public class UserInfo implements Serializable {

    private String name;

    private String username;

    private String email;

}
