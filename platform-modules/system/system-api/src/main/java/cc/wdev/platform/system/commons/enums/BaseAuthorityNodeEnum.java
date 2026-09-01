package cc.wdev.platform.system.commons.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.system.commons.domain.AuthorityNode;

public interface BaseAuthorityNodeEnum extends BaseEnum<String> {

    AuthorityNode[] getNodes();

    @Override
    default String getDescription() {
        return getValue();
    }

}
