package cc.wdev.platform.system.config.api;

import cc.wdev.platform.commons.enums.BaseBizTypeEnum;
import cc.wdev.platform.system.commons.domain.vo.SimpleOptionVo;
import cc.wdev.platform.system.config.domain.request.BizTypeDeleteRequest;
import cc.wdev.platform.system.config.domain.request.BizTypeSaveRequest;
import cc.wdev.platform.system.config.domain.request.BizTypeSearchRequest;
import cc.wdev.platform.system.config.domain.vo.BizTypeVo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface BizTypeApi {

    /**
     * 初始化所有业务类型
     */
    void initialize();

    /**
     * 分页获取分组下所有的业务类型
     */
    <E> Page<BizTypeVo<E>> findBizTypePage(BizTypeSearchRequest request);

    /**
     * 分页获取分组下所有的业务类型
     */
    <E> List<BizTypeVo<E>> findBizTypeList(BizTypeSearchRequest request);

    /**
     * 分页获取分组下所有的业务类型
     */
    List<SimpleOptionVo> findBizTypeVoList(BizTypeSearchRequest request);

    /**
     * 根据业务分组和编码获取业务类型
     */
    default <T, E extends BaseBizTypeEnum> BizTypeVo<T> getBizType(E bizTypeEnum) {
        return this.getBizType(bizTypeEnum, null);
    }

    /**
     * 根据业务分组和编码获取业务类型
     */
    default <T, E extends BaseBizTypeEnum> BizTypeVo<T> getBizType(E bizType, Class<T> configClass) {
        return this.getBizType(bizType, configClass, null);
    }

    /**
     * 根据业务分组和编码获取业务类型
     */
    <T, E extends BaseBizTypeEnum> BizTypeVo<T> getBizType(E bizType, Class<T> configClass, T defaultConfig);

    /**
     * 根据业务分组和编码获取业务类型
     */
    default <T> BizTypeVo<T> getBizType(String bizTypeGroup, String bizType) {
        return this.getBizType(bizTypeGroup, bizType, null);
    }

    /**
     * 根据业务分组和编码获取业务类型
     */
    default <T> BizTypeVo<T> getBizType(String bizTypeGroup, String bizType, Class<T> configClass) {
        return this.getBizType(bizTypeGroup, bizType, configClass, null);
    }

    /**
     * 根据业务分组和编码获取业务类型
     */
    <T> BizTypeVo<T> getBizType(String bizTypeGroup, String bizType, Class<T> configClass, T defaultConfig);

    /**
     * 保存业务类型
     */
    <T> void saveBizType(BizTypeSaveRequest<T> request);

    /**
     * 删除业务类型
     */
    void deleteBizType(BizTypeDeleteRequest request);

}
