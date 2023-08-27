package org.lzx.confluence.oauth2.oauth2.response.handler;

import org.lzx.confluence.oauth2.oauth2.response.AccessTokenResponse;
import org.lzx.confluence.oauth2.util.JsonUtils;
import org.apache.http.HttpEntity;
import org.apache.http.impl.client.AbstractResponseHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 令牌端点响应转换器
 *
 * @author LZx
 * @since 2021/09/01
 */
public class AccessTokenResponseHandler extends AbstractResponseHandler<AccessTokenResponse> {

    @Override
    public AccessTokenResponse handleEntity(HttpEntity httpEntity) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        httpEntity.writeTo(outputStream);
        String json = outputStream.toString(StandardCharsets.UTF_8.toString());
        return JsonUtils.read(json, AccessTokenResponse.class);
    }

}
