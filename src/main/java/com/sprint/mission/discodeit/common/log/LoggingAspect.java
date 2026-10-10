package com.sprint.mission.discodeit.common.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @AfterReturning(
            value = "@annotation(MutationLog)",
            returning = "result"
    )
    public void logging(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();

        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Map<String, Object> argMap = IntStream.range(0, Integer.min(parameterNames.length, args.length) - 1)
                .boxed()
                .collect(Collectors.toMap(
                        i -> parameterNames[i],
                        i -> args[i]
                ));

        appendLog(className, methodName, argMap, result);
    }

    private void appendLog(String className, String methodName, Map<String, Object> argMap, Object result) {
        log.info("[END] {}.{}({}): {}",
                className,
                methodName,
                argMap,
                result);
    }
}
