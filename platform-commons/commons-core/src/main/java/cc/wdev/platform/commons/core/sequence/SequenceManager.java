package cc.wdev.platform.commons.core.sequence;

import cc.wdev.platform.commons.core.sequence.snowflake.DefaultSnowflakeSequence;

/**
 * @author elvea
 */
public class SequenceManager {

    private static volatile Sequence globalSequence = new DefaultSnowflakeSequence();

    public static Sequence getSequence() {
        return globalSequence;
    }

    public static void setSequence(Sequence sequence) {
        globalSequence = sequence;
    }

}
