package com.softeer.backend.fo_domain.fcfs.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LuaRedisUtil {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 선착순 등록을 처리하는 lua script를 실행하는 메서드
     * <p>
     * 1. 선착순에 이미 당첨됐다면 'DUPLICATED' 를 반환한다.
     * 2. 선착순이 이미 마감됐다면 'FCFS_CLOSED'를 반환한다.
     * 3. code값이 redis에 더이상 없다면 'FCFS_CODE_EMPTY'를 반환한다.
     * 4. userId 값을 redis에 저장한다.
     * 5. code 및 userId값을 redis에 hash구조로 저장한다.
     * 6. 선착순 참가자 수를 +1 만큼 증가시킨다.
     * 7. 현재 유저를 마지막으로 마감됐다면 '{code값}_CLOSED' 를 반환한다.
     * 8. 마감되지 않았다면 code값을 반환한다.
     */
    public String executeFcfsScript(String userIdSetKey, String codeSetKey, String codeUserIdHashKey, String participantCountKey, Integer userId, int maxWinners){
        String script = """
            local userIdSetKey = KEYS[1]
            local codeSetKey = KEYS[2]
            local codeUserIdHashKey = KEYS[3]
            local participantCountKey = KEYS[4]

            local userId = ARGV[1]
            local maxWinners = tonumber(ARGV[2])

            if redis.call('SISMEMBER', userIdSetKey, userId) == 1 then
                return 'DUPLICATED'
            end

            local numOfWinners = redis.call('SCARD', userIdSetKey)
            if numOfWinners >= maxWinners then
                return 'FCFS_CLOSED'
            end

            local code = redis.call('SPOP', codeSetKey)
            if not code then
                return 'FCFS_CODE_EMPTY'
            end
       
            redis.call('SADD', userIdSetKey, userId)

            redis.call('HSET', codeUserIdHashKey, code, userId)
            redis.call('INCR', participantCountKey)

            local updatedNumOfWinners = numOfWinners + 1
            local isClosed = (updatedNumOfWinners == maxWinners) and true or false
       
            if isClosed then
                code = code .. "_CLOSED"
            end

            return code
        """;

        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(String.class);

        List<String> keys = Arrays.asList(userIdSetKey, codeSetKey, codeUserIdHashKey, participantCountKey);

        String result =  stringRedisTemplate.execute(redisScript, keys, userId.toString(), String.valueOf(maxWinners));
        log.info("lua result: {}", result);

        return result;
    }
}