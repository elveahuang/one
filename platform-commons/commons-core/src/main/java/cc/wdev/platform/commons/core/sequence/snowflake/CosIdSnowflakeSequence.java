package cc.wdev.platform.commons.core.sequence.snowflake;

import cc.wdev.platform.commons.core.sequence.Sequence;
import lombok.extern.slf4j.Slf4j;
import me.ahoo.cosid.IdGenerator;
import me.ahoo.cosid.runtime.CosIdRuntime;
import me.ahoo.cosid.runtime.RuntimeSpec;

/**
 * @author elvea
 */
@Slf4j
public class CosIdSnowflakeSequence implements Sequence {

    private final IdGenerator generator;

    public CosIdSnowflakeSequence(CosIdRuntime idRuntime) {
        this.generator = idRuntime.generator(RuntimeSpec.DEFAULT_GENERATOR);
    }

    @Override
    public long nextId() {
        return this.generator.generate();
    }

}
