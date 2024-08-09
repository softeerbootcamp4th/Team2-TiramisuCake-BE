package com.softeer.backend.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

/**
 * 선착순, 추첨 이벤트의 동기화를 위해 사용되는 RedisUtil 클래스
 */
@Component
@RequiredArgsConstructor
public class EventLockRedisUtil {

    private final RedisTemplate<String, Integer> integerRedisTemplate;

    // key 에 해당하는 데이터 얻어오는 메서드
    public Integer getData(String key) {
        return getStringIntegerValueOperations().get(key);
    }

    // key - value 데이터 설정하는 메서드
    public void setData(String key, int value) {
        getStringIntegerValueOperations().set(key, value);
    }

    // 참가자의 ID를 Set으로 저장하고 관리하는 메서드
    public void addValueToSet(String key, Integer userId) {
        getStringSetIntegerValueOperations().add(key, userId);
    }

    // TTL 설정하는 메서드
    public void setTTL(String key, long seconds) {
        integerRedisTemplate.expire(key, Duration.ofSeconds(seconds));
    }

    // key에 해당하는 데이터의 값을 1 더하는 메서드
    // 원자적으로 값을 증가시킨다.
    public void incrementParticipantCount(String key) {
        getStringIntegerValueOperations().increment(key, 1);
    }

    public void deleteParticipantCount(String key) {
        integerRedisTemplate.delete(key);
    }

    public boolean isParticipantExists(String key, Integer participantId) {
        return Boolean.TRUE.equals(getStringSetIntegerValueOperations().isMember(key, participantId));
    }

    public void removeParticipantId(String key, Integer participantId) {
        getStringSetIntegerValueOperations().remove(key, participantId);
    }

    public Set<Integer> getAllDataAsSet(String key) {
        return getStringSetIntegerValueOperations().members(key);
    }

    public void deleteTempWinnerList(String key) {
        integerRedisTemplate.delete(key);
    }

    public void deleteParticipantIds(String key) {
        integerRedisTemplate.delete(key);
    }

    private ValueOperations<String, Integer> getStringIntegerValueOperations() {
        return integerRedisTemplate.opsForValue();
    }

    private SetOperations<String, Integer> getStringSetIntegerValueOperations() {
        return integerRedisTemplate.opsForSet();
    }
}
