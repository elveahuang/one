package cc.wdev.platform.system.site.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.site.domain.converter.KeywordConverter;
import cc.wdev.platform.system.site.domain.entity.KeywordEntity;
import cc.wdev.platform.system.site.domain.form.KeywordForm;
import cc.wdev.platform.system.site.domain.request.KeywordCheckRequest;
import cc.wdev.platform.system.site.domain.request.KeywordRequest;
import cc.wdev.platform.system.site.repository.KeywordRepository;
import cc.wdev.platform.system.site.service.KeywordService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.KEYWORD;

/**
 * @author elvea
 * @see KeywordService
 * @see BaseCachingEntityService
 */
@Slf4j
@Service
public class KeywordServiceImpl extends BaseCachingEntityService<KeywordEntity, Long, KeywordRepository> implements KeywordService {

    private final CacheKeyGenerator cacheKeyGenerator = new SimpleTenantCacheKeyGenerator(KEYWORD);

    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * 获取关键字列表
     */
    @Override
    public Page<KeywordEntity> findKeywordList(KeywordRequest request) {
        IPage<KeywordEntity> page = StringUtils.isNotBlank(request.getQ()) ?
            this.lambdaQueryWrapper()
                .eq(KeywordEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
                .like(KeywordEntity::getContent, request.getQ())
                .page(getMyBatisPlusPage(request.getPageable()))
            :
            this.lambdaQueryWrapper()
                .eq(KeywordEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
                .page(getMyBatisPlusPage(request.getPageable()));

        return MyBatisPlusUtils.toSpringDataPage(page);
    }

    /**
     * 保存关键字
     */
    @Override
    public Boolean saveKeyword(KeywordForm form) {

        KeywordEntity entity;
        if (null != form.getId() && form.getId() > 0) {
            entity = this.findById(form.getId());
            ObjectUtils.copyNotNullProperties(form, entity);
        } else {
            entity = KeywordConverter.INSTANCE.formToEntity(form);
        }
        save(entity);

        return true;
    }

    /**
     * 删除关键字
     */
    @Override
    public Boolean deleteKeyword(DeleteRequest request) {
        if (null != request && null != request.getIds()) {
            for (Long id : request.getIds()) {
                softDeleteById(id);
            }
        }
        return true;
    }

    /**
     * 检查关键字是否重复
     */
    @Override
    public Boolean checkKeyword(KeywordCheckRequest request) {
        return !lambdaQueryWrapper()
            .ne(existsById(request.getId()), KeywordEntity::getId, request.getId())
            .eq(KeywordEntity::getContent, request.getContent())
            .eq(KeywordEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .exists();
    }

}
