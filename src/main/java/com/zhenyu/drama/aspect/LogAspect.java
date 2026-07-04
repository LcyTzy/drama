package com.zhenyu.drama.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAspect {
    // 切点：Controller 包下所有方法
    @Pointcut("execution(* com.zhenyu.drama.controller..*.*(..))")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("🚀 [{}] 开始请求: {}.{}", Thread.currentThread().getName(), className, methodName);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            log.error("❌ [{}] 请求异常: {}.{}", Thread.currentThread().getName(), className, methodName);
            throw e;
        }

        long cost = System.currentTimeMillis() - start;
        log.info("✅ [{}] 请求结束: {}.{}，耗时: {}ms", Thread.currentThread().getName(), className, methodName, cost);

        return result;
    }
}
