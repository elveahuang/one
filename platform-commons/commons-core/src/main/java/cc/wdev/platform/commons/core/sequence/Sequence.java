package cc.wdev.platform.commons.core.sequence;

/**
 * @author elvea
 */
public interface Sequence {

    long nextId();

    /**
     * 生成一个ID后做36进制转换并转成大写
     */
    default String generateCode() {
        return generateCode(32);
    }

    /**
     * 生成一个ID后做36进制转换并转成大写
     */
    default String generateCode(int radix) {
        return Long.toString(this.nextId(), radix).toUpperCase();
    }

}
