package net.shlab.hogefugapiyo.framework.logging.aspect;

import net.shlab.hogefugapiyo.framework.core.operation.OperationContextHolder;
import net.shlab.hogefugapiyo.framework.core.service.ApplicationService;
import net.shlab.hogefugapiyo.framework.core.service.BaseCommandService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * アプリケーションサービスおよびコマンドサービスの業務トレースログを出力する Aspect。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/02_architecture/logging-policy.md}</li>
 * </ul>
 */
@Aspect
@Component
public class BusinessFlowLoggingAspect {

    static final String BUSINESS_INFO_LOGGER_NAME = "businessInfoLogger";
    static final String OPERATION_ID_MDC_KEY = "operationId";

    private static final Logger BUSINESS_INFO_LOGGER = LoggerFactory.getLogger(BUSINESS_INFO_LOGGER_NAME);

    @Pointcut("target(net.shlab.hogefugapiyo.framework.core.service.ApplicationService)")
    void applicationServiceTarget() {
    }

    @Pointcut("target(net.shlab.hogefugapiyo.framework.core.service.BaseCommandService)")
    void commandServiceTarget() {
    }

    @Pointcut("execution(public * *(..))")
    void anyPublicMethod() {
    }

    @Pointcut("execution(* execute(..))")
    void commandExecuteMethod() {
    }

    @Around("applicationServiceTarget() && anyPublicMethod()")
    public Object logApplicationService(ProceedingJoinPoint joinPoint) throws Throwable {
        var operationContext = OperationContextHolder.get();
        String previousOperationId = MDC.get(OPERATION_ID_MDC_KEY);
        if (operationContext != null) {
            MDC.put(OPERATION_ID_MDC_KEY, operationContext.operationId().toString());
        }

        String serviceName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        BUSINESS_INFO_LOGGER.info("event=application-service-start service={} method={}", serviceName, methodName);
        try {
            Object result = joinPoint.proceed();
            BUSINESS_INFO_LOGGER.info("event=application-service-end service={} method={} outcome=success", serviceName, methodName);
            return result;
        } catch (Throwable throwable) {
            BUSINESS_INFO_LOGGER.info("event=application-service-end service={} method={} outcome=failure", serviceName, methodName);
            throw throwable;
        } finally {
            restoreOperationId(previousOperationId);
        }
    }

    @Around("commandServiceTarget() && commandExecuteMethod()")
    public Object logCommandService(ProceedingJoinPoint joinPoint) throws Throwable {
        String serviceName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        BUSINESS_INFO_LOGGER.info("event=command-service-execute service={} method={}", serviceName, methodName);
        return joinPoint.proceed();
    }

    private void restoreOperationId(String previousOperationId) {
        if (previousOperationId == null) {
            MDC.remove(OPERATION_ID_MDC_KEY);
            return;
        }
        MDC.put(OPERATION_ID_MDC_KEY, previousOperationId);
    }
}
