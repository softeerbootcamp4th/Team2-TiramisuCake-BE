package com.softeer.backend.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FcfsRedisUtil {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Integer> integerRedisTemplate;

    public void addToIntegerSet(String key, Integer value) {
        integerRedisTemplate.opsForSet().add(key, value);
    }

    public void addToStringSet(String key, String value) {
        stringRedisTemplate.opsForSet().add(key, value);
    }

    public void addToHash(String key, String field, Integer value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
    }

    public void incrementValue(String key){
        integerRedisTemplate.opsForValue().increment(key);
    }

    public Long getIntegerSetSize(String key) {
        return integerRedisTemplate.opsForSet().size(key);
    }

    public boolean isValueInIntegerSet(String key, Integer value) {
        return Boolean.TRUE.equals(integerRedisTemplate.opsForSet().isMember(key, value));
    }

    public boolean isValueInStringSet(String key, String value) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(key, value));
    }

    public Map<String, Integer> getHashEntries(String key) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        Map<String, Integer> result = new HashMap<>();

        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            String mapKey = (String) entry.getKey();
            Integer mapValue = (Integer) entry.getValue();
            result.put(mapKey, mapValue);
        }

        return result;
    }

    public Integer getValue(String key) {
        return integerRedisTemplate.opsForValue().get(key);
    }

    public void clearIntegerSet(String key) {
        integerRedisTemplate.delete(key);
    }

    public void clearStringSet(String key) {
        stringRedisTemplate.delete(key);
    }

    public void clearHash(String key) {
        stringRedisTemplate.delete(key);
    }

    public void clearValue(String key) {
        stringRedisTemplate.delete(key);
    }



}
