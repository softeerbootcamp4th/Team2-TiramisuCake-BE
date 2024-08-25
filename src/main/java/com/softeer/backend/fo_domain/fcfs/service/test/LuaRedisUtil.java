package com.softeer.backend.fo_domain.fcfs.service.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LuaRedisUtil {

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public String executeFcfsScript(String userIdSetKey, String codeSetKey, String codeUserIdHashKey, String participantCountKey, Integer userId, int maxWinners) throws JsonProcessingException {
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

            redis.call('SADD', userIdSetKey, userId)
            local code = redis.call('SPOP', codeSetKey)
            if not code then
                return 'FCFS_CODE_EMPTY'
            end

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
