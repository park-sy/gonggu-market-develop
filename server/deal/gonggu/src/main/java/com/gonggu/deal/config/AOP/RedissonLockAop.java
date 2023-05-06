package com.gonggu.deal.config.AOP;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLockAop {
    String method();
    long waitTime() default 5L;

    long leaseTime() default 1L;
    // 초단위 계산
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
