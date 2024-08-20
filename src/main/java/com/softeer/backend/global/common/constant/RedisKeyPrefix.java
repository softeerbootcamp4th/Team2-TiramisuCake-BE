package com.softeer.backend.global.common.constant;

import lombok.Getter;

@Getter
public enum RedisKeyPrefix {
    // 선착순
    FCFS_USERID_PREFIX("FCFS_WINNER_"),
    FCFS_CODE_PREFIX("FCFS_CODE_"),
    FCFS_CODE_USERID_PREFIX("FCFS_CODE_USERID_"),
    FCFS_PARTICIPANT_COUNT_PREFIX("FCFS_PARTICIPANT_COUNT"),

    // 추첨
    DRAW_LOCK_PREFIX("LOCK:DRAW_WINNER"),
    DRAW_WINNER_LIST_PREFIX("LOCK:DRAW_WINNER_LIST_"),
    DRAW_PARTICIPANT_COUNT_PREFIX("DRAW_PARTICIPANT_COUNT"),

    // 사이트 방문자 수
    TOTAL_VISITORS_COUNT_PREFIX("TOTAL_VISITORS_COUNT_");


    private final String prefix;

    RedisKeyPrefix(String prefix) {
        this.prefix = prefix;
    }
}
