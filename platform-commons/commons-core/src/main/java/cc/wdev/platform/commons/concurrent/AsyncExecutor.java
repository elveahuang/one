package cc.wdev.platform.commons.concurrent;

import cc.wdev.platform.commons.utils.mdc.MdcContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author elvea
 */
@Slf4j
public abstract class AsyncExecutor {

    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private static final Semaphore semaphore = new Semaphore(10);

    public static void execute(Runnable task) {
        executor.execute(wrap(task));
    }

    public static <T> Future<T> execute(Callable<T> task) {
        return executor.submit(wrap(task));
    }

    public static Runnable wrap(Runnable task) {
        final Map<String, String> contextMap = MDC.getCopyOfContextMap();
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return () -> {
            try {
                semaphore.acquire();

                MdcContext.setAsyncContext(contextMap);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                task.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Task execution interrupted", e);
            } finally {
                semaphore.release();

                SecurityContextHolder.clearContext();
                MdcContext.flush();
            }
        };
    }

    public static <T> Callable<T> wrap(Callable<T> task) {
        final Map<String, String> contextMap = MDC.getCopyOfContextMap();
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return () -> {
            try {
                semaphore.acquire();

                MdcContext.setAsyncContext(contextMap);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                return task.call();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CancellationException("Task submission interrupted");
            } finally {
                semaphore.release();

                SecurityContextHolder.clearContext();
                MdcContext.flush();
            }
        };
    }

    public static void shutdown() {
        executor.shutdown();
    }

}
