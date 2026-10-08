package cc.wdev.platform.system.config.strategy;

import java.util.List;

/**
 * BizType 删除策略接口
 * 用于在删除 BizType 时级联删除相关的子数据
 *
 * @author erden
 */
public interface BizTypeDeleteStrategy {

    /**
     * 获取支持的业务组类型
     *
     * @return 业务组类型代码
     */
    String getSupportedGroupType();

    /**
     * 删除指定业务类型的所有子数据
     *
     * @param bizTypeList 业务类型代码列表
     */
    void deleteChildren(List<String> bizTypeList);

}
