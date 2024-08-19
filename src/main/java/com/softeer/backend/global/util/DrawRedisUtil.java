package com.softeer.backend.global.util;

import com.softeer.backend.global.annotation.EventLock;
import com.softeer.backend.global.common.constant.RedisKeyPrefix;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
    public void incrementIntegerValue(String key) {
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

    /**
     * userId가 당첨자 목록에 있으면 등수, 없으면 0 반환
     *
     * @param userId 사용자 아이디
     */
    public int getRankingIfWinner(Integer userId) {
        String drawWinnerKey;
        for (int ranking = 1; ranking < 4; ranking++) {
            drawWinnerKey = RedisKeyPrefix.DRAW_WINNER_LIST_PREFIX.getPrefix() + ranking;
            Set<Integer> drawTempSet = getAllDataAsSet(drawWinnerKey);
            if (drawTempSet.contains(userId)) {
                return ranking;
            }
        }
        return 0;
    }

    @EventLock(key = "DRAW_WINNER_#{#ranking}")
    public boolean isWinner(Integer userId, int ranking, int winnerNum) {
        String drawWinnerKey = RedisKeyPrefix.DRAW_WINNER_LIST_PREFIX.getPrefix() + ranking;
        Set<Integer> drawWinnerSet = getAllDataAsSet(drawWinnerKey);

        // 레디스에서 해당 랭킹에 자리가 있는지 확인
        if (drawWinnerSet.size() < winnerNum) {
            // 자리가 있다면 당첨 성공. 당첨자 리스트에 추가
            setIntegerValueToSet(drawWinnerKey, userId);
            return true;
        } else {
            // 이미 자리가 가득 차서 당첨 실패
            return false;
        }
    }

    @EventLock(key = "DRAW_PARTICIPATION_COUNT")
    public void increaseDrawParticipationCount() {
        incrementIntegerValue(RedisKeyPrefix.DRAW_PARTICIPANT_COUNT_PREFIX.getPrefix());
    }

    // 추첨 참여인원수 조회
    public Integer getDrawParticipationCount() {
        return integerRedisTemplate.opsForValue().get(RedisKeyPrefix.DRAW_PARTICIPANT_COUNT_PREFIX.getPrefix());
    }
}
