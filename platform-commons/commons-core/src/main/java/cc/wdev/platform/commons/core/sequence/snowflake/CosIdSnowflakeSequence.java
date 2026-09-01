package cc.wdev.platform.commons.core.sequence.snowflake;

import cc.wdev.platform.commons.core.sequence.Sequence;
import lombok.extern.slf4j.Slf4j;
import me.ahoo.cosid.provider.IdGeneratorProvider;

/**
 * @author elvea
 */
@Slf4j
public class CosIdSnowflakeSequence implements Sequence {

    private final IdGeneratorProvider provider;

    public CosIdSnowflakeSequence(IdGeneratorProvider provider) {
        this.provider = provider;
    }

    @Override
    public long nextId() {
        return this.provider.getShare().generate();
    }

}
