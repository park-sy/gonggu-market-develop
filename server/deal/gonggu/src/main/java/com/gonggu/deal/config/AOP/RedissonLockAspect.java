package com.gonggu.deal.config.AOP;

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
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RedissonLockAspect {
    private final RedissonClient redissonClient;

    @Around("@annotation(com.gonggu.deal.config.AOP.RedissonLockAop) && args(dealId, ..)")
    public Object lock(ProceedingJoinPoint pjp, Long dealId) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        RedissonLockAop redissonLock = method.getAnnotation(RedissonLockAop.class);
        Long waitTime = redissonLock.waitTime();
        Long leaseTime = redissonLock.leaseTime();
        String key = String.valueOf(dealId);

        RLock lock = redissonClient.getFairLock(key);
        try{
            boolean available = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

            if (!available) {
                log.info("lock 획득 실패");
                return false;
            }
            return pjp.proceed();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(lock != null && lock.isLocked()){
                lock.unlock();
            }
        }

    }
}
