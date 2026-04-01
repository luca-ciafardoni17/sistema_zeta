package com.platformzeta.storage.audit;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class AfterThrowingExceptionLogging {

    @AfterThrowing(
            pointcut = "within(@org.springframework.stereotype.Service *) || within(@org.springframework.web.bind.annotation.RestController *)",
            throwing = "ex"
    )
    public void logAfterException(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] methodArgs = joinPoint.getArgs();
        log.error("> Exception occurred in method: {}, Arguments: {}, Exception type: {}, Exception message: {}", methodName, Arrays.toString(methodArgs), ex.getClass().getSimpleName(), ex.getMessage());
    }

}