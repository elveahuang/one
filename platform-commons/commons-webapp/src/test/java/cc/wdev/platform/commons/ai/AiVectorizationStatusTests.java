package cc.wdev.platform.commons.ai;

import cc.wdev.platform.commons.ai.enums.AiVectorizationStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 向量化状态枚举单元测试
 *
 * @author elvea
 */
public class AiVectorizationStatusTests {

    @Test
    public void statusValuesShouldBeDistinct() {
        Assertions.assertNotEquals(AiVectorizationStatus.COMPLETED.getValue(),
            AiVectorizationStatus.FAILED.getValue());
        Assertions.assertEquals(1, AiVectorizationStatus.PENDING.getValue());
        Assertions.assertEquals(2, AiVectorizationStatus.PROCESSING.getValue());
        Assertions.assertEquals(3, AiVectorizationStatus.COMPLETED.getValue());
        Assertions.assertEquals(4, AiVectorizationStatus.FAILED.getValue());
    }

}
