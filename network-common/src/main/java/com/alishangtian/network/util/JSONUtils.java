package com.alishangtian.network.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

@Slf4j
public class JSONUtils {

    private static ObjectMapper objectMapper = createMapper();

    public final static Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    private JSONUtils() {
    }

    public static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    public static String toJSONString(Object obj) {
        return toJSONString(obj, objectMapper);
    }

    public static String toJSONString(Object obj, ObjectMapper objectMapper) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JSONUtils toJSONString failed ! obj :" + obj.toString(), e);
            throw new RuntimeException("json parse error");
        }
    }

    public static String toJSONStringPretty(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JSONUtils toJSONStringPretty failed ! obj :" + obj.toString(), e);
        }
        return null;
    }

    public static <T> T parseObject(final byte[] array, Class<T> clazz) {
        return parseObject(array, clazz, objectMapper);
    }

    public static <T> T parseObject(final byte[] array, Class<T> clazz, ObjectMapper objectMapper) {
        if (null == array || array.length == 0) {
            return null;
        }
        try {
            return objectMapper.readValue(array, clazz);
        } catch (Exception e) {
            log.error("JSONUtils parseObject failed", e);
        }
        return null;
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        return parseObject(json, clazz, objectMapper);
    }

    public static <T> T parseObject(String json, Class<T> clazz, ObjectMapper objectMapper) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("JSONUtils parseObject failed ! json :" + json, e);
        }
        return null;
    }

    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        return parseObject(json, typeReference, objectMapper);
    }

    public static <T> T parseObject(String json, TypeReference<T> typeReference, ObjectMapper objectMapper) {
        if (StringUtils.isEmpty(json) || typeReference == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            log.error("JSONUtils parseObject failed ! json :" + json, e);
        }
        return null;
    }

    public static <T> T parseObject(byte[] bytes, TypeReference<T> typeReference) {
        return parseObject(bytes, typeReference, objectMapper);
    }

    public static <T> T parseObject(byte[] bytes, TypeReference<T> typeReference, ObjectMapper objectMapper) {
        if (null == bytes || null == typeReference || null == objectMapper) {
            return null;
        }
        try {
            return objectMapper.readValue(bytes, typeReference);
        } catch (Exception e) {
            log.error("JSONUtils parseObject failed ! bytes :" + bytes, e);
        }
        return null;
    }
}