package com.softeer.backend.global.annotation.aop;

import com.softeer.backend.global.annotation.EventLock;
import com.softeer.backend.global.common.exception.EventLockException;
import com.softeer.backend.global.util.SpringELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


/**
 * 선착순, 추첨 이벤트 시에 동기화를 위한 redis lock를 설정하는 Aop 클래스
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class EventLockAop {
    // lock의 앞에 붙을 prefix
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    // 메서드의 transaction commit을 보장하기 위한 클래스
    private final AopForTransaction aopForTransaction;

    /**
     * EventLock 어노테이션이 붙은 모든 메서드에서 실행
     * 1. 메서드 정보를 가져온다.
     * 2. EventLock 어노테이션의 정보를 가져온다.
     * 3. prefix와 SpelExpressionParser를 이용해 동적으로 lock을 위한 key 생성
     * 4. 생성한 lock을 이용해 redis lock
     *  4-1. lock을 얻는데 성공했다면 AopForTransaction을 이용해 메서드의 commit 보장
     *  4-2. lock을 얻는데 실패했다면 예외 던지기
     * 5. unlock 실행
     */
    @Around("@annotation(com.softeer.backend.global.annotation.EventLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 메서드 정보 가져오기
        Method method = signature.getMethod();
        // EventLock의 정보 가져오기
        EventLock eventLock = method.getAnnotation(EventLock.class);

        // prefix와 SpelExpressionParser를 이용해 동적으로 lock을 위한 key 생성
        String key = REDISSON_LOCK_PREFIX + SpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), eventLock.key());
        RLock rLock = redissonClient.getLock(key);

        try {
            // 생성한 lock을 이용해 redis lock
            boolean available = rLock.tryLock(eventLock.waitTime(), eventLock.leaseTime(), eventLock.timeUnit());
            if (!available) {
                log.info("{} is locked", key);

                if(key.contains("SCHEDULER"))
                    return null;

                throw new EventLockException(key);
            }

            // AopForTransaction을 이용해 메서드의 commit 보장
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            log.info("Interrupted while waiting for lock, key: {}", key);
            // lock을 얻는데 실패했다면 예외 던지기

            if(key.contains("SCHEDULER"))
                return null;
            throw new EventLockException(key);
        } finally {
            try {
                // unlock 실행
                rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock, MethodName: {}, key: {}", method.getName(), key);
            }
        }
    }
}
