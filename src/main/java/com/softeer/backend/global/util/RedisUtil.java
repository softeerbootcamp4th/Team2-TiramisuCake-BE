package com.softeer.backend.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate stringRedisTemplate;

    // key 에 해당하는 데이터 얻어오는 메서드
    public String getData(String key) {
        ValueOperations<String, String> valueOperations = getStringStringValueOperations();
        return valueOperations.get(key);
    }

    // key - value 데이터 설정하는 메서드
    public void setData(String key, String value) {
        ValueOperations<String, String> valueOperations = getStringStringValueOperations();
        valueOperations.set(key, value);
    }

    /* key 에 해당하는 데이터 삭제하는 메소드 */
    public void deleteData(String key) {
        this.stringRedisTemplate.delete(key);
    }

    /* key 에 해당하는 데이터 만료기간 설정 메소드 */
    public void setDataExpire(String key, String value, Long duration) {
        ValueOperations<String, String> valueOperations = getStringStringValueOperations();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    private ValueOperations<String, String> getStringStringValueOperations() {
        return this.stringRedisTemplate.opsForValue();
    }
}
