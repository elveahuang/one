package cc.wdev.platform.commons.utils;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author elvea
 */
public class CollectionUtilsTests {

    @Test
    public void test() {
        List<String> list = Lists.newArrayList("A", "A");
        Assertions.assertEquals(2, list.size());
        List<String> newList = list.stream().distinct().toList();
        Assertions.assertEquals(1, newList.size());
    }

}
