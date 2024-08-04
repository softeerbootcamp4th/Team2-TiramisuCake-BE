package com.softeer.backend.global.common.constant;

import lombok.Getter;

@Getter
public enum RedisLockPrefix {
    FCFS_LOCK_PREFIX("LOCK:FCFS_"),
    DRAW_LOCK_PREFIX("LOCK:DRAW_");

    private final String prefix;

    RedisLockPrefix(String prefix) {
        this.prefix = prefix;
    }
}
