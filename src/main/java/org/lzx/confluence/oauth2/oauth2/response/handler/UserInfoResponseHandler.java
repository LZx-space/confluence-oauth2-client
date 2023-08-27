package org.lzx.confluence.oauth2.oauth2.response.handler;

import org.lzx.confluence.oauth2.util.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.HttpEntity;
import org.apache.http.impl.client.AbstractResponseHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户端点返回信息处理器
 *
 * @author LZx
 * @since 2021/09/01
 */
public class UserInfoResponseHandler extends AbstractResponseHandler<Map<String, Object>> {

    @Override
    public Map<String, Object> handleEntity(HttpEntity httpEntity) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        httpEntity.writeTo(outputStream);
        String json = outputStream.toString(StandardCharsets.UTF_8.toString());
        return JsonUtils.read(json, new TypeReference<HashMap<String, Object>>() {
        });
    }

}
