package org.lzx.confluence.oauth2.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author LZx
 * @since 2018年5月20日
 */
public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    /**
     * 将对象序列化为字符串
     *
     * @param obj 需被序列化的对象
     * @return 字符串
     * @throws JsonProcessingException 异常
     */
    public static String write(Object obj) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }

    /**
     * 将字符串反序列化为对象
     *
     * @param json  字符串
     * @param clazz 对象的类类型
     * @param <T>   类型泛型
     * @return 对象
     * @throws JsonProcessingException 异常
     */
    public static <T> T read(String json, Class<T> clazz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, clazz);
    }

    /**
     * JSON字符串转变为对象，对于Collection或者Map等包含泛型的Object需要指定其元素的具体类型
     *
     * @param json    JSON字符串
     * @param typeRef 转变的目标类型
     * @return 绑定的类的对象
     * @throws JsonMappingException 异常
     */
    public static <T> T read(String json, TypeReference<T> typeRef) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, typeRef);
    }

}
