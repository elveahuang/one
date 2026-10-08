package cc.wdev.platform.system.tag.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.domain.request.SequenceRequest;
import cc.wdev.platform.system.commons.domain.vo.SequenceVo;
import cc.wdev.platform.system.tag.domain.entity.TagSequenceEntity;
import cc.wdev.platform.system.tag.repository.TagSequenceRepository;
import cc.wdev.platform.system.tag.service.TagSequenceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author erden
 */
@Slf4j
@Service
@AllArgsConstructor
public class TagSequenceServiceImpl extends BaseEntityService<TagSequenceEntity, Long, TagSequenceRepository> implements TagSequenceService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSequence(SequenceRequest request) {
        if (!ObjectUtils.isValidId(request.getBizId()) || StringUtils.isBlank(request.getBizType())) {
            throw new ServiceException(ResponseCodeEnum.PARAM_ERROR);
        }

        // 删除旧的排序
        this.deleteSequence(request);
        List<TagSequenceEntity> entityList = new ArrayList<>(request.getSequence().size());
        for (Map.Entry<Long, Integer> entry : request.getSequence().entrySet()) {
            TagSequenceEntity entity = TagSequenceEntity.builder()
                .bizType(request.getBizType())
                .bizId(request.getBizId())
                .tagId(entry.getKey())
                .idx(entry.getValue())
                .build();
            entityList.add(entity);
        }
        this.saveBatch(entityList);
        log.info("TagSequence Save success. bizType={}, bizId={}, size={}", request.getBizType(), request.getBizId(), entityList.size());
    }

    @Override
    public SequenceVo getSequence(SequenceRequest request) {
        List<TagSequenceEntity> list = findSequence(request);
        Map<Long, Integer> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (TagSequenceEntity e : list) {
                map.put(e.getTagId(), e.getIdx());
            }
        }
        return SequenceVo.builder()
            .bizType(request.getBizType())
            .bizId(request.getBizId())
            .sequence(map)
            .build();
    }

    @Override
    public List<TagSequenceEntity> findSequence(SequenceRequest request) {
        return lambdaQueryWrapper()
            .eq(TagSequenceEntity::getBizType, request.getBizType())
            .eq(TagSequenceEntity::getBizId, request.getBizId())
            .list();
    }

    @Override
    public void deleteByTagId(Long tagId) {
        lambdaUpdateWrapper()
            .eq(TagSequenceEntity::getTagId, tagId)
            .remove();
    }

    private void deleteSequence(SequenceRequest request) {
        lambdaUpdateWrapper()
            .eq(TagSequenceEntity::getBizType, request.getBizType())
            .eq(TagSequenceEntity::getBizId, request.getBizId())
            .remove();
    }

}
