package cc.wdev.platform.commons.utils;


import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author elvea
 */
public abstract class ExceptionUtils {

    public static RuntimeException unchecked(Throwable ex) {
        if (ex instanceof RuntimeException) {
            return (RuntimeException) ex;
        } else {
            return new RuntimeException(ex);
        }
    }

    public static String getStackTraceAsString(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

}
