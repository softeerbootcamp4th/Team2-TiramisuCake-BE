package com.softeer.backend.global.common.exception;

import com.softeer.backend.global.common.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 선착순, 추첨을 위한 redisson lock 사용시 발생하는 예외
 */
@Getter
@RequiredArgsConstructor
public class EventLockException extends RuntimeException {
    private final String redissonKeyName;

}
