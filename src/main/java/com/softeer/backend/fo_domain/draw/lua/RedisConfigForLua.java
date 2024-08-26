package com.softeer.backend.fo_domain.draw.lua;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisConfigForLua {
    @Bean
    public RedisScript<Long> isWinnerScript() {
        String script = "local draw_winner_key = KEYS[1] " +
                "local user_id = ARGV[1] " +
                "local winner_num = tonumber(ARGV[2]) " +
                "local winner_set_size = redis.call('SCARD', draw_winner_key) " +
                "if winner_set_size < winner_num then " +
                "    redis.call('SADD', draw_winner_key, user_id) " +
                "    return 1 " +
                "else " +
                "    return 0 " +
                "end";

        return new DefaultRedisScript<>(script, Long.class);
    }

    @Bean
    public RedisScript<Long> checkIfUserInSetScript() {
        String script = "local draw_winner_key = KEYS[1] " +
                "local user_id = ARGV[1] " +
                "local exists = redis.call('SISMEMBER', draw_winner_key, user_id) " +
                "if exists == 1 then " +
                "    return 1 " +
                "else " +
                "    return 0 " +
                "end";

        return new DefaultRedisScript<>(script, Long.class);
    }
}