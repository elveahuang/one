package cc.wdev.platform.system.site.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.site.domain.entity.KeywordEntity;
import cc.wdev.platform.system.site.domain.form.KeywordForm;
import cc.wdev.platform.system.site.domain.request.KeywordCheckRequest;
import cc.wdev.platform.system.site.domain.request.KeywordRequest;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface KeywordService extends CachingEntityService<KeywordEntity, Long> {

    /**
     * 获取关键字列表
     */
    Page<KeywordEntity> findKeywordList(KeywordRequest request);

    /**
     * 保存关键字
     */
    Boolean saveKeyword(KeywordForm form);

    /**
     * 删除关键字
     */
    Boolean deleteKeyword(DeleteRequest request);

    /**
     * 检查关键字是否重复
     */
    Boolean checkKeyword(KeywordCheckRequest request);

    /**
     * 获取所有关键字
     */
    List<KeywordEntity> findAll();

}
