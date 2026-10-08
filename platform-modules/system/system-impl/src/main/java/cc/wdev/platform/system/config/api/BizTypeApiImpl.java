package cc.wdev.platform.system.config.api;

import cc.wdev.platform.commons.enums.BaseBizTypeEnum;
import cc.wdev.platform.commons.utils.ClassUtils;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.JacksonUtils;
import cc.wdev.platform.system.commons.domain.vo.SimpleOptionVo;
import cc.wdev.platform.system.config.domain.entity.BizTypeEntity;
import cc.wdev.platform.system.config.domain.request.BizTypeDeleteRequest;
import cc.wdev.platform.system.config.domain.request.BizTypeSaveRequest;
import cc.wdev.platform.system.config.domain.request.BizTypeSearchRequest;
import cc.wdev.platform.system.config.domain.vo.BizTypeVo;
import cc.wdev.platform.system.config.service.BizTypeService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemConstants.GLOABL_BASE_PACKAGE;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class BizTypeApiImpl implements BizTypeApi {

    private final BizTypeService bizTypeService;

    /**
     * @see BizTypeApi#initialize()
     */
    @Override
    @SneakyThrows
    public void initialize() {
        List<BaseBizTypeEnum> bizTypeEnumList = ClassUtils.getEnumClass(GLOABL_BASE_PACKAGE, BaseBizTypeEnum.class);

        if (CollectionUtils.isNotEmpty(bizTypeEnumList)) {
            List<BizTypeEntity> updateEntityList = Lists.newArrayList();
            List<BizTypeEntity> insertEntityList = Lists.newArrayList();
            for (BaseBizTypeEnum bizTypeEnum : bizTypeEnumList) {
                BizTypeEntity bizTypeEntity = this.bizTypeService.getBizTypeEntity(bizTypeEnum.getGroup(), bizTypeEnum.getCode());
                if (bizTypeEntity != null) {
                    bizTypeEntity.setUpdatedAt(LocalDateTime.now());
                    updateEntityList.add(bizTypeEntity);
                } else {
                    bizTypeEntity = new BizTypeEntity();
                    bizTypeEntity.setCreatedAt(LocalDateTime.now());
                    bizTypeEntity.setUpdatedAt(LocalDateTime.now());
                    insertEntityList.add(bizTypeEntity);
                }
                bizTypeEntity.setActive(1);
                bizTypeEntity.setBizGroupType(bizTypeEnum.getGroup());
                bizTypeEntity.setBizScopeType(bizTypeEnum.getScope());
                bizTypeEntity.setBizType(bizTypeEnum.getCode());
                bizTypeEntity.setLabelKey(bizTypeEnum.getLabelKey());
                bizTypeEntity.setLabelGroup(bizTypeEnum.getLabelGroup());
                bizTypeEntity.setTitle(bizTypeEnum.getDescription());
                bizTypeEntity.setDescription(bizTypeEnum.getDescription());
                if (bizTypeEnum.getConfig() != null) {
                    bizTypeEntity.setExtra(JacksonUtils.toJson(bizTypeEnum.getConfig()));
                    bizTypeEntity.setDefaultConfig(JacksonUtils.toJson(bizTypeEnum.getConfig()));
                }
            }
            this.bizTypeService.updateBatchById(updateEntityList);
            this.bizTypeService.insertBatch(insertEntityList);
        }
    }

    /**
     * @see BizTypeApi#findBizTypePage(BizTypeSearchRequest)
     */
    @Override
    public <E> Page<BizTypeVo<E>> findBizTypePage(BizTypeSearchRequest request) {
        return bizTypeService.findBizTypePage(request);
    }

    /**
     * @see BizTypeApi#findBizTypeList(BizTypeSearchRequest)
     */
    @Override
    public <E> List<BizTypeVo<E>> findBizTypeList(BizTypeSearchRequest request) {
        return bizTypeService.findBizTypeList(request);
    }

    /**
     * @see BizTypeApi#findBizTypeVoList(BizTypeSearchRequest)
     */
    @Override
    public List<SimpleOptionVo> findBizTypeVoList(BizTypeSearchRequest request) {
        List<BizTypeVo<Object>> bizTypeList = findBizTypeList(request);
        return bizTypeList.stream().map((item) -> SimpleOptionVo.builder()
            .value(item.getBizType())
            .title(item.getTitle())
            .label(item.getLabelKey())
            .labelKey(item.getLabelKey())
            .labelGroup(item.getLabelGroup())
            .label(item.getLabelKey())
            .build()
        ).toList();
    }

    /**
     * @see BizTypeApi#getBizType(BaseBizTypeEnum, Class, Object)
     */
    @Override
    public <T, E extends BaseBizTypeEnum> BizTypeVo<T> getBizType(E bizType, Class<T> configClass, T defaultConfig) {
        return this.bizTypeService.getBizType(bizType, configClass, defaultConfig);
    }

    /**
     * @see BizTypeApi#getBizType(String, String, Class, Object)
     */
    @Override
    public <T> BizTypeVo<T> getBizType(String bizTypeGroup, String bizType, Class<T> configClass, T defaultConfig) {
        return this.bizTypeService.getBizType(bizTypeGroup, bizType, configClass, defaultConfig);
    }

    /**
     * @see BizTypeApi#saveBizType(BizTypeSaveRequest)
     */
    @Override
    public <T> void saveBizType(BizTypeSaveRequest<T> request) {
        this.bizTypeService.saveBizType(request);
    }

    /**
     * @see BizTypeApi#deleteBizType(BizTypeDeleteRequest)
     */
    @Override
    public void deleteBizType(BizTypeDeleteRequest request) {
        this.bizTypeService.deleteBizType(request);
    }

}
