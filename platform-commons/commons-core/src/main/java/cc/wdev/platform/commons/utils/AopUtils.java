package cc.wdev.platform.commons.utils;

import org.aspectj.lang.JoinPoint;

/**
 * @author elvea
 */
public abstract class AopUtils {

    public static String getJoinPointClass(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getName();
    }

    public static String getJoinPointMethod(JoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }

}
