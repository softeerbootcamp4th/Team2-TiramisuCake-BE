package com.softeer.backend.fo_domain.draw.test.lua;

import com.softeer.backend.global.annotation.EventLock;
import com.softeer.backend.global.common.constant.RedisKeyPrefix;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DrawRedisLuaUtil {
    private final RedisTemplate<String, Integer> integerRedisTemplate;
    private final RedisScript<Long> isWinnerScript;
    private final RedisScript<Long> checkIfUserInSetScript;

    // 추첨 당첨자 목록: DRAW_WINNER_LIST_{ranking}, Set<Integer>
    // 추첨 참여자 수:  DRAW_PARTICIPANT_COUNT, Integer

    // ranking의 추첨 당첨자 목록 반환
    public Set<Integer> getAllDataAsSet(String key) {
        return integerRedisTemplate.opsForSet().members(key);
    }

    private Long getIntegerSetSize(String key) {
        return integerRedisTemplate.opsForSet().size(key);
    }

    // ranking의 당첨자 목록 업데이트
    public void setIntegerValueToSet(String key, Integer userId) {
        integerRedisTemplate.opsForSet().add(key, userId);
    }

    /**
     * userId가 당첨자 목록에 있으면 등수, 없으면 0 반환
     *
     * @param userId 사용자 아이디
     */
    public int getRankingIfWinner(Integer userId) {
        for (int ranking = 1; ranking < 4; ranking++) {
            String drawWinnerKey = RedisKeyPrefix.DRAW_WINNER_LIST_PREFIX.getPrefix() + ranking;

            Long result = integerRedisTemplate.execute(
                    checkIfUserInSetScript,
                    List.of(drawWinnerKey), // KEYS
                    userId.toString()       // ARGV
            );

            if (result != null && result == 1) {
                return ranking;
            }
        }
        return 0;
    }

    /**
     * 해당 등수의 자리가 남아있는지 판단하는 메서드
     * <p>
     * 1. redis에서 해당 등수의 자리가 남아있는지 판단한다.
     * 1-1. 자리가 남아있다면 사용자를 당첨자 리스트에 저장하고 true 반환
     * 1-2. 자리가 없다면 false 반환
     */
    // @EventLock(key = "DRAW_WINNER")
    public boolean isWinner(Integer userId, int ranking, int winnerNum) {
        String drawWinnerKey = RedisKeyPrefix.DRAW_WINNER_LIST_PREFIX.getPrefix() + ranking;

        // Lua 스크립트를 실행하여 당첨자를 추가할지 결정
        Long result = integerRedisTemplate.execute(
                isWinnerScript,
                Collections.singletonList(drawWinnerKey), // KEYS
                userId.toString(), String.valueOf(winnerNum) // ARGV
        );

        System.out.println(result == null);

        return result != null && result == 1;
    }

    /**
     * 추첨 참여자 수 증가
     */
    public void increaseDrawParticipationCount() {
        integerRedisTemplate.opsForValue().increment(RedisKeyPrefix.DRAW_PARTICIPANT_COUNT_PREFIX.getPrefix());
    }
}
