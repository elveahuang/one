package cc.wdev.platform.commons.extensions.keyword;

import java.util.List;

/**
 * @author elvea
 */
public interface KeywordManager {

    void initialize(List<String> list);

    boolean check(String text);

}
