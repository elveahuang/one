package cc.wdev.platform.commons.core.sequence.snowflake;

import cc.wdev.platform.commons.core.sequence.Sequence;
import lombok.extern.slf4j.Slf4j;
import me.ahoo.cosid.runtime.CosIdRuntime;
import me.ahoo.cosid.time.TimeBasedGenerator;

/**
 * @author elvea
 */
@Slf4j
public class CosIdSnowflakeSequence implements Sequence {

    private final CosIdRuntime idRuntime;

    public CosIdSnowflakeSequence(CosIdRuntime idRuntime) {
        this.idRuntime = idRuntime;
    }

    @Override
    public long nextId() {
        return this.idRuntime.get("default", TimeBasedGenerator.class).generate();
    }

}
