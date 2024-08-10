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
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.softeer.backend.global.annotation.EventLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        EventLock eventLock = method.getAnnotation(EventLock.class);

        String key = REDISSON_LOCK_PREFIX + SpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), eventLock.key());
        RLock rLock = redissonClient.getLock(key);

        try {
            boolean available = rLock.tryLock(eventLock.waitTime(), eventLock.leaseTime(), eventLock.timeUnit());
            if (!available) {
                log.info("{} is locked", key);
                throw new EventLockException(key);
            }

            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            log.info("Interrupted while waiting for lock, key: {}", key);
            throw new EventLockException(key);
        } finally {
            try {
                rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock, MethodName: {}, key: {}", method.getName(), key);
            }
        }
    }
}
