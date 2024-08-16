package com.softeer.backend.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 추첨 이벤트에서 사용할 레디스
 */
@Component
@RequiredArgsConstructor
public class DrawRedisUtil {
    private final RedisTemplate<String, Integer> integerRedisTemplate;

    // 추첨 당첨자 목록: DRAW_WINNER_LIST_{ranking}, Set<Integer>
    // 추첨 참여자 수:  DRAW_PARTICIPANT_COUNT, Integer

    // 추첨 참여자 수 증가
    public void increaseIntegerValue(String key) {
        integerRedisTemplate.opsForValue().increment(key);
    }

    // ranking의 추첨 당첨자 목록 반환
    public Set<Integer> getAllDataAsSet(String key) {
        return integerRedisTemplate.opsForSet().members(key);
    }

    // ranking의 당첨자 목록 업데이트
    public void setIntegerValueToSet(String key, Integer userId) {
        integerRedisTemplate.opsForSet().add(key, userId);
    }

    // ranking의 Set 값 모두 삭제
    public void deleteAllSetData(String key) {
        integerRedisTemplate.delete(key);
    }

    // 참여자 수 삭제
    public void deleteIntegerValue(String key) {
        integerRedisTemplate.delete(key);
    }
}
