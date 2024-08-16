package com.softeer.backend.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 선착순, 추첨 이벤트 시에 동기화를 위한 redis lock 속성을 지정하는 애노테이션
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventLock {
    /**
     * 락의 이름
     */
    String key();

    /**
     * 락의 시간 단위
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 락을 기다리는 시간 (default - 5000ms)
     * 락 획득을 위해 waitTime 만큼 대기한다
     */
    long waitTime() default 5000L;

    /**
     * 락 임대 시간 (default - 100ms)
     * 락을 획득한 이후 leaseTime 이 지나면 락을 해제한다
     */
    long leaseTime() default 100L;
}
