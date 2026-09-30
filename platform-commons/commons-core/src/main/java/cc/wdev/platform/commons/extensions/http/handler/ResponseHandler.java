package cc.wdev.platform.commons.extensions.http.handler;

/**
 * @author elvea
 */
public interface ResponseHandler<T> {
    void handle(T t) throws Exception;
}
