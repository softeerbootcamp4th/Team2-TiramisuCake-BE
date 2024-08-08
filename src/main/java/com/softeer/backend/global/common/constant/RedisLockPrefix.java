package com.softeer.backend.global.common.constant;

import lombok.Getter;

@Getter
public enum RedisLockPrefix {
    FCFS_LOCK_PREFIX("LOCK:FCFS_WINNER_"),
    DRAW_LOCK_PREFIX("LOCK:DRAW_WINNER"),
    DRAW_TEMP_PREFIX("DRAW_TEMP_"),
    FCFS_PARTICIPANT_COUNT_PREFIX("FCFS_PARTICIPANT_COUNT_");

    private final String prefix;

    RedisLockPrefix(String prefix) {
        this.prefix = prefix;
    }
}
