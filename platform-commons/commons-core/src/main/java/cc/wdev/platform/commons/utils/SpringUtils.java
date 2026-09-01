package cc.wdev.platform.commons.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author elvea
 */
@Slf4j
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        Assert.notNull(applicationContext, "applicationContext must not be null.");
        SpringUtils.applicationContext = applicationContext;
    }

    /**
     * 获取实例
     *
     * @param name 名称
     * @return Object
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 获取实例
     *
     * @param name         名称
     * @param requiredType 类型
     * @param <T>          T
     * @return T
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    /**
     * 获取实例
     *
     * @param clazz T
     * @param <T>   T
     * @return T
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 获取实例
     *
     * @param clazz T
     * @param <T>   T
     * @return T
     */
    public static <T> ObjectProvider<T> getBeanProvider(Class<T> clazz) {
        return applicationContext.getBeanProvider(clazz);
    }

    /**
     * 检查是否包含该类
     *
     * @param name String
     * @return boolean
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * 检查是否是单例
     *
     * @param name String
     * @return boolean
     */
    public static boolean isSingleton(String name) {
        return applicationContext.isSingleton(name);
    }

    /**
     * 获取类型
     *
     * @param name String
     * @return Class<?>
     */
    public static Class<?> getType(String name) {
        return applicationContext.getType(name);
    }

    /**
     * 获取HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attributes.getRequest();
    }

    /**
     * 获取HttpSession
     */
    public static HttpSession getSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attributes.getRequest().getSession();
    }

    /**
     * 发布事件
     */
    public static ApplicationContext publishEvent(Object event) {
        applicationContext.publishEvent(event);
        return applicationContext;
    }

    /**
     * 编程式事务
     */
    public static <R> R executeWithResult(Function<Object, R> function) {
        return executeWithResult(getBean(PlatformTransactionManager.class), function);
    }

    /**
     * 编程式事务
     */
    public static <R> R executeWithResult(PlatformTransactionManager transactionManager, Function<Object, R> function) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return transactionTemplate.execute(status -> {
            try {
                return function.apply(new Object());
            } catch (Exception e) {
                status.setRollbackOnly();
                return null;
            }
        });
    }

    /**
     * 编程式事务
     */
    public static void executeWithoutResult(Consumer<Object> consumer) {
        executeWithoutResult(getBean(PlatformTransactionManager.class), consumer);
    }

    /**
     * 编程式事务
     */
    public static void executeWithoutResult(PlatformTransactionManager transactionManager, Consumer<Object> consumer) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.executeWithoutResult(status -> {
            try {
                consumer.accept(new Object());
            } catch (Exception e) {
                status.setRollbackOnly();
            }
        });
    }

}
