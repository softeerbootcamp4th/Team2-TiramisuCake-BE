package com.softeer.backend.global.common.constant;

import lombok.Getter;

@Getter
public enum RedisLockPrefix {
    FCFS_LOCK_PREFIX("LOCK:FCFS_WINNER_"),
    DRAW_LOCK_PREFIX("LOCK:DRAW_WINNER"),
    DRAW_WINNER_LIST_PREFIX("DRAW_WINNER_LIST_"),
    FCFS_PARTICIPANT_COUNT_PREFIX("FCFS_PARTICIPANT_COUNT_"),
    DRAW_PARTICIPANT_COUNT_PREFIX("DRAW_PARTICIPANT_COUNT_");

    private final String prefix;

    RedisLockPrefix(String prefix) {
        this.prefix = prefix;
    }
}
