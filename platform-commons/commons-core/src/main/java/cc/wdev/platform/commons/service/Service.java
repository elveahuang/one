package cc.wdev.platform.commons.service;

import cc.wdev.platform.commons.core.sequence.SequenceManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 通用服务接口
 *
 * @author elvea
 */
public interface Service {

    /**
     * 获取当前系统时间
     */
    default LocalDateTime getCurLocalDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前系统时间
     */
    default LocalDate getCurLocalDate() {
        return LocalDate.now();
    }

    default Long generateId() {
        return SequenceManager.getSequence().nextId();
    }

    default String generateIdAsString() {
        return String.valueOf(SequenceManager.getSequence().nextId());
    }

    /**
     * 生成唯一编码
     */
    default String generateCode(String prefix) {
        return String.format("%s%s", prefix, SequenceManager.getSequence().generateCode());
    }

}
