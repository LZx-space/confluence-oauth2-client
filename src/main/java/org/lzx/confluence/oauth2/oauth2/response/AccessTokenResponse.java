package org.lzx.confluence.oauth2.oauth2.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 令牌端口响应
 *
 * @author LZx
 * @since 2021/09/01
 */
@Data
public class AccessTokenResponse implements Serializable {

    @JsonProperty("access_token")
    private String accessToken;

}
