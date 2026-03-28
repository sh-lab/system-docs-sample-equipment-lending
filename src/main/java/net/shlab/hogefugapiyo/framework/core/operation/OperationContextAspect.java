package net.shlab.hogefugapiyo.framework.core.operation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Application Service 実行時に operation context を設定する Aspect。
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OperationContextAspect {

    @Pointcut("target(net.shlab.hogefugapiyo.framework.core.service.ApplicationService)")
    void applicationServiceTarget() {
    }

    @Pointcut("execution(public * *(..))")
    void anyPublicMethod() {
    }

    @Around("applicationServiceTarget() && anyPublicMethod()")
    public Object applyOperationContext(ProceedingJoinPoint joinPoint) throws Throwable {
        OperationContext currentContext = OperationContextHolder.get();
        if (currentContext != null) {
            return joinPoint.proceed();
        }

        OperationContextHolder.set(OperationContext.create());
        try {
            return joinPoint.proceed();
        } finally {
            OperationContextHolder.clear();
        }
    }
}
