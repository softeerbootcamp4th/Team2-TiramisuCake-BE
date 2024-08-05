package com.softeer.backend.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * 선착순, 추첨 이벤트의 동기화를 위해 사용되는 RedisUtil 클래스
 */
@Component
@RequiredArgsConstructor
public class EventLockRedisUtil {

    private final RedisTemplate<String, Integer> redisTemplate;

    // key 에 해당하는 데이터 얻어오는 메서드
    public Integer getData(String key) {
        return getStringIntegerValueOperations().get(key);
    }

    // key에 해당하는 데이터의 값을 1 더하는 메서드
    // 원자적으로 값을 증가시킨다.
    public void incrementData(String key){
        getStringIntegerValueOperations().increment(key, 1);
    }

    // key - value 데이터 설정하는 메서드
    public void setData(String key, int value) {
        getStringIntegerValueOperations().set(key, value);
    }

    public void deleteData(String key){
        redisTemplate.delete(key);
    }

    private ValueOperations<String, Integer> getStringIntegerValueOperations() {
        return redisTemplate.opsForValue();
    }
}
