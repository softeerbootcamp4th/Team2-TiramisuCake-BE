package com.softeer.backend.global.annotation.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 메서드의 transaction commit을 보장하기 위한 클래스
 */
@Component
public class AopForTransaction {

    /**
     * 파라미터로 넘어온 메서드를 새로운 트랜잭션에서 실행하는 메서드
     *
     * @param joinPoint
     * @return 메서드의 반환값
     * @throws Throwable
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
